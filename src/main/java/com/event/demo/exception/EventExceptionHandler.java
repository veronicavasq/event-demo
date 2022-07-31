package com.event.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class EventExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(EventExceptionHandler.class);

    @ExceptionHandler(value = {InvalidEventException.class, ViolationPaidException.class})
    protected ResponseEntity<Object> handlerInternalException(EventException ex, WebRequest request) {
        logger.warn("EventException has occurred", ex);
        ErrorInfo errorInfo = new ErrorInfo(ex.getErrorCode().name(), ex.getErrorCode().getCode(), ex.getAdditionalData());
        return handleExceptionInternal(ex, errorInfo, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handlerGeneralException(RuntimeException ex, WebRequest request) {
        this.logger.warn(ex.toString());
        ErrorInfo errorInfo = new ErrorInfo(ErrorCode.INTERNAL_ERROR.name(), ErrorCode.INTERNAL_ERROR.getCode());
        return handleExceptionInternal(ex, errorInfo, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ErrorInfo("Failed Validation", ErrorCode.INVALID_EVENT.getCode(), details.toString()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus httpStatus, WebRequest request) {
        return new ResponseEntity<>(new ErrorInfo("Failed Validation", ErrorCode.INVALID_EVENT.getCode(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
