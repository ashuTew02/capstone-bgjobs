package com.capstone.bgjobs.model;

public enum KafkaTopic {
    AUTHSERVER_JFC("authserver_jfc"),
    TOOLSCHEDULER_JFC("toolscheduler_jfc"),
    PARSER_JFC("parser_jfc"),
    ACK_JOB("ack_job"),
    BGJOBS_JFC("bgjobs_jfc"),
    JOBINGESTION_JFC("jobingestion_jfc");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }
}
