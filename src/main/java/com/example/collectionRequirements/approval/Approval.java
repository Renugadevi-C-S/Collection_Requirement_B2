package com.example.collectionRequirements.approval;

import com.example.collectionRequirements.request.Request;
import com.example.user.UserInfo;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Approval {

    @Id
    @GeneratedValue
    private Long approvalId;

    @OneToOne
    @JoinColumn
    private Request request;

    @OneToOne
    @JoinColumn
    private UserInfo approvedBy;

    private LocalDate approvalDate;

    public UserInfo getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UserInfo approvedBy) {
        this.approvedBy = approvedBy;
    }

    private String approvalStatus;

    private String approvalNotes;

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "Approval{" +
                "approvalId=" + approvalId +
                ", request=" + request +
                ", approvalDate=" + approvalDate +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", approvalNotes='" + approvalNotes + '\'' +
                '}';
    }
}
