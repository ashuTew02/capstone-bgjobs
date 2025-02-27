package com.capstone.bgjobs.dto.event.payload.job;

import com.capstone.bgjobs.dto.event.payload.ticket.TicketCreateEventPayload;
import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tool;

public class TicketCreateJobEventPayload {
    String findingId;
    String summary;
    String description;
    Long tenantId;
    Tool tool;
    Long jobId;
    
    public TicketCreateJobEventPayload(String findingId, String summary, String description, Long tenantId,
            KafkaTopic destTopic, Tool tool, Long jobId) {
        this.findingId = findingId;
        this.summary = summary;
        this.description = description;
        this.tenantId = tenantId;
        this.tool = tool;
        this.jobId = jobId;
    }

    public TicketCreateJobEventPayload(TicketCreateEventPayload payload, Long jobId) {
        this.findingId = payload.getFindingId();
        this.summary = payload.getSummary();
        this.description = payload.getDescription();
        this.tenantId = payload.getTenantId();
        this.tool = payload.getTool();
        this.jobId = jobId;
    }
    
    public TicketCreateJobEventPayload() {
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
    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
}
