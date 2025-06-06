//package com.nhnacademy;
//
//import com.nhnacademy.email.service.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/email")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendEmail(@RequestParam String email) {
//        String code = generateCode();
//        emailService.sendVerificationEmail(email, code);
//        return ResponseEntity.ok("인증 코드 전송 완료: " + code); // 테스트용
//    }
//
//    private String generateCode() {
//        return String.valueOf((int)(Math.random() * 900000) + 100000);
//    }
//}
