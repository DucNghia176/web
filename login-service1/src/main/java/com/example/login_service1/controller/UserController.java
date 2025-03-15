package com.example.login_oracle.controller;

import com.example.login_oracle.dto.request.UserRequest;
import com.example.login_oracle.entity.User;
import com.example.login_oracle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) { // CHỈ DÙNG @RequestBody
        try {
            System.out.println("Received request: " + request); // Log request để debug
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            // Chuyển từ request thành entity
            User user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .name(request.getName())
                    .email(request.getEmail())
                    .roles(request.getRoles())
                    .dob(request.getDob())
                    .build();

            // Lưu user vào DB
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
