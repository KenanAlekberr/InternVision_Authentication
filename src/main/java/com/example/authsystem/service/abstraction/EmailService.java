package com.example.authsystem.service.abstraction;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}