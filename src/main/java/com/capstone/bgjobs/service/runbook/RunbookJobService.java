package com.capstone.bgjobs.service.runbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capstone.bgjobs.dto.event.job.RunbookTriggerJobEvent;
import com.capstone.bgjobs.dto.event.payload.job.RunbookTriggerJobEventPayload;
import com.capstone.bgjobs.model.Finding;
import com.capstone.bgjobs.model.FindingSeverity;
import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.Tenant;
import com.capstone.bgjobs.model.Tool;
import com.capstone.bgjobs.model.runbook.Runbook;
import com.capstone.bgjobs.model.runbook.RunbookAction;
import com.capstone.bgjobs.model.runbook.RunbookFilter;
import com.capstone.bgjobs.repository.TenantRepository;
import com.capstone.bgjobs.repository.runbook.RunbookRepository;
import com.capstone.bgjobs.service.ElasticSearchService;
import com.capstone.bgjobs.service.JiraTicketService;
import com.capstone.bgjobs.service.github.update.GitHubFindingUpdateService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RunbookJobService {
    private final RunbookRepository runbookRepository;
    private final ElasticSearchService esService;
    private final Map<Tool, GitHubFindingUpdateService> serviceByTool;
    private final TenantRepository tenantRepository;
    private final JiraTicketService jiraService;

    public RunbookJobService(RunbookRepository runbookRepository,
                             ElasticSearchService esService,
                             List<GitHubFindingUpdateService> services,
                             TenantRepository tenantRepository,
                             JiraTicketService jiraService) {
        this.runbookRepository = runbookRepository;
        this.esService = esService;
        this.serviceByTool = services.stream()
                .collect(Collectors.toMap(GitHubFindingUpdateService::getToolType, Function.identity()));
        this.tenantRepository = tenantRepository;
        this.jiraService = jiraService;
    }

    public void handleTriggerJob(RunbookTriggerJobEvent event) {
        RunbookTriggerJobEventPayload payload = event.getPayload();
        List<String> findingIds = payload.getFindingIds();
        Long tenantId = payload.getTenantId();

        // List<Finding> findings = new ArrayList<>();
        System.out.println("FINDING IDS LENGTH " + findingIds.size());
        List<Finding> findings = esService.getFindingsByIds(findingIds, tenantId);
        System.out.println("FINDING LENGTH " + findings.size());
        List<Runbook> runbooks = getRunbooksForTenant(tenantId);
        System.out.println("Runbooks: " + runbooks.toString());
        for (Runbook runbook : runbooks) {
            if (!runbook.isEnabled() || runbook.getTriggers() == null || runbook.getFilters() == null
                    || runbook.getActions() == null) {
                continue;
            }

            if (runbook.getTriggers().stream()
                    .anyMatch(trigger -> trigger.getTriggerType().equals(payload.getTriggerType()))) {
                
                for (RunbookFilter filter : runbook.getFilters()) {
                    System.out.println("Filter severity: " + filter.getSeverity());
                    System.out.println("Filter state: " + filter.getState());
                    FindingState filterState = filter.getState();
                    FindingSeverity filterSeverity = filter.getSeverity();
                    List<Finding> filteredFindings;

                    if (filterState == null && filterSeverity == null) {
                        filteredFindings = findings;
                    } else if (filterState == null) {
                        filteredFindings = findings.stream()
                                .filter(finding -> finding.getSeverity().equals(filterSeverity))
                                .collect(Collectors.toList());
                    } else if (filterSeverity == null) {
                        filteredFindings = findings.stream()
                                .filter(finding -> finding.getState().equals(filterState))
                                .collect(Collectors.toList());
                    } else {
                        filteredFindings = findings.stream()
                                .filter(finding -> finding.getState().equals(filterState)
                                        && finding.getSeverity().equals(filterSeverity))
                                .collect(Collectors.toList());
                    }
                    System.out.println("FILTERED FINDINGS LENGTH: " + filteredFindings.size());
                    if (!filteredFindings.isEmpty()) {
                        for (RunbookAction action : runbook.getActions()) {
                            System.out.println("ACTION: " + action.getToState().toString() + " " + action.isTicketCreate());
                            FindingState toState = action.getToState();

                            for (Finding finding : filteredFindings) {
                                Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
                                if (tenant == null) {
                                    continue;
                                }

                                String findingId = finding.getId();

                                if (toState != null) {
                                    GitHubFindingUpdateService ghService = serviceByTool.get(finding.getToolType());
                                    Map<String, Object> addProps = finding.getToolAdditionalProperties();
                                    Integer alertNumberInteger = (Integer) addProps.get("number");
                                    Long alertNumber = alertNumberInteger.longValue();
                                    String owner = tenant.getOwner();
                                    String repo = tenant.getRepo();
                                    String pat = tenant.getPat();
                                    System.out.println("TRYING TO UPDATE STATE ON GH");
                                    ghService.updateFinding(owner, repo, pat, alertNumber, toState, findingId, tenantId);
                                    System.out.println("UPDATED STATE ON GH");
                                }

                                if (action.isTicketCreate()) {
                                    String summary = finding.getTitle();
                                    String description = finding.getDesc();
                                    jiraService.createTicket(tenantId, findingId, summary, description);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Runbook> getRunbooksForTenant(Long tenantId) {
        return runbookRepository.findByTenantId(tenantId);
    }
}
