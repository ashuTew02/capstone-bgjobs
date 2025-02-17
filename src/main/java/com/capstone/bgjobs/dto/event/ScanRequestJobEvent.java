package com.capstone.bgjobs.dto.event;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.payload.ScanRequestJobEventPayload;
import com.capstone.bgjobs.model.EventType;

public final class ScanRequestJobEvent implements Event<ScanRequestJobEventPayload> {
    private ScanRequestJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.SCAN_REQUEST_JOB;


    public ScanRequestJobEvent(ScanRequestJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public ScanRequestJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public ScanRequestJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
