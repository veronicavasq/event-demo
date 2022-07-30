package com.event.demo.service.util;

import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;

import java.util.Date;
import java.util.UUID;

public final class EventTestUtil {

    private EventTestUtil() {
    }

    public static Event buildEvent(EventType eventType) {
        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setEventDate(new Date());
        event.setEventType(eventType);
        event.setLicensePlate(RandomStringUtils.randomAlphabetic(10));
        event.setSpeed(0.0F);
        event.setLimit(0.0F);
        event.setUnity("km/h");
        event.setProcessed(false);
        return event;
    }

    public static void validateViolation(Violation violation, String eventId) {
        Assertions.assertNotNull(violation);
        Assertions.assertEquals(violation.getEventId(), eventId);
        Assertions.assertNotNull(violation.getFine());
        Assertions.assertFalse(violation.getPaid());
    }

}
