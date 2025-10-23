package com.example.user;

import com.example.DTOs.LogInRequest;
import com.example.DTOs.LogInResponse;

import java.util.List;

public interface UserService {

    UserInfo createUser(UserInfo userInfo);
    List<UserInfo> getAllUsers();
    UserInfo getUserById(Long id);
    void deleteUser(Long id);

    UserInfo getUserByCdsId(String cdsId) throws UserException;

    LogInResponse logIn(LogInRequest logInRequest) throws UserException;
}
