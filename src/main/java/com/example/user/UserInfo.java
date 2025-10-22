package com.example.user;

import com.example.collectionRequirements.approval.Approval;
import com.example.collectionRequirements.request.Request;
import com.example.department.Department;
import com.example.region.Region;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class UserInfo {

    @Id
    @GeneratedValue
    private Long userId;

    private String firstName;
    private String lastName;

    private String email;

    @ManyToOne
    private Department department;

    private String role;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserInfo getManager() {
        return manager;
    }

    public void setManager(UserInfo manager) {
        this.manager = manager;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    @ManyToOne
    private Region region;

    @OneToOne
    @JoinColumn
    private UserInfo manager;

    @ManyToMany(mappedBy = "requestedParticipants")
    private List<Request> requests;

    @OneToOne(mappedBy = "approvedBy")
    private Approval approval;

}
