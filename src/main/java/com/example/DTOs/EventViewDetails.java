package com.example.DTOs;

import java.util.List;

public class EventViewDetails {
    private Long eventId;
    private String eventName;
    private String description;
    private Integer duration;
    private String eventType;
    private String fundingSource;
    private Integer participantsCount;
    private String status;
    private String createdBy;
    private List<RequestsViewDetails> linkedRequests;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<RequestsViewDetails> getLinkedRequests() {
        return linkedRequests;
    }

    public void setLinkedRequests(List<RequestsViewDetails> linkedRequests) {
        this.linkedRequests = linkedRequests;
    }
}
