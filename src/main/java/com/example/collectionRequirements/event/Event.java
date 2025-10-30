package com.example.collectionRequirements.event;

import com.example.collectionRequirements.request.Request;
import com.example.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue

    private Long eventId;

    private String eventName;

    private String description;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Request> requests;

    private Integer participantsCount;

    private Integer duration;

    private String eventType;

    private String fundingSource;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    UserInfo createdBy;

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

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

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Integer getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Integer noOfParticipants) {
        this.participantsCount = noOfParticipants;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer durationInHours) {
        this.duration = durationInHours;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String eventStatus) {
        this.status = eventStatus;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", eventDescription='" + description + '\'' +
                ", requests=" + requests +
                ", noOfParticipants=" + participantsCount +
                ", durationInHours=" + duration +
                ", eventType='" + eventType + '\'' +
                ", fundingSource=" + fundingSource +
                ", eventStatus='" + status + '\'' +
                '}';
    }
}
