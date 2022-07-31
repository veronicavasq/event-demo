package com.event.demo.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorInfo {

    private String type;
    private String code;
    private Map<String, Object> additionalData;

    public ErrorInfo() {
    }

    public ErrorInfo(String type, String code, Map<String, Object> additionalData) {
        this.type = type;
        this.code = code;
        this.additionalData = additionalData;
    }

    public ErrorInfo(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public ErrorInfo(String type, String code, String message) {
        this.type = type;
        this.code = code;
        this.additionalData = new HashMap<>();
        additionalData.put("message", message);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
}
