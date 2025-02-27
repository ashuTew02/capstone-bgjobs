package com.capstone.bgjobs.dto.event;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.payload.RunbookTriggerEventPayload;
import com.capstone.bgjobs.model.EventType;

public class RunbookTriggerEvent implements Event<RunbookTriggerEventPayload>{
    private RunbookTriggerEventPayload payload;
    private String eventId;
    private EventType type = EventType.RUNBOOK_TRIGGER;

    public RunbookTriggerEvent(RunbookTriggerEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public RunbookTriggerEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public RunbookTriggerEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
