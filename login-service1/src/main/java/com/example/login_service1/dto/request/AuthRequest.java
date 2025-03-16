package com.example.login_service1.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequest {
    @JsonProperty("USERNAME")
    String username;

    @JsonProperty("PASSWORD")
    String password;

    @JsonProperty("ROLES")
    String roles;
}
