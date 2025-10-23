package com.example.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
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

}
