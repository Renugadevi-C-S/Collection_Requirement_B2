package com.example.user;

import com.example.DTOs.LogInRequest;
import com.example.DTOs.LogInResponse;
import com.example.DTOs.NewUserInfo;

import java.util.List;

public interface UserService {

    UserInfo createUser(NewUserInfo newUserInfo);
    List<UserInfo> getAllUsers();
    UserInfo getUserById(Long id);
    void deleteUser(Long id);

    UserInfo getUserByCdsId(String cdsId) throws UserException;

    LogInResponse logIn(LogInRequest logInRequest) throws UserException;
}
