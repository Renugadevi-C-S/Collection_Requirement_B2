package com.example.DTOs;

public class RequestDetails {

    private String requestorId;
    private String justification;
    private String tanNo;
    private Integer noOfParticipants;
    private String department;
    private String curriculum;
    private String[] usersCdsId;

    public String[] getUsersCdsId() {
        return usersCdsId;
    }

    public void setUsersCdsId(String[] usersCdsId) {
        this.usersCdsId = usersCdsId;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getTanNo() {
        return tanNo;
    }

    public void setTanNo(String tanNo) {
        this.tanNo = tanNo;
    }

    public Integer getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(Integer noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }
}
