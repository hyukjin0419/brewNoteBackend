//package com.pard.server.brewnotebackend.global.utils;
//
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendEmail(String to, String subject, String body) {
//        MimeMessage message = mailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true); // true 설정 시 HTML 사용 가능
//
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            // 예외 처리 로직 (로그 기록 등)
//            throw new RuntimeException("메일 발송 실패", e);
//        }
//    }
//}