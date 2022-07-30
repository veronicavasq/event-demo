package com.event.demo.service.processor;

import com.event.demo.exception.ErrorCode;
import com.event.demo.exception.EventException;
import com.event.demo.exception.InvalidEventException;
import com.event.demo.model.Event;
import com.event.demo.model.Violation;

public interface IEventProcessor {

    Violation getViolationInfo(Event event);

    boolean isValidEventForProcessor(Event event);

    default Violation determineViolation(Event event) throws EventException {
        if (!isValidEventForProcessor(event)) {
            throw new InvalidEventException(ErrorCode.INVALID_EVENT, "Invalid event");
        }
        return getViolationInfo(event);
    }

}
