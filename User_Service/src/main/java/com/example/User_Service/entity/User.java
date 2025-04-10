package com.example.User_Service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "ID")
    Long id;

    @Column(name = "USERNAME")
    String username;

    @Column(name = "PASSWORD")
    String password;

    @Column(name = "NAME")
    String name;

    @Column(name = "EMAIL")
    String email;

    @Column(name = "DOB")
    LocalDate dob;

    @Column(name = "ROLES")
    String roles;

    private boolean locked;
}
