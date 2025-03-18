package com.example.User_Service.controller;

import com.example.User_Service.dto.request.UserRequest;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest request) {
        try {
            System.out.println("Received request: " + request); // Log request để debug

            // Kiểm tra nếu username rỗng
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username is required", "Username cannot be empty"));
            }

            // Kiểm tra định dạng email
            if (!isValidEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email format", "Please provide a valid email address"));
            }

            // Kiểm tra email đã tồn tại trong database
            if (userService.isEmailExist(request.getEmail())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Email already exists", "The email address is already in use"));
            }

            // Kiểm tra tên đã tồn tại chưa
            if (userService.isNameExist(request.getName())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Name already exists", "The name is already in use"));
            }
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            // Chuyển từ request thành entity
            User user = User.builder()
                    .username(request.getUsername())
                    .password(hashedPassword)
                    .name(request.getName())
                    .email(request.getEmail())
                    .roles(request.getRoles())
                    .dob(request.getDob())
                    .build();

            // Lưu user vào DB
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Internal Server Error", "An unexpected error occurred"));
        }
    }


    // Phương thức kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
