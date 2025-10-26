package com.example.DTOs;

import java.time.LocalDate;

public class RequestsViewDetails {

    private Long RequestId;
    private String department;
    private String eventName;
    private LocalDate requestDate;
    private String justification;
    private Integer noOfParticipants;
    private String requestStatus;

    public Long getRequestId() {
        return RequestId;
    }

    public void setRequestId(Long requestId) {
        RequestId = requestId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(Integer noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }


    //    "requestId": 1,
//            "department": null,
//            "event": null,
//            "approval": null,
//            "requestDate": "2025-10-22",
//            "requestStatus": "Submitted",
//            "groupRequest": true,
//            "justification": "For Java Training",
//            "noOfParticipants": 14,
//            "requestedParticipants": null,
//            "curriculumLink": "Sample Link",
//            "tan_Number": "TANNO123"
}
