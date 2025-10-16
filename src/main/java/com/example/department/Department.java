package com.example.department;

import com.example.collectionRequirements.request.Request;
import com.example.user.UserInfo;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Department {

    @Id
    @GeneratedValue
    private Long departmentId;

    private String departmentName;

    @OneToOne(mappedBy = "manager")
    private UserInfo manager;

    @OneToMany(mappedBy = "department")
    private List<UserInfo> users;

//    @OneToOne
//    private Budget budget;

    @OneToMany(mappedBy = "department")
    private List<Request> requests;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public UserInfo getManager() {
        return manager;
    }

    public void setManager(UserInfo manager) {
        this.manager = manager;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", manager=" + manager +
                ", users=" + users +
                ", requests=" + requests +
                '}';
    }
}
