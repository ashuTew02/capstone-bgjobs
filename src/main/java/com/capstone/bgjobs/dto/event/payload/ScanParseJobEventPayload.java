package com.capstone.bgjobs.dto.event.payload;

import com.capstone.bgjobs.model.Tool;

public class ScanParseJobEventPayload {
    Tool tool;
    Long tenantId;
    String scanFilePath;

    public ScanParseJobEventPayload(Tool tool, Long tenantId, String scanFilePath) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.scanFilePath = scanFilePath;
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
