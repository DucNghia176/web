package com.example.User_Service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
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
    @Size(min=8, message = "password must be 8 characters")
    String password;

    @JsonProperty("ROLES")
    String roles;
}
