package com.capstone.bgjobs.service;

import com.capstone.bgjobs.dto.TicketDTO;
import com.capstone.bgjobs.model.Tenant;
import com.capstone.bgjobs.model.TenantTicket;
import com.capstone.bgjobs.repository.TenantRepository;
import com.capstone.bgjobs.repository.TenantTicketRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JiraTicketService {

    private final TenantRepository tenantRepository;
    private final TenantTicketRepository tenantTicketRepository;
    private final ElasticSearchService elasticSearchService;

    public JiraTicketService(
            TenantRepository tenantRepository,
            TenantTicketRepository tenantTicketRepository,
            ElasticSearchService elasticSearchService
    ) {
        this.tenantRepository = tenantRepository;
        this.tenantTicketRepository = tenantTicketRepository;
        this.elasticSearchService = elasticSearchService;
    }

    // -----------------------------------------------------------------------------------
    //                                CREATE A TICKET
    // -----------------------------------------------------------------------------------
    public String createTicket(Long tenantId, String findingId, String summary, String description) {
        // 1) Fetch tenant details
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        String accountUrl   = tenant.getAccountUrl();    // e.g. "https://<your-domain>.atlassian.net"
        String jiraUsername = tenant.getJiraUsername();
        String apiToken     = tenant.getApiToken();
        String projectKey   = tenant.getProjectKey();    // e.g. "CRM"

        // 2) Prepare the URL: {accountUrl}/rest/api/2/issue
        String createIssueUrl = "https://" + accountUrl + "/rest/api/2/issue";

        // 3) Build the request body
        Map<String, Object> fields = new HashMap<>();
        Map<String, String> projectMap = new HashMap<>();
        projectMap.put("key", projectKey);

        Map<String, String> issueTypeMap = new HashMap<>();
        issueTypeMap.put("name", "Bug");  // or "Task", etc.

        fields.put("project", projectMap);
        fields.put("summary", summary);
        fields.put("description", description);
        fields.put("issuetype", issueTypeMap);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fields", fields);

        // 4) Build Basic Auth header
        String basicAuthHeader = buildBasicAuthHeader(jiraUsername, apiToken);

        // 5) Use WebClient to POST
        Map<String, Object> responseBody =
            WebClient.builder().build()
                .post()
                .uri(createIssueUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", basicAuthHeader)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody ->
                            Mono.error(new RuntimeException("Failed to create Jira issue: " + errorBody))
                        )
                )
                .bodyToMono(Map.class)
                .block(); // blocking call in a synchronous service

        if (responseBody == null) {
            throw new RuntimeException("Jira createIssue API returned no response body");
        }

        // Usually Jira responds with something like:
        // { "id": "10011", "key": "CRM-11", "self": "https://..." }
        String newTicketKey = (String) responseBody.get("key");
        if (newTicketKey == null) {
            throw new RuntimeException("Unable to parse 'key' from Jira create-issue response.");
        }

        // 6) Update the ES finding with the new ticket ID
        try {
            elasticSearchService.updateFindingTicketId(findingId, newTicketKey, tenantId);
        } catch (Exception e) {
            throw new RuntimeException("Error updating ES with new Jira ticket ID: " + e.getMessage(), e);
        }

        // 7) Create a record in tenant_ticket table
        TenantTicket tenantTicket = new TenantTicket(tenant, newTicketKey, findingId);
        tenantTicketRepository.save(tenantTicket);

        return newTicketKey;
    }

    // -----------------------------------------------------------------------------------
    //                       GET ALL TICKETS FOR A TENANT
    // -----------------------------------------------------------------------------------
    public List<TicketDTO> getAllTenantTickets(Long tenantId) {
        // 1) Fetch all TenantTicket records for tenant
        List<TenantTicket> tenantTickets = tenantTicketRepository.findAllByTenantId(tenantId);
        if (tenantTickets.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) Get tenant credentials for Jira calls
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        String accountUrl   = tenant.getAccountUrl();
        String jiraUsername = tenant.getJiraUsername();
        String apiToken     = tenant.getApiToken();

        // 3) For each TicketId, fetch from Jira
        List<TicketDTO> result = new ArrayList<>();
        for (TenantTicket tt : tenantTickets) {
            try {
                TicketDTO dto = fetchTicketDetails(accountUrl, jiraUsername, apiToken, tt.getTicketId());
                if (dto != null) {
                    result.add(dto);
                }
            } catch (Exception e) {
                // Log or handle the error; continue for other tickets
                e.printStackTrace();
            }
        }

        return result;
    }

    // Helper for GET /rest/api/2/issue/{ticketId}
    private TicketDTO fetchTicketDetails(String accountUrl, String username, String apiToken, String ticketId) {
        String url = "https://" + accountUrl + "/rest/api/2/issue/" + ticketId;

        String basicAuthHeader = buildBasicAuthHeader(username, apiToken);

        Map<String, Object> responseBody =
            WebClient.builder().build()
                .get()
                .uri(url)
                .header("Authorization", basicAuthHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody ->
                            Mono.error(new RuntimeException("Failed to fetch Jira issue: " + errorBody))
                        )
                )
                .bodyToMono(Map.class)
                .block();

        if (responseBody == null) {
            return null;
        }

        // e.g. { "id": "10011", "key": "CRM-11", "fields": { ... } }
        String jiraKey = (String) responseBody.get("key");
        Map<String, Object> fields = (Map<String, Object>) responseBody.get("fields");
        if (fields == null) {
            return null;
        }

        // parse needed fields
        Map<String, Object> issueType = (Map<String, Object>) fields.get("issuetype");
        Map<String, Object> status    = (Map<String, Object>) fields.get("status");

        String issueTypeName        = (issueType != null) ? (String) issueType.get("name") : null;
        String issueTypeDescription = (issueType != null) ? (String) issueType.get("description") : null;
        String summary              = (String) fields.get("summary");
        String statusName           = (status != null) ? (String) status.get("name") : null;

        // Return your custom DTO
        TicketDTO dto = new TicketDTO();
        dto.setTicketId(jiraKey);
        dto.setIssueTypeName(issueTypeName);
        dto.setIssueTypeDescription(issueTypeDescription);
        dto.setSummary(summary);
        dto.setStatusName(statusName);

        return dto;
    }

    // -----------------------------------------------------------------------------------
    //                     TRANSITION TICKET TO DONE (LINEAR)
    // -----------------------------------------------------------------------------------
    public void transitionTicketToDone(Long tenantId, String ticketId) {
        try {
            Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

            String accountUrl = tenant.getAccountUrl();
            String jiraUsername = tenant.getJiraUsername();
            String apiToken = tenant.getApiToken();

            String transitionsUrl = "https://" + accountUrl 
                + "/rest/api/2/issue/" + ticketId + "/transitions?expand=transitions.fields";
            String basicAuthHeader = buildBasicAuthHeader(jiraUsername, apiToken);

            while (true) {
                Map<String, Object> transitionsBody = WebClient.builder().build()
                    .get()
                    .uri(transitionsUrl)
                    .header("Authorization", basicAuthHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody ->
                                Mono.error(new RuntimeException("Failed to fetch transitions for ticket " 
                                                                + ticketId + ": " + errorBody))
                            )
                    )
                    .bodyToMono(Map.class)
                    .block();

                if (transitionsBody == null) {
                    throw new RuntimeException("No response received for transitions on ticket " + ticketId);
                }

                List<Map<String, Object>> transitions = (List<Map<String, Object>>) transitionsBody.get("transitions");
                if (transitions == null || transitions.isEmpty()) {
                    break;
                }

                Map<String, Object> singleTransition = transitions.get(0);
                String transitionId = (String) singleTransition.get("id");
                if (transitionId == null) {
                    throw new RuntimeException("Unable to parse transition ID for ticket " + ticketId);
                }

                Map<String, Object> transitionRequest = new HashMap<>();
                Map<String, String> transitionIdMap = new HashMap<>();
                transitionIdMap.put("id", transitionId);
                transitionRequest.put("transition", transitionIdMap);

                WebClient.builder().build()
                    .post()
                    .uri(transitionsUrl)
                    .header("Authorization", basicAuthHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(transitionRequest)
                    .retrieve()
                    .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody ->
                                Mono.error(new RuntimeException("Error transitioning ticket " 
                                                                + ticketId + ": " + errorBody))
                            )
                    )
                    .bodyToMono(Void.class)
                    .block();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // -----------------------------------------------------------------------------------
    //                            HELPER: BASIC AUTH
    // -----------------------------------------------------------------------------------
    private String buildBasicAuthHeader(String username, String apiToken) {
        // Basic <base64(username:apiToken)>
        String auth = username + ":" + apiToken;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
    }


    public TicketDTO getTicketById(Long tenantId, String ticketId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
    
        // 1) Lookup the TenantTicket record for this (tenantId, ticketId)
        //    so we can fetch esFindingId
        TenantTicket tenantTicket = tenantTicketRepository.findAllByTenantId(tenantId).stream()
            .filter(tt -> tt.getTicketId().equalsIgnoreCase(ticketId))
            .findFirst()
            .orElse(null);
    
        if (tenantTicket == null) {
            return null;
        }
    
        // 2) Reuse your existing method to get details from Jira
        TicketDTO dto = fetchTicketDetails(
            tenant.getAccountUrl(),
            tenant.getJiraUsername(),
            tenant.getApiToken(),
            ticketId
        );
    
        if (dto != null) {
            // Attach the esFindingId from the DB
            dto.setEsFindingId(tenantTicket.getEsFindingId());
        }
    
        return dto;
    }
}
