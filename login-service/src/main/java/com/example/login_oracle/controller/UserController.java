package com.example.login_oracle.controller;

import com.example.login_oracle.entity.User;
import com.example.login_oracle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("roles") String roles,
            @RequestParam("date") String date // Giả sử ngày được truyền dưới dạng String
    ) {
        try {
            // Chuyển đổi chuỗi "date" thành LocalDate với định dạng yyyy-MM-dd
            LocalDate localDate = LocalDate.parse(date);

            // Tạo đối tượng User từ các tham số nhận được
            User user = User.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .roles(roles)
                    .date(localDate)
                    .build();

            // Gọi service để lưu user vào cơ sở dữ liệu
            User newUser = userService.createUser(user);

            // Trả về ResponseEntity với dữ liệu người dùng mới
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            // Nếu có lỗi trong quá trình chuyển đổi ngày, trả về lỗi với ResponseEntity<User>
            return ResponseEntity.badRequest().body(null); // Trả về lỗi 400 nếu không thể chuyển đổi ngày
        }
    }
}
