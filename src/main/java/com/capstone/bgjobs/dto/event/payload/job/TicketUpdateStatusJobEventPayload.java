package com.capstone.bgjobs.dto.event.payload.job;

import com.capstone.bgjobs.dto.event.payload.ticket.TicketUpdateStatusEventPayload;
import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tool;

public class TicketUpdateStatusJobEventPayload {
    String findingId;
    Long tenantId;
    Long jobId;
    Tool tool;
    String status;
    String ticketId;

    public TicketUpdateStatusJobEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool,
            String status, Long jobId, String ticketId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.tool = tool;
        this.status = status;
        this.jobId = jobId;
        this.ticketId = ticketId;
    }

    public TicketUpdateStatusJobEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool, Long jobId, String ticketId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.tool = tool;
        this.status = "DONE";
        this.jobId = jobId;
        this.ticketId = ticketId;
    }

    public TicketUpdateStatusJobEventPayload(TicketUpdateStatusEventPayload payload, Long jobId) {
        this.findingId = payload.getFindingId();
        this.tenantId = payload.getTenantId();
        this.tool = payload.getTool();
        this.status = payload.getStatus();
        this.ticketId = payload.getTicketId();
        this.jobId = jobId;
    }

    public TicketUpdateStatusJobEventPayload() {
    }

    public String getFindingId() {
        return findingId;
    }

    public void setFindingId(String findingId) {
        this.findingId = findingId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
