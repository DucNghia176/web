package com.example.User_Service.service;

import com.example.User_Service.entity.User;
import com.example.User_Service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email); // Cần thêm phương thức này trong repository
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Kiểm tra tên đã tồn tại chưa
    public boolean isNameExist(String name) {
        return userRepository.existsByName(name); // Cần thêm phương thức này trong repository
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

}
