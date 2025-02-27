package com.capstone.bgjobs.dto.event.payload.job;

import java.util.List;

import com.capstone.bgjobs.dto.event.payload.RunbookTriggerEventPayload;
import com.capstone.bgjobs.model.Tool;
import com.capstone.bgjobs.model.runbook.RunbookTriggerType;

public class RunbookTriggerJobEventPayload {
    Tool tool;
    Long tenantId;
    List<String> findingIds;
    Long jobId;
    RunbookTriggerType triggerType;

    public RunbookTriggerJobEventPayload(Tool tool, Long tenantId, List<String> findingIds,
            RunbookTriggerType triggerType) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.findingIds = findingIds;
        this.triggerType = triggerType;
    }

    public RunbookTriggerJobEventPayload(Long jobId, RunbookTriggerEventPayload payload) {
        this.jobId = jobId;
        this.tool = payload.getTool();
        this.tenantId = payload.getTenantId();
        this.findingIds = payload.getFindingIds();
        this.triggerType = payload.getTriggerType();
    }

    public RunbookTriggerJobEventPayload() {
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getFindingIds() {
        return findingIds;
    }

    public void setFindingIds(List<String> findingIds) {
        this.findingIds = findingIds;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public RunbookTriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(RunbookTriggerType triggerType) {
        this.triggerType = triggerType;
    }
}
