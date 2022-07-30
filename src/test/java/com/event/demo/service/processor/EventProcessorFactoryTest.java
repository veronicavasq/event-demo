package com.event.demo.service.processor;

import com.event.demo.exception.EventException;
import com.event.demo.model.EventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventProcessorFactoryTest {

    @Test
    public void getEventProcessorForEveryEventType() throws EventException {
        for (EventType eventType : EventType.values()) {
            IEventProcessor processor = EventProcessorFactory.getEventProcessor(eventType);
            Assertions.assertNotNull(processor);
        }
    }
}
