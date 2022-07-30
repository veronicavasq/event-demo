package com.event.demo.exception;

public class InvalidEventException extends EventException {

    public InvalidEventException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
