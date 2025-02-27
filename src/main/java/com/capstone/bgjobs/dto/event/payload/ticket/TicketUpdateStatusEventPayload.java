package com.capstone.bgjobs.dto.event.payload.ticket;

import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tool;

public class TicketUpdateStatusEventPayload {
    String findingId;
    Long tenantId;
    KafkaTopic destTopic;
    Tool tool;
    String status;
    String ticketId;

    public TicketUpdateStatusEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool,
            String status, String ticketId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.destTopic = destTopic;
        this.tool = tool;
        this.status = status;
        this.ticketId = ticketId;
    }

    public TicketUpdateStatusEventPayload(String findingId, Long tenantId, KafkaTopic destTopic, Tool tool, String ticketId) {
        this.findingId = findingId;
        this.tenantId = tenantId;
        this.destTopic = destTopic;
        this.tool = tool;
        this.status = "DONE";
        this.ticketId = ticketId;
    }

    public TicketUpdateStatusEventPayload() {
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTicketId() {
        return ticketId;
    }
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
