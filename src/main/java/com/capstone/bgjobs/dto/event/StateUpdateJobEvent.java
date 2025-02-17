package com.capstone.bgjobs.dto.event;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.payload.StateUpdateJobEventPayload;
import com.capstone.bgjobs.model.EventType;

public final class StateUpdateJobEvent implements Event<StateUpdateJobEventPayload> {
    private StateUpdateJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.STATE_UPDATE_JOB;


    public StateUpdateJobEvent(StateUpdateJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    
    public StateUpdateJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }


    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public StateUpdateJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
