package com.event.demo.service.processor;

import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import com.event.demo.service.util.EventTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RedLightEventProcessorTest {

    private final RedLightEventProcessor processor = new RedLightEventProcessor();

    @Test
    public void isValidEventForProcessorOk() {
        Event event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Assertions.assertTrue(this.processor.isValidEventForProcessor(event));
    }

    @Test
    public void getViolationInfoOk() {
        Event event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Violation violation = this.processor.getViolationInfo(event);
        EventTestUtil.validateViolation(violation, event.getId());
    }
}
