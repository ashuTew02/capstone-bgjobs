package com.capstone.bgjobs.dto.event.payload.ticket;

import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tool;

public class TicketCreateEventPayload {
    String findingId;
    String summary;
    String description;
    Long tenantId;
    KafkaTopic destTopic;
    Tool tool;
    
    public TicketCreateEventPayload(String findingId, String summary, String description, Long tenantId,
            KafkaTopic destTopic, Tool tool) {
        this.findingId = findingId;
        this.summary = summary;
        this.description = description;
        this.tenantId = tenantId;
        this.destTopic = destTopic;
        this.tool = tool;
    }
    
    public TicketCreateEventPayload() {
    }

    public String getFindingId() {
        return findingId;
    }
    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    public KafkaTopic getDestTopic() {
        return destTopic;
    }
    public void setDestTopic(KafkaTopic destTopic) {
        this.destTopic = destTopic;
    }
    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }


    
}
