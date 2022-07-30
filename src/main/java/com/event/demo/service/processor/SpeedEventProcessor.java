package com.event.demo.service.processor;

import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class SpeedEventProcessor implements IEventProcessor {

    private static final BigDecimal FINE_COST = new BigDecimal("50.00");

    @Override
    public boolean isValidEventForProcessor(Event event) {
        return EventType.SPEED == event.getEventType()
                && ObjectUtils.allNotNull(event.getSpeed(), event.getLimit())
                && StringUtils.isNotBlank(event.getUnity());
    }

    @Override
    public Violation getViolationInfo(Event event) {
        if (event.getSpeed() > event.getLimit()) {
            return new Violation(event.getId(), FINE_COST, false);
        }
        return null;
    }

}
