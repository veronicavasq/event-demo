package com.event.demo.exception;

public enum ErrorCode {

    INVALID_EVENT("001"),
    PROCESSED_EVENT("002"),
    UNSUPPORTED_EVENT("003"),
    INVALID_VIOLATION("004"),
    PAID_VIOLATION("005"),
    INTERNAL_ERROR("006");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
