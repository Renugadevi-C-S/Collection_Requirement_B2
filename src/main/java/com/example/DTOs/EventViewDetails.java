package com.example.DTOs;

import java.util.List;

public class EventViewDetails {
    private Long eventId;
    private String eventName;
    private Integer participantsCount;
    private Integer duration;
    private String eventType;
    private String status;
    private List<RequestsViewDetails> requests;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RequestsViewDetails> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestsViewDetails> requests) {
        this.requests = requests;
    }
}
