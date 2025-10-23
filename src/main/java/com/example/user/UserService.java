package com.example.user;

import java.util.List;

public interface UserService {

    UserInfo createUser(UserInfo userInfo);
    List<UserInfo> getAllUsers();
    UserInfo getUserById(Long id);
    void deleteUser(Long id);

}
