package com.capstone.bgjobs.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.capstone.bgjobs.dto.event.AckRunbookTriggerJobEvent;
import com.capstone.bgjobs.dto.event.payload.AckJobEventPayload;
import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.JobStatus;  // or import from your Enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AckRunbookTriggerJobEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AckRunbookTriggerJobEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(Long jobId, JobStatus status) {
        try {
            // Mark job as SUCCESS or IN_PROGRESS, etc. For now, let's do SUCCESS.
            AckJobEventPayload payload = new AckJobEventPayload(jobId, status);
            AckRunbookTriggerJobEvent ackEvent = new AckRunbookTriggerJobEvent(payload);

            String json = objectMapper.writeValueAsString(ackEvent);

            kafkaTemplate.send(KafkaTopic.ACK_JOB.getTopicName(),
                                AckRunbookTriggerJobEvent.class.getSimpleName(),
                               json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
