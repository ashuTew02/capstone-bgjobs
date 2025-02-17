package com.capstone.bgjobs.dto.event;

import com.capstone.bgjobs.model.EventType;

public interface Event<T> {
    EventType getType();
    T getPayload();
    String getEventId();
}
