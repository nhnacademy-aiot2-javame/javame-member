//package com.nhnacademy.email.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendVerificationEmail(String toEmail, String code) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("[회사 인증] 이메일 인증 코드");
//        message.setText("인증 코드: " + code);
//        mailSender.send(message);
//    }
//}
