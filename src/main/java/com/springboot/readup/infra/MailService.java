package com.springboot.readup.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendIdReminder(String to, String loginId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("[ReadUp] 아이디 안내");
        message.setText(
                "안녕하세요.\n\n" +
                        "요청하신 아이디는 다음과 같습니다:\n\n" +
                        loginId + "\n\n" +
                        "감사합니다."
        );
        mailSender.send(message);
    }
}