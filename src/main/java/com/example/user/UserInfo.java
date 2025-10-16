package com.example.user;

import jakarta.persistence.*;

@Entity
public class UserInfo {

    @Id
    @GeneratedValue
    private Long userId;

    private String firstName;
    private String lastName;

    private String email;

//    @ManyToOne
//    private Department department;

    private String role;

//    @ManyToOne
//    private Region region;

//    @OneToOne
//    private UserInfo manager;

}
