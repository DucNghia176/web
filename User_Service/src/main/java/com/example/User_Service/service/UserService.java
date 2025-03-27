package com.example.User_Service.service;

import com.example.User_Service.dto.request.UserCreateRequest;
import com.example.User_Service.dto.request.UserUpdateRequest;
import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<Object> createUser(UserCreateRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return badRequest("Username is required", "Username cannot be empty");
            }
            if (!isValidEmail(request.getEmail())) {
                return badRequest("Invalid email format", "Please provide a valid email address");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                return badRequest("Email already exists", "The email address is already in use");
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                return badRequest("Name already exists", "The name is already in use");
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .email(request.getEmail())
                    .roles(request.getRoles())
                    .dob(request.getDob())
                    .build();

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateUser(UserUpdateRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return badRequest("Email is required", "Email cannot be empty");
        }

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return badRequest("User not found", "No user found with this email");
        }

        User user = optionalUser.get();
        if (request.getName() != null) user.setName(request.getName());
        if (request.getDob() != null) user.setDob(request.getDob());

        return ResponseEntity.ok(userRepository.save(user));
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return userRepository.findById(id)
                .<ResponseEntity<Object>>map(user -> ResponseEntity.ok().body(user)) // Trả về user nếu tìm thấy
                .orElseGet(() -> ResponseEntity.badRequest().body(
                        new ErrorResponse("User not found", "No user found with this ID")
                )); // Trả về lỗi nếu không tìm thấy
    }



    public Object getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ErrorResponse("No users found", "User list is empty") : users;
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("User not found", "No user found with ID: " + id));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Delete failed", e.getMessage()));
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private ResponseEntity<Object> badRequest(String error, String message) {
        return ResponseEntity.badRequest().body(new ErrorResponse(error, message));
    }

}
