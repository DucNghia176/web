package com.example.User_Service.controller;

import com.example.User_Service.entity.User;
import com.example.User_Service.service.AdminService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ Lấy danh sách user
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // ✅ Khóa tài khoản user
    @PutMapping("/lock-user/{id}")
    public ResponseEntity<Object> lockUser(@PathVariable Long id) {
        return adminService.lockUser(id);
    }

    // ✅ Mở khóa tài khoản user
    @PutMapping("/unlock-user/{id}")
    public ResponseEntity<Object> unlockUser(@PathVariable Long id) {
        return adminService.unlockUser(id);
    }
}
