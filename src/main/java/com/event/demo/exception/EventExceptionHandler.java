package com.event.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EventExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(EventExceptionHandler.class);

    @ExceptionHandler(value = {InvalidEventException.class, ViolationPaidException.class})
    protected ResponseEntity<Object> handlerInternalException(EventException ex, WebRequest request) {
        logger.warn("EventException has occurred", ex);
        ErrorInfo errorInfo = new ErrorInfo(ex.getErrorCode().name(), ex.getErrorCode().getCode(), ex.getAdditionalData());
        return handleExceptionInternal(ex, errorInfo, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
