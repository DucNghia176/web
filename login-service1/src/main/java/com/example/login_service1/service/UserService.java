package com.example.login_service1.service;

import com.example.login_service1.entity.User;
import com.example.login_service1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Kiểm tra email đã tồn tại chưa
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email); // Cần thêm phương thức này trong repository
    }

    // Kiểm tra tên đã tồn tại chưa
    public boolean isNameExist(String name) {
        return userRepository.existsByName(name); // Cần thêm phương thức này trong repository
    }

    public User createUser(User user){
        return userRepository.save(user);
    }
}
