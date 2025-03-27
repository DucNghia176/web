package com.example.User_Service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @JsonProperty("PASSWORD")
    @Size(min=8, message = "password must be 8 characters")
    String password;

    @JsonProperty("NAME")
    String name;

    @JsonProperty("EMAIL")
    String email;

    @JsonProperty("DOB")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Format ngày tháng
    LocalDate dob;
}
