package com.capstone.bgjobs.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.bgjobs.dto.event.job.RunbookTriggerJobEvent;
import com.capstone.bgjobs.dto.event.job.StateUpdateJobEvent;
import com.capstone.bgjobs.dto.event.payload.job.RunbookTriggerJobEventPayload;
import com.capstone.bgjobs.dto.event.payload.job.StateUpdateJobEventPayload;
import com.capstone.bgjobs.dto.event.payload.job.TicketCreateJobEventPayload;
import com.capstone.bgjobs.dto.event.payload.job.TicketUpdateStatusJobEventPayload;
import com.capstone.bgjobs.dto.event.ticket.job.TicketCreateJobEvent;
import com.capstone.bgjobs.dto.event.ticket.job.TicketUpdateStatusJobEvent;
import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.JobStatus;
import com.capstone.bgjobs.model.Tenant;
import com.capstone.bgjobs.model.TenantTicket;
import com.capstone.bgjobs.model.Tool;
import com.capstone.bgjobs.repository.TenantRepository;
import com.capstone.bgjobs.repository.TenantTicketRepository;
import com.capstone.bgjobs.service.JiraTicketService;
import com.capstone.bgjobs.service.github.update.GitHubFindingUpdateService;
import com.capstone.bgjobs.service.runbook.RunbookJobService;
import com.capstone.bgjobs.kafka.producer.AckRunbookTriggerJobEventProducer;
import com.capstone.bgjobs.kafka.producer.AckStateUpdateJobEventProducer;
import com.capstone.bgjobs.kafka.producer.AckTicketCreateJobEventProducer;
import com.capstone.bgjobs.kafka.producer.AckTicketUpdateStatusJobEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UnifiedJobEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedJobEventConsumer.class);

    private final TenantRepository tenantRepository;
    private final ObjectMapper objectMapper;
    private final JiraTicketService jiraTicketService;
    private final AckStateUpdateJobEventProducer stateUpdateAckProducer;
    private final AckTicketCreateJobEventProducer ticketCreateAckProducer;
    private final AckTicketUpdateStatusJobEventProducer ticketUpdateStatusAckProducer;
    private final AckRunbookTriggerJobEventProducer runbookTriggerAckProducer;
    private final Map<Tool, GitHubFindingUpdateService> serviceByTool;
    private final TenantTicketRepository tenantTicketRepository;
    private final RunbookJobService runbookJobService;

    public UnifiedJobEventConsumer(
            TenantRepository tenantRepository,
            ObjectMapper objectMapper,
            JiraTicketService jiraTicketService,
            List<GitHubFindingUpdateService> services,
            TenantTicketRepository tenantTicketRepository,
            RunbookJobService runbookJobService,
            AckStateUpdateJobEventProducer ackStateUpdateJobEventProducer,
            AckTicketCreateJobEventProducer ackTicketCreateJobEventProducer,
            AckTicketUpdateStatusJobEventProducer ackTicketUpdateStatusJobEventProducer,
            AckRunbookTriggerJobEventProducer ackRunbookTriggerJobEventProducer
    ) {
        this.tenantRepository = tenantRepository;
        this.objectMapper = objectMapper;
        this.jiraTicketService = jiraTicketService;
        this.tenantTicketRepository = tenantTicketRepository;
        this.serviceByTool = services.stream()
                .collect(Collectors.toMap(GitHubFindingUpdateService::getToolType, Function.identity()));
        this.runbookJobService = runbookJobService;
        this.stateUpdateAckProducer = ackStateUpdateJobEventProducer;
        this.ticketCreateAckProducer = ackTicketCreateJobEventProducer;
        this.ticketUpdateStatusAckProducer = ackTicketUpdateStatusJobEventProducer;
        this.runbookTriggerAckProducer = ackRunbookTriggerJobEventProducer;
    }

    @KafkaListener(
            topics = "#{T(com.capstone.bgjobs.model.KafkaTopic).BGJOBS_JFC.getTopicName()}",
            groupId = "${spring.kafka.consumer.group-id:bgjobs-consumer-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(String rawJson) {
        System.out.println("Bgjobs receives event");

        Long wholeJobId = null;
        try {
            if (rawJson.contains("\"STATE_UPDATE_JOB\"")) {
                wholeJobId = handleStateUpdateJob(rawJson);
            } else if (rawJson.contains("\"TICKET_CREATE_JOB\"")) {
                wholeJobId = handleTicketCreateJob(rawJson);
            } else if (rawJson.contains("\"TICKET_UPDATE_STATUS_JOB\"")) {
                wholeJobId = handleTicketUpdateStatusJob(rawJson);
            } else if (rawJson.contains("\"RUNBOOK_TRIGGER_JOB\"")) {
                wholeJobId = handleRunbookTriggerJob(rawJson);
            }
        } catch (Exception e) {
            if (wholeJobId != null) {
                stateUpdateAckProducer.produce(wholeJobId, JobStatus.FAILURE);
            }
            LOGGER.error("Error deserializing or processing job event", e);
        }
    }

    private Long handleStateUpdateJob(String rawJson) throws Exception {
        StateUpdateJobEvent event = objectMapper.readValue(rawJson, StateUpdateJobEvent.class);
        StateUpdateJobEventPayload payload = event.getPayload();
        GitHubFindingUpdateService ghService = serviceByTool.get(payload.getTool());

        if (ghService == null) {
            throw new IllegalArgumentException("Unsupported tool: " + payload.getTool());
        }

        Tenant tenant = tenantRepository.findById(payload.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + payload.getTenantId()));

        ghService.updateFinding(
                payload.getOwner(),
                payload.getRepository(),
                tenant.getPat(),
                payload.getAlertNumber(),
                payload.getUpdatedState(),
                payload.getEsFindingId(),
                payload.getTenantId()
        );


        Optional<TenantTicket> optionalTenantTicket = tenantTicketRepository.findByEsFindingId(payload.getEsFindingId());
        optionalTenantTicket.ifPresent(tenantTicket -> {
            if(!payload.getUpdatedState().equals(FindingState.OPEN)) {
                String ticketId = tenantTicket.getTicketId();
                jiraTicketService.transitionTicketToDone(payload.getTenantId(), ticketId);
            }
        });


        stateUpdateAckProducer.produce(payload.getJobId(), JobStatus.SUCCESS);
        return payload.getJobId();
    }

    private Long handleTicketCreateJob(String rawJson) throws Exception {
        TicketCreateJobEvent event = objectMapper.readValue(rawJson, TicketCreateJobEvent.class);
        TicketCreateJobEventPayload payload = event.getPayload();

        jiraTicketService.createTicket(
                payload.getTenantId(),
                payload.getFindingId(),
                payload.getSummary(),
                payload.getDescription()
        );

        ticketCreateAckProducer.produce(payload.getJobId(), JobStatus.SUCCESS);
        return payload.getJobId();
    }

    private Long handleTicketUpdateStatusJob(String rawJson) throws Exception {
        TicketUpdateStatusJobEvent event = objectMapper.readValue(rawJson, TicketUpdateStatusJobEvent.class);
        TicketUpdateStatusJobEventPayload payload = event.getPayload();

        jiraTicketService.transitionTicketToDone(payload.getTenantId(), payload.getTicketId());
        ticketUpdateStatusAckProducer.produce(payload.getJobId(), JobStatus.SUCCESS);
        return payload.getJobId();
    }

    @Transactional
    private Long handleRunbookTriggerJob(String rawJson) throws Exception {
        System.out.println("Received Runbook Trigger Job");
        RunbookTriggerJobEvent event = objectMapper.readValue(rawJson, RunbookTriggerJobEvent.class);
        RunbookTriggerJobEventPayload payload = event.getPayload();

        System.out.println(payload.getTool());

        GitHubFindingUpdateService ghService = serviceByTool.get(payload.getTool());
        // System.out.println("RECEIVED RUNBOOK TRIGGER JOB, finding list:: " + payload.getFindingIds().toString());
        if (ghService == null) {
            throw new IllegalArgumentException("Unsupported tool: " + payload.getTool());
        }
        System.out.println("GHSERVICE::" + ghService.getClass());

        
        runbookJobService.handleTriggerJob(event);
        runbookTriggerAckProducer.produce(payload.getJobId(), JobStatus.SUCCESS);
        return payload.getJobId();
    }
}
