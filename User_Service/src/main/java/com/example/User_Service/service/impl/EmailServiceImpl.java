package com.example.User_Service.service.impl;

import com.example.User_Service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email sent successfully to " + to); // In thông báo gửi thành công ra console
        } catch (Exception e) {
            // Log lỗi chi tiết nếu có vấn đề xảy ra khi gửi email
            System.err.println("Failed to send email to " + to);
            e.printStackTrace(); // In lỗi ra console để dễ dàng kiểm tra
        }
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Tạo số 6 chữ số
        return String.valueOf(code);
    }

}