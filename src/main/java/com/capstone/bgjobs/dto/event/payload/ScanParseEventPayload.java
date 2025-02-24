package com.capstone.bgjobs.dto.event.payload;

import com.capstone.bgjobs.model.KafkaTopic;
import com.capstone.bgjobs.model.Tool;

public class ScanParseEventPayload {
    Tool tool;
    Long tenantId;
    String scanFilePath;
    KafkaTopic destTopic;

    public KafkaTopic getDestTopic() {
        return destTopic;
    }

    public void setDestTopic(KafkaTopic destTopic) {
        this.destTopic = destTopic;
    }

    public ScanParseEventPayload(Tool tool, Long tenantId, String scanFilePath, KafkaTopic destTopic) {
        this.tool = tool;
        this.tenantId = tenantId;
        this.scanFilePath = scanFilePath;
        this.destTopic = destTopic;
    }

    public ScanParseEventPayload() {
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
