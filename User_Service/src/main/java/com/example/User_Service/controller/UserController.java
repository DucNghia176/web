package com.example.User_Service.controller;

import com.example.User_Service.dto.request.UserCreateRequest;
import com.example.User_Service.dto.request.UserUpdateRequest;
import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.entity.User;
import com.example.User_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody UserUpdateRequest request) {
        return userService.updateUser(request);
    }
}
