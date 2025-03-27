package com.example.User_Service.service;

import com.example.User_Service.dto.request.AuthRequest;
import com.example.User_Service.dto.request.IntrospectRequest;
import com.example.User_Service.dto.response.AuthResponse;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    UserRepository userRepository;
    private final Set<String> revokedTokens = new HashSet<>();
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

}