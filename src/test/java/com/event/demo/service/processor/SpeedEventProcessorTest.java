package com.event.demo.service.processor;

import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import com.event.demo.service.util.EventTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpeedEventProcessorTest {

    private final SpeedEventProcessor processor = new SpeedEventProcessor();

    @Test
    public void isValidEventForProcessorOk() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        Assertions.assertTrue(this.processor.isValidEventForProcessor(event));
    }

    @Test
    public void isValidEventForProcessorInvalidEventType() {
        Event event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Assertions.assertFalse(this.processor.isValidEventForProcessor(event));
    }

    @Test
    public void isValidEventForProcessorMissingFields() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setLimit(null);
        Assertions.assertFalse(this.processor.isValidEventForProcessor(event));

        event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(null);
        Assertions.assertFalse(this.processor.isValidEventForProcessor(event));

        event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setUnity(null);
        Assertions.assertFalse(this.processor.isValidEventForProcessor(event));
    }

    @Test
    public void getViolationInfoOk() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(300.0F);
        event.setLimit(50.0F);
        Violation violation = this.processor.getViolationInfo(event);
        EventTestUtil.validateViolation(violation, event.getId());
        Assertions.assertNull(violation.getId());
    }

    @Test
    public void getViolationInfoNoViolation() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(10.0F);
        event.setLimit(50.0F);
        Violation violation = this.processor.getViolationInfo(event);
        Assertions.assertNull(violation);
    }

}
