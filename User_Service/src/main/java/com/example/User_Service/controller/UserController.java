package com.example.User_Service.controller;

import com.example.User_Service.dto.request.UserCreateRequest;
import com.example.User_Service.dto.request.UserUpdateRequest;
import com.example.User_Service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
