package com.example.user;

import com.example.DTOs.LogInRequest;
import com.example.DTOs.LogInResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserInfo createUser(UserInfo userInfo) {
        return userRepository.save(userInfo);
    }

    public List<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

    public UserInfo getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserInfo getUserByCdsId(String cdsId) throws UserException {
        Optional<UserInfo> fetchedUser =  userRepository.findByCdsID(cdsId);

        if(fetchedUser.isEmpty()) {
            throw new UserException("User not found with id: " + cdsId);
        }

        return fetchedUser.get();
    }

    @Override
    public LogInResponse logIn(LogInRequest logInRequest) throws UserException {

        LogInResponse logInResponse = new LogInResponse();


        UserInfo fetchedUser = getUserByCdsId(logInRequest.getCdsId());
        if(fetchedUser == null) {
            throw new UserException("User not found with id: " + logInRequest.getCdsId());
        }
        logInResponse.setCsdId(fetchedUser.getCdsID());
        logInResponse.setMessage("Valid User");
        logInResponse.setRole(fetchedUser.getRole());

        return logInResponse;
    }
}
