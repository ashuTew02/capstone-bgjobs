package com.capstone.bgjobs.dto.event.ticket;

import java.util.UUID;

import com.capstone.bgjobs.dto.event.Event;
import com.capstone.bgjobs.dto.event.payload.ticket.TicketCreateEventPayload;
import com.capstone.bgjobs.model.EventType;

public final class TicketCreateEvent implements Event<TicketCreateEventPayload> {
    private TicketCreateEventPayload payload;
    private String eventId;
    private EventType type = EventType.TICKET_CREATE;


    public TicketCreateEvent(TicketCreateEventPayload payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public TicketCreateEvent() {
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public TicketCreateEventPayload getPayload() {
        return payload;
    }

    @Override
    public String getEventId() {
        return eventId;
    }
}
