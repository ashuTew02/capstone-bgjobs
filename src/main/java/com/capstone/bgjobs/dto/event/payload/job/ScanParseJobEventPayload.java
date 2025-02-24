package com.capstone.bgjobs.dto.event.payload.job;

import com.capstone.bgjobs.dto.event.payload.ScanParseEventPayload;
import com.capstone.bgjobs.model.Tool;

public class ScanParseJobEventPayload {
    Tool tool;
    Long tenantId;
    String scanFilePath;
    Long jobId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public ScanParseJobEventPayload(Tool tool, Long tenantId, String scanFilePath, Long jobId) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.scanFilePath = scanFilePath;
        this.jobId = jobId;
    }
    public ScanParseJobEventPayload(Long jobId, ScanParseEventPayload payload) {
        this.jobId = jobId;
        this.tool = payload.getTool();
        this.tenantId = payload.getTenantId();
        this.scanFilePath = payload.getScanFilePath();
    }


    public ScanParseJobEventPayload() {
    }

    public Tool getTool() {
        return tool;
    }
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    public String getScanFilePath() {
        return scanFilePath;
    }
    public void setScanFilePath(String scanFilePath) {
        this.scanFilePath = scanFilePath;
    }
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    
}
