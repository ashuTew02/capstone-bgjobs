package com.capstone.bgjobs.dto.event.payload.job;

import com.capstone.bgjobs.dto.event.payload.ScanRequestEventPayload;
import com.capstone.bgjobs.model.Tool;

public final class ScanRequestJobEventPayload {
    private Tool tool;
    private String owner;
    private String repository;
    private Long tenantId;
    Long jobId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public ScanRequestJobEventPayload(Tool tool, Long tenantId, String owner, String repository, Long jobId) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.owner = owner;
        this.repository = repository;
        this.jobId = jobId;
    }

    public ScanRequestJobEventPayload(Long jobId, ScanRequestEventPayload payload) {
        this.tool = payload.getTool();
        this.tenantId = payload.getTenantId();
        this.owner = payload.getOwner();
        this.repository = payload.getRepository();
        this.jobId = jobId;
    }

    public ScanRequestJobEventPayload() {
    }

    public Tool getTool() {
        return tool;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepository() {
        return repository;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
