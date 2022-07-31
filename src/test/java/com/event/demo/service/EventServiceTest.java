package com.event.demo.service;

import com.event.demo.exception.EventException;
import com.event.demo.exception.InvalidEventException;
import com.event.demo.exception.ViolationPaidException;
import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import com.event.demo.model.ViolationSummary;
import com.event.demo.service.util.EventTestUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.currentThread;

public class EventServiceTest {

    private EventService eventService;

    @BeforeEach
    void init() {
        this.eventService = new EventService();
    }

    @Test
    public void processEventInvalidEventTest() {
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(null));
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(new Event()));

        Event invalidEvent = new Event();
        invalidEvent.setId(UUID.randomUUID().toString());
        invalidEvent.setLicensePlate(RandomStringUtils.randomAlphabetic(10));
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(invalidEvent));
    }

    @Test
    public void processEventInvalidSpeedEventTest() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setLimit(null);
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(event));
    }

    @Test
    public void processEventSpeedTest() throws EventException {
        // No Speed violation
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(10.0F);
        event.setLimit(100.0F);
        Violation violation = this.eventService.processEvent(event);
        Assertions.assertNull(violation);

        // Send same speed even with no violation
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(event));

        // Speed violation
        Event violationEvent = EventTestUtil.buildEvent(EventType.SPEED);
        violationEvent.setSpeed(100.0F);
        violationEvent.setLimit(15.0F);
        violation = this.eventService.processEvent(violationEvent);
        EventTestUtil.validateViolation(violation, violationEvent.getId());

        // Send same speed even with no violation
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(violationEvent));
    }

    @Test
    public void processEventRedLightTest() throws EventException {
        // No red light violation
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        Violation violation = this.eventService.processEvent(event);
        Assertions.assertNull(violation);

        // Send same speed even with no violation
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(event));

        // Speed violation
        Event violationEvent = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        violation = this.eventService.processEvent(violationEvent);
        EventTestUtil.validateViolation(violation, violationEvent.getId());

        // Send same speed even with no violation
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.processEvent(violationEvent));
    }

    @Test
    public void getViolations() throws EventException {
        Event speedEvent = EventTestUtil.buildEvent(EventType.SPEED);
        speedEvent.setSpeed(100.0F);
        speedEvent.setLimit(15.0F);

        Event redLightEvent = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Event redLightEventCustomLicense = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        redLightEventCustomLicense.setLicensePlate("custom-license");

        Violation speedViolation = this.eventService.processEvent(speedEvent);
        EventTestUtil.validateViolation(speedViolation, speedEvent.getId());
        Assertions.assertNotNull(speedViolation.getId());

        Violation redLightViolation = this.eventService.processEvent(redLightEvent);
        EventTestUtil.validateViolation(redLightViolation, redLightEvent.getId());
        Assertions.assertNotNull(redLightViolation.getId());

        Violation redLightViolationCustomLicense = this.eventService.processEvent(redLightEventCustomLicense);
        EventTestUtil.validateViolation(redLightViolationCustomLicense, redLightEventCustomLicense.getId());
        Assertions.assertNotNull(redLightViolationCustomLicense.getId());

        // Get all violations
        List<Violation> violations = this.eventService.getViolations(null, null, null);
        Assertions.assertNotNull(violations);
        Assertions.assertEquals(violations.size(), 3);

        // Only red light violations
        List<Violation> redLightViolations = this.eventService.getViolations(EventType.RED_LIGHT, null, null);
        Assertions.assertNotNull(redLightViolations);
        Assertions.assertEquals(redLightViolations.size(), 2);

        // Only speed violations
        List<Violation> speedViolations = this.eventService.getViolations(EventType.SPEED, null, null);
        Assertions.assertNotNull(speedViolations);
        Assertions.assertEquals(speedViolations.size(), 1);

        // Only custom license violations
        List<Violation> customLicenseViolations = this.eventService.getViolations(null, null, "custom-license");
        Assertions.assertNotNull(customLicenseViolations);
        Assertions.assertEquals(customLicenseViolations.size(), 1);

        // Only paid violations
        this.eventService.violationPayment(speedViolation.getId());
        this.eventService.violationPayment(redLightViolation.getId());
        List<Violation> paidViolations = this.eventService.getViolations(null, true, null);
        Assertions.assertNotNull(paidViolations);
        Assertions.assertEquals(paidViolations.size(), 2);
    }

    @Test
    public void violationPaymentTest() throws EventException {
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.violationPayment(null));
        Assertions.assertThrows(InvalidEventException.class, () -> this.eventService.violationPayment("test-id"));

        Event event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Violation violation = this.eventService.processEvent(event);

        this.eventService.violationPayment(violation.getId());
        Assertions.assertThrows(ViolationPaidException.class, () -> this.eventService.violationPayment(violation.getId()));

        List<Violation> paidViolations = this.eventService.getViolations(null, true, null);
        Assertions.assertNotNull(paidViolations);
        Assertions.assertEquals(paidViolations.size(), 1);
        Assertions.assertEquals(paidViolations.get(0).getId(), violation.getId());
        Assertions.assertTrue(paidViolations.get(0).getPaid());
    }

    @Test
    public void getViolationSummary() throws EventException {
        // Speed violation event
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(100.0F);
        event.setLimit(15.0F);
        this.eventService.processEvent(event);

        // No violation event
        event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(.0F);
        event.setLimit(15.0F);
        this.eventService.processEvent(event);

        // red light events
        event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        this.eventService.processEvent(event);

        event = EventTestUtil.buildEvent(EventType.RED_LIGHT);
        Violation violation = this.eventService.processEvent(event);

        // Pay one violation event
        this.eventService.violationPayment(violation.getId());

        // Get summary
        ViolationSummary summary = this.eventService.getViolationSummary();
        Assertions.assertEquals(summary.getTotalPaid(), 1);
        Assertions.assertEquals(summary.getTotalUnpaid(), 2);
    }

    @Test
    public void testProcessDifferentEventWithConcurrency() {

        Event speedEvent = EventTestUtil.buildEvent(EventType.SPEED);
        speedEvent.setSpeed(120.0F);
        speedEvent.setLimit(100.0F);

        Event redLightEvent = EventTestUtil.buildEvent(EventType.RED_LIGHT);

        ExecutorService exec = Executors.newScheduledThreadPool(3);

        CompletableFuture<Violation> event1 = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Event:" + currentThread().getId() + " - time:" + System.currentTimeMillis());
                    try {
                        return this.eventService.processEvent(speedEvent);
                    } catch (EventException exception) {
                        return null;
                    }
                }, exec);

        CompletableFuture<Violation> event2 = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Event:" + currentThread().getId() + " - time:" + System.currentTimeMillis());
                    try {
                        return this.eventService.processEvent(redLightEvent);
                    } catch (EventException exception) {
                        return null;
                    }
                }, exec);

        CompletableFuture<Violation> event3 = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Event:" + currentThread().getId() + " - time:" + System.currentTimeMillis());
                    try {
                        return this.eventService.processEvent(speedEvent);
                    } catch (EventException exception) {
                        return null;
                    }
                }, exec);

        CompletableFuture.allOf(event1, event2, event3).join();

        List<Violation> violations = this.eventService.getViolations(null, null, null);
        Assertions.assertNotNull(violations);
        Assertions.assertEquals(violations.size(), 2);

    }

    @Test
    public void testProcessOneEventWithConcurrency() {
        Event event = EventTestUtil.buildEvent(EventType.SPEED);
        event.setSpeed(120.0F);
        event.setLimit(100.0F);

        ExecutorService exec = Executors.newScheduledThreadPool(100);

        List<CompletableFuture<Violation>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println("Event:" + currentThread().getId() + " - time:" + System.currentTimeMillis());
                        try {
                            return this.eventService.processEvent(event);
                        } catch (EventException exception) {
                            return null;
                        }
                    }, exec));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Violation> violations = this.eventService.getViolations(null, null, null);
        Assertions.assertNotNull(violations);
        Assertions.assertEquals(violations.size(), 1);

    }

}
