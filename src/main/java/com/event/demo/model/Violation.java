package com.event.demo.model;

import java.math.BigDecimal;

public class Violation {

    private String id;
    private String eventId;
    private BigDecimal fine;
    private Boolean paid;

    public Violation(String eventId, BigDecimal fine, Boolean paid) {
        this.eventId = eventId;
        this.fine = fine;
        this.paid = paid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
