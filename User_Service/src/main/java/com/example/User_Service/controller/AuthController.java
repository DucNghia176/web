package com.example.User_Service.controller;

import com.example.User_Service.dto.request.AuthRequest;
import com.example.User_Service.dto.request.IntrospectRequest;
import com.example.User_Service.dto.request.ResetPasswordRequest;
import com.example.User_Service.dto.response.AuthResponse;
import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.dto.response.IntrospectResponse;
import com.example.User_Service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    // ✅ Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    // ✅ Kiểm tra token
    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        try {
            return ResponseEntity.ok(authService.introspect(request));
        } catch (Exception e) {
            log.error("Token introspection failed", e);
            return ResponseEntity.badRequest().body(new IntrospectResponse(false));
        }
    }

    // Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody IntrospectRequest request) {
        return authService.logout(request.getToken());
    }

    // Quên mật khẩu -> Gửi OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.getOrDefault("email", request.get("EMAIL"));
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    // Đặt lại mật khẩu sau khi nhập OTP
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        Object response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
