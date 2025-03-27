package com.example.User_Service.service;

import com.example.User_Service.dto.request.AuthRequest;
import com.example.User_Service.dto.request.IntrospectRequest;
import com.example.User_Service.dto.request.ResetPasswordRequest;
import com.example.User_Service.dto.response.AuthResponse;
import com.example.User_Service.dto.response.ErrorResponse;
import com.example.User_Service.dto.response.IntrospectResponse;
import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;
    UserService userService;
    EmailService emailService;

    UserRepository userRepository;
    Set<String> revokedTokens = new HashSet<>();
    Map<String, String> otpStorage = new HashMap<>();
    Map<String, Long> otpExpiry = new ConcurrentHashMap<>();
    static long OTP_EXPIRY_TIME = 5 * 60 * 1000;
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY =
            "/r/a3kh+1BLgXBAEU6dERcsXrzHZgWnsOqcnmxYDTxEMSa/6piUNFaoDWbmcE92K";


    private static final int TOKEN_EXPIRATION_HOURS = 1;

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("deviate.com")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.HOURS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Không thể tạo token: " + e.getMessage(), e);
        }
    }

    private String buildScope(User user) {
        return Optional.ofNullable(user.getRoles())
                .map(roles -> String.join(" ", roles.split(",")))
                .orElse("");
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        if (revokedTokens.contains(token)) {
            return IntrospectResponse.builder().valid(false).build(); // Token đã bị thu hồi
        }
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder().valid(verified && expiryTime.after(new Date())).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        System.out.println("Username nhận được: " + request.getUsername());
        if (request.getUsername() == null) {
            throw new RuntimeException("Username không được null!");
        }
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Đăng nhập thất bại: Mật khẩu không đúng");
        }

        // Tạo token JWT
        String token = generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }

    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            revokedTokens.add(token);
        }
    }
    public boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }

    // ✅ Quên mật khẩu -> Gửi OTP
    public Object forgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new ErrorResponse("Email not found", "No user found with this email");
        }

        // Tạo & gửi OTP
        String otpCode = emailService.generateVerificationCode();
        otpStorage.put(email, otpCode);
        otpExpiry.put(email, System.currentTimeMillis() + OTP_EXPIRY_TIME);
        emailService.sendEmail(email, "Reset Password Code", "Your OTP Code: " + otpCode);

        return "OTP code sent to your email.";
    }

    // ✅ Đặt lại mật khẩu sau khi nhập OTP
    public Object resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String storedOtp = otpStorage.get(email);
        Long expiryTime = otpExpiry.get(email);

        if (storedOtp == null || !storedOtp.equals(request.getOtp()) || expiryTime == null || System.currentTimeMillis() > expiryTime) {
            return new ErrorResponse("Invalid OTP", "The OTP code is incorrect or expired");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new ErrorResponse("Email not found", "No user found with this email");
        }

        // Cập nhật mật khẩu mới
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng
        otpStorage.remove(email);
        otpExpiry.remove(email);

        return "Password has been reset successfully.";
    }
}