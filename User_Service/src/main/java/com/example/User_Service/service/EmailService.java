package com.example.User_Service.service;

import java.util.Random;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    String generateVerificationCode();
}