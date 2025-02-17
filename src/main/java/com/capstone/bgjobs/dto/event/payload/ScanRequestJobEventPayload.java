package com.capstone.bgjobs.dto.event.payload;

import com.capstone.bgjobs.model.Tool;

public final class ScanRequestJobEventPayload {
    private Tool tool;
    private String owner;
    private String repository;
    private Long tenantId;

    public ScanRequestJobEventPayload(Tool tool, Long tenantId, String owner, String repository) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.owner = owner;
        this.repository = repository;
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
