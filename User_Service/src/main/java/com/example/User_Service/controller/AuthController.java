package com.example.User_Service.controller;

import com.example.User_Service.dto.request.AuthRequest;
import com.example.User_Service.dto.request.IntrospectRequest;
import com.example.User_Service.dto.request.ResetPasswordRequest;
import com.example.User_Service.dto.response.AuthResponse;
import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.dto.response.IntrospectResponse;
import com.example.User_Service.entity.User;
import com.example.User_Service.service.AuthService;
import com.example.User_Service.service.EmailService;
import com.example.User_Service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    private final AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> otpStorage = new HashMap<>();
    // Lưu OTP với thời gian hết hạn
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();

    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 phút
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {

        try {
            // Gọi phương thức authenticate từ AuthService để đăng nhập và lấy token
            AuthResponse authResponse = authService.authenticate(request);
                return ResponseEntity.ok(authResponse); // Trả về token và trạng thái thành công
        } catch (Exception e) {
            // Xử lý lỗi và trả về thông báo lỗi nếu đăng nhập không thành công
            return ResponseEntity.status(401)
                    .body(AuthResponse.builder()
                            .token(null)
                            .username(null)
                            .roles(null)
                            .authenticated(false)
                            .build());
        }
    }

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        try {
            // Gọi phương thức introspect từ AuthService để kiểm tra tính hợp lệ của token
            IntrospectResponse introspectResponse = authService.introspect(request);
            return ResponseEntity.ok(introspectResponse); // Trả về kết quả kiểm tra token
        } catch (Exception e) {
            // Xử lý lỗi và trả về kết quả không hợp lệ nếu có lỗi trong quá trình kiểm tra
            return ResponseEntity.status(400).body(new IntrospectResponse(false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody IntrospectRequest request) {
        String token = request.getToken();

        // Kiểm tra nếu token đã bị thu hồi trước đó
        if (authService.isTokenRevoked(token)) {
            return ResponseEntity.status(400).body("Bạn đã đăng xuất trước đó.");
        }

        try {
            authService.logout(token);
            return ResponseEntity.ok("Đăng xuất thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Đăng xuất thất bại.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody Map<String, String> request) {
        System.out.println("Received request: " + request); // Debug request đầu vào

        String email = request.get("email");
        if (email == null) {
            email = request.get("EMAIL");
        }
        System.out.println("Extracted email: " + email); // Debug email

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email", "Email is required"));
        }
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email not found", "No user found with this email"));
        }

        // Tạo OTP
        String otpCode = generateOTP();
        otpStorage.put(email, otpCode);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_EXPIRY_TIME);

        // Gửi OTP qua email
        emailService.sendEmail(email, "Reset Password Code", "Your OTP Code: " + otpCode);

        return ResponseEntity.ok("OTP code sent to your email.");
    }

    // ✅ Đặt lại mật khẩu sau khi nhập OTP
    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        String email = request.getEmail();
        String storedOtp = otpStorage.get(email);
        Long expiryTime = otpExpiry.get(email);

        // Kiểm tra OTP có hợp lệ không
        if (storedOtp == null || !storedOtp.equals(request.getOtp()) || expiryTime == null || System.currentTimeMillis() > expiryTime) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid OTP", "The OTP code is incorrect or expired"));
        }

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email not found", "No user found with this email"));
        }

        // Cập nhật mật khẩu mới
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateUser(user);

        // Xóa OTP sau khi sử dụng
        otpStorage.remove(email);
        otpExpiry.remove(email);

        return ResponseEntity.ok("Password has been reset successfully.");
    }

    // ✅ Tạo OTP ngẫu nhiên 6 số
    private String generateOTP() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
