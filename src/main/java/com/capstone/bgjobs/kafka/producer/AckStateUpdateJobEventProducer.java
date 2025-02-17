package com.capstone.bgjobs.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.capstone.bgjobs.dto.event.AckStateUpdateJobEvent;
import com.capstone.bgjobs.dto.event.payload.AckJobEventPayload;
import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.JobStatus;  // or import from your Enum
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AckStateUpdateJobEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public AckStateUpdateJobEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void produce(String jobId) {
        try {
            // Mark job as SUCCESS or IN_PROGRESS, etc. For now, let's do SUCCESS.
            AckJobEventPayload payload = new AckJobEventPayload(jobId, JobStatus.SUCCESS);
            AckStateUpdateJobEvent ackEvent = new AckStateUpdateJobEvent(payload);

            String json = objectMapper.writeValueAsString(ackEvent);

            kafkaTemplate.send(KafkaTopic.ACK_JOB.getTopicName(),
                               jobId,
                               json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
