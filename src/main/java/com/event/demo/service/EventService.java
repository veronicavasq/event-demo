package com.event.demo.service;

import com.event.demo.exception.ErrorCode;
import com.event.demo.exception.EventException;
import com.event.demo.exception.InvalidEventException;
import com.event.demo.exception.ViolationPaidException;
import com.event.demo.model.Event;
import com.event.demo.model.EventType;
import com.event.demo.model.Violation;
import com.event.demo.model.ViolationSummary;
import com.event.demo.service.processor.EventProcessorFactory;
import com.event.demo.service.processor.IEventProcessor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final ConcurrentHashMap<String, Event> eventMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Violation> violationMap = new ConcurrentHashMap<>();

    public synchronized Violation processEvent(Event event) throws EventException {
        this.validateEvent(event);

        if (eventMap.containsKey(event.getId())) {
            logger.warn("Event was already process: {}", event.getId());
            throw new InvalidEventException(ErrorCode.PROCESSED_EVENT, "Event was already process");
        }

        IEventProcessor eventProcessor = EventProcessorFactory.getEventProcessor(event.getEventType());
        Violation violation = eventProcessor.determineViolation(event);
        if (violation != null) {
            violation.setId(UUID.randomUUID().toString());
            event.setProcessed(true);
            violationMap.put(violation.getId(), violation);
        }
        eventMap.put(event.getId(), event);
        return violation;
    }

    private void validateEvent(Event event) throws EventException {
        if (event == null
                || StringUtils.isAnyBlank(event.getId(), event.getLicensePlate())
                || ObjectUtils.anyNull(event.getEventDate(), event.getEventType())) {
            throw new InvalidEventException(ErrorCode.INVALID_EVENT, "Invalid event");
        }
    }

    public List<Violation> getViolations(EventType eventType, Boolean paid, String licensePlate) {
        return violationMap.values()
                .stream()
                .filter(violation -> violationPassFilters(violation, eventType, paid, licensePlate))
                .collect(Collectors.toList());
    }

    private boolean violationPassFilters(Violation violation, EventType eventType, Boolean paid, String licensePlate) {
        Event event = eventMap.get(violation.getEventId());

        if (eventType != null && event.getEventType() != eventType) {
            return false;
        }

        if (StringUtils.isNotBlank(licensePlate) && !licensePlate.equalsIgnoreCase(event.getLicensePlate())) {
            return false;
        }

        if (paid != null && paid != violation.getPaid()) {
            return false;
        }

        return true;
    }

    public synchronized void violationPayment(String violationId) throws EventException {
        if (StringUtils.isBlank(violationId) || !violationMap.containsKey(violationId)) {
            throw new InvalidEventException(ErrorCode.INVALID_VIOLATION, "Invalid violation");
        }

        Violation violation = violationMap.get(violationId);
        if (violation.getPaid()) {
            throw new ViolationPaidException(ErrorCode.PAID_VIOLATION, "Violation was already paid");
        }

        violationMap.get(violationId).setPaid(true);
    }

    public ViolationSummary getViolationSummary() {
        ViolationSummary violationSummary = new ViolationSummary();
        for (Violation violation : violationMap.values()) {
            if (violation.getPaid()) {
                violationSummary.setTotalPaid(violationSummary.getTotalPaid() + 1);
            } else {
                violationSummary.setTotalUnpaid(violationSummary.getTotalUnpaid() + 1);
            }
        }

        return violationSummary;
    }

}

