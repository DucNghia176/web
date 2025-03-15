package com.example.login_oracle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ✅ Cách đúng trong Spring Security 6.1+
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ Cho phép tất cả request không cần đăng nhập
                );
        return http.build();
    }
}


