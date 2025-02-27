package com.capstone.bgjobs.dto.event;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.payload.AckJobEventPayload;
import com.capstone.bgjobs.model.EventType;

public class AckTicketCreateJobEvent implements Event<AckJobEventPayload>{
    private AckJobEventPayload payload;
    private String eventId;
    private EventType type = EventType.ACK_TICKET_CREATE_JOB;


    public AckTicketCreateJobEvent(AckJobEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public AckTicketCreateJobEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public AckJobEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
