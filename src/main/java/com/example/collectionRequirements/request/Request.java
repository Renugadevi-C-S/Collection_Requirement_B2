package com.example.collectionRequirements.request;

import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.event.Event;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Request {

    @Id
    @GeneratedValue
    private Long requestId;

//    @OneToOne
//    @JoinColumn
//    private UserInfo requestor;

//    @ManyToOne
//    private Department department;

    @ManyToOne
    @JoinColumn(name = "eventId")
    private Event event;

    @OneToOne(mappedBy = "request")
    private Approval approval;

    private LocalDate requestDate;

    private String requestStatus;

    private Boolean groupRequest;

    private String justification;

    private Integer noOfParticipants;

//    @ManyToMany
//    @JoinTable(
//            name = "RequestedParticipants",
//            joinColumns = @JoinColumn(name = "Request_Id", referencedColumnName = "requestId"),
//            inverseJoinColumns = @JoinColumn(name = "User_Id", referencedColumnName = "userId")
//    )
//    private List<UserInfo> requestedParticipants;

    private String TAN_Number;

    private String curriculumLink;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
//                ", department=" + department +
//                ", eventId=" + eventId +
                ", requestDate=" + requestDate +
                ", requestStatus='" + requestStatus + '\'' +
                ", groupRequest=" + groupRequest +
                ", justification='" + justification + '\'' +
                ", TAN_Number='" + TAN_Number + '\'' +
                ", curriculumLink='" + curriculumLink + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(requestId, request.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requestId);
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }


    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Boolean getGroupRequest() {
        return groupRequest;
    }

    public void setGroupRequest(Boolean groupRequest) {
        this.groupRequest = groupRequest;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getTAN_Number() {
        return TAN_Number;
    }

    public void setTAN_Number(String TAN_Number) {
        this.TAN_Number = TAN_Number;
    }

    public String getCurriculumLink() {
        return curriculumLink;
    }

    public void setCurriculumLink(String curriculumLink) {
        this.curriculumLink = curriculumLink;
    }
}
