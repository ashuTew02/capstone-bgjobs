package com.capstone.bgjobs.dto.event;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.payload.ScanRequestEventPayload;
import com.capstone.bgjobs.model.EventType;

public final class ScanRequestEvent implements Event<ScanRequestEventPayload> {
    private ScanRequestEventPayload payload;
    private String eventId;
    private EventType type = EventType.SCAN_REQUEST;


    public ScanRequestEvent(ScanRequestEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public ScanRequestEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public ScanRequestEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
