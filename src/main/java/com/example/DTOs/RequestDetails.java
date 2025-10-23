package com.example.DTOs;

public class RequestDetails {

    private String requestorId;
    private String justification;
    private String tanNo;
    private Integer noOfParticipants;
    private String deptName;
    private String curriculumLink;

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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCurriculumLink() {
        return curriculumLink;
    }

    public void setCurriculumLink(String curriculumLink) {
        this.curriculumLink = curriculumLink;
    }
}
