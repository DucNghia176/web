package com.example.User_Service.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}