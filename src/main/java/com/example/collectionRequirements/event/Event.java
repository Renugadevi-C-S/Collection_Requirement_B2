package com.example.collectionRequirements.event;

import com.example.collectionRequirements.request.Request;
import com.example.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
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
    private UserInfo createdBy;

    private LocalDate createdDate;

    private String completedBy;
    private String completionNotes;
    private LocalDate completedDate;

    private String cancelledBy;
    private String cancellationNotes;
    private LocalDate cancelledDate;

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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellationNotes() {
        return cancellationNotes;
    }

    public void setCancellationNotes(String cancellationNotes) {
        this.cancellationNotes = cancellationNotes;
    }

    public LocalDate getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(LocalDate cancelledDate) {
        this.cancelledDate = cancelledDate;
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
                ", createdDate=" + createdDate +
                ", completedBy='" + completedBy + '\'' +
                ", completedDate=" + completedDate +
                ", cancelledBy='" + cancelledBy + '\'' +
                ", cancelledDate=" + cancelledDate +
                '}';
    }
}
