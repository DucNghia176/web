package com.example.login_service1.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @JsonProperty("USERNAME")
    String username;

    @JsonProperty("PASSWORD")
    String password;

    @JsonProperty("NAME")
    String name;

    @JsonProperty("EMAIL")
    String email;

    @JsonProperty("DOB")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Format ngày tháng
    LocalDate dob;

    @JsonProperty("ROLES")
    String roles;
}

