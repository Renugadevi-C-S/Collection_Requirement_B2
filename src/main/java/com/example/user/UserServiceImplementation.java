package com.example.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public  UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInfo addUser(UserInfo userInfo) {
        Optional<UserInfo> existingUser = userRepository.findByCdsID(userInfo.getCdsID());
        if (existingUser.isPresent()) {
            throw new UserException("User with cdsID '" + userInfo.getCdsID() + "' already exists.");
        }
        return userRepository.save(userInfo);
    }
}
