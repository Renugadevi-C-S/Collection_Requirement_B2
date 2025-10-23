package com.example.user;

import com.example.DTOs.LogInRequest;
import com.example.DTOs.LogInResponse;
import com.example.collectionRequirements.request.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserInfo createUser(@RequestBody UserInfo userInfo) {
        return userService.createUser(userInfo);
    }

    @GetMapping("/cdsId/{cdsId}")
    public UserInfo getUserByCdsId(@PathVariable("cdsId") String cdsId) {
        return userService.getUserByCdsId(cdsId);
    }


    @GetMapping("/all")
    public List<UserInfo> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserInfo getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/login")
    public LogInResponse login(@RequestBody LogInRequest logInRequest) throws RequestException {
        return userService.logIn(logInRequest);
    }

}
