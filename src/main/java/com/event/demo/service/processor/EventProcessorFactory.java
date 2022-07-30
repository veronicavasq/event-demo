package com.event.demo.service.processor;

import com.event.demo.exception.ErrorCode;
import com.event.demo.exception.EventException;
import com.event.demo.exception.InvalidEventException;
import com.event.demo.model.EventType;

import java.util.Map;

public final class EventProcessorFactory {

    private EventProcessorFactory() {
    }

    private final static Map<EventType, IEventProcessor> eventProcessorMap = Map.of(
            EventType.SPEED, new SpeedEventProcessor(),
            EventType.RED_LIGHT, new RedLightEventProcessor()
    );

    public static IEventProcessor getEventProcessor(EventType eventType) throws EventException {
        if (!eventProcessorMap.containsKey(eventType)) {
            throw new InvalidEventException(ErrorCode.UNSUPPORTED_EVENT, "Unsupported event");
        }

        return eventProcessorMap.get(eventType);
    }

}
