package com.capstone.bgjobs.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.capstone.bgjobs.dto.event.StateUpdateJobEvent;
import com.capstone.bgjobs.dto.event.payload.StateUpdateJobEventPayload;
import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tenant;
import com.capstone.bgjobs.model.Tool;
import com.capstone.bgjobs.repository.TenantRepository;
import com.capstone.bgjobs.service.github.update.GitHubFindingUpdateService;
import com.capstone.bgjobs.kafka.producer.AckStateUpdateJobEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StateUpdateJobEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateUpdateJobEventConsumer.class);

    private final TenantRepository tenantRepository;
    private final ObjectMapper objectMapper;

    // Inject new producers for ack & parse job
    private final AckStateUpdateJobEventProducer ackProducer;
    private final Map<Tool, GitHubFindingUpdateService> serviceByTool;

    public StateUpdateJobEventConsumer(

            TenantRepository tenantRepository,
            ObjectMapper objectMapper,
            AckStateUpdateJobEventProducer ackProducer,
            List<GitHubFindingUpdateService> services
    ) {
        this.serviceByTool = services.stream()
        .collect(Collectors.toMap(
            GitHubFindingUpdateService::getToolType,
            Function.identity()
        ));
        this.tenantRepository = tenantRepository;
        this.objectMapper = objectMapper;
        this.ackProducer = ackProducer;
    }

    @KafkaListener(
        topics = "#{T(com.capstone.bgjobs.model.KafkaTopic).BGJOBS_JFC.getTopicName()}",
        groupId = "${spring.kafka.consumer.group-id:bgjobs-consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(String rawJson) {
        try {
            if(!rawJson.contains("\"STATE_UPDATE_JOB\"")) {
                return;
            }
            LOGGER.info("Received StateUpdateJobEvent (from JFC) as raw JSON: {}", rawJson);
            StateUpdateJobEvent stateUpdateJobEvent =
            objectMapper.readValue(rawJson, StateUpdateJobEvent.class);
            
            StateUpdateJobEventPayload payload = stateUpdateJobEvent.getPayload();
            GitHubFindingUpdateService service = serviceByTool.get(payload.getTool());

            if (service == null) {
                throw new IllegalArgumentException("Unsupported tool: " + payload.getTool());
            }
        Long tenantId = payload.getTenantId();
        // Tool tool = payload.getTool();
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
        String esFindingId = payload.getEsFindingId();
        // 3) Extract needed data from Tenant
        String owner = payload.getOwner();
        String repo = payload.getRepository();
        // String pat  = tenant.getPat();  // personal access token
        String pat  = tenant.getPat();  // personal access token


        service.updateFinding(
            owner,
            repo,
            pat,
            payload.getAlertNumber(),
            payload.getUpdatedState(),
            esFindingId,
            tenantId
        );

        ackProducer.produce(stateUpdateJobEvent.getEventId());

        } catch (Exception e) {
            LOGGER.error("Error deserializing or processing scan request", e);
        }
    }
}
