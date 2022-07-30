package com.event.demo.exception;

public class ViolationPaidException extends EventException {

    public ViolationPaidException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
