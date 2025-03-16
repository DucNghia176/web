package com.example.login_service1.controller;

import com.example.login_service1.dto.request.AuthRequest;
import com.example.login_service1.dto.request.IntrospectRequest;
import com.example.login_service1.dto.response.AuthResponse;
import com.example.login_service1.dto.response.IntrospectResponse;
import com.example.login_service1.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    private final AuthService authService;

    /**
     * Endpoint đăng nhập và trả về JWT token nếu thành công.
     *
     * @param request Chứa username và password để đăng nhập.
     * @return ResponseEntity chứa token nếu đăng nhập thành công.
     */
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

    /**
     * Endpoint kiểm tra tính hợp lệ của token.
     *
     * @param request Chứa token cần kiểm tra.
     * @return ResponseEntity chứa kết quả kiểm tra tính hợp lệ của token.
     */
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

}
