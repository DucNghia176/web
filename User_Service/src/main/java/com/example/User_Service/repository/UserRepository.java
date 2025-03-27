package com.example.User_Service.repository;


import com.example.User_Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String name);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
