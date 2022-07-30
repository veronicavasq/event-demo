package com.event.demo.service.processor;

import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;

import java.math.BigDecimal;

public class RedLightEventProcessor implements IEventProcessor {

    private static final BigDecimal FINE_COST = new BigDecimal("100.00");

    @Override
    public boolean isValidEventForProcessor(Event event) {
        return EventType.RED_LIGHT == event.getEventType();
    }

    @Override
    public Violation getViolationInfo(Event event) {
        return new Violation(event.getId(), FINE_COST, false);
    }

}
