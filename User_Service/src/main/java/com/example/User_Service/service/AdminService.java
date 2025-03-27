package com.example.User_Service.service;

import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Lấy danh sách tất cả user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Khóa tài khoản user
    public ResponseEntity<Object> lockUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User not found", "No user found with this ID"));
        }

        User user = optionalUser.get();
        user.setLocked(true); // Đánh dấu user bị khóa
        userRepository.save(user);
        return ResponseEntity.ok("User has been locked.");
    }

    // ✅ Mở khóa tài khoản user
    public ResponseEntity<Object> unlockUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("User not found", "No user found with this ID"));
        }

        User user = optionalUser.get();
        user.setLocked(false); // Đánh dấu user được mở khóa
        userRepository.save(user);
        return ResponseEntity.ok("User has been unlocked.");
    }
}
