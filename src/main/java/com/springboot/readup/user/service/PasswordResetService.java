package com.springboot.readup.user.service;

import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private static class CodeInfo {
        String code;
        long expireTime;
    }

    private final ConcurrentHashMap<String, CodeInfo> codeStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> verifiedEmailMap = new ConcurrentHashMap<>();

    public void sendVerificationCode(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        if (!exists) {
            throw new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다.");
        }

        String code = generateCode();

        CodeInfo info = new CodeInfo();
        info.code = code;
        info.expireTime = System.currentTimeMillis() + (5 * 60 * 1000);

        codeStorage.put(email, info);

        sendEmail(email, "[READUP] 비밀번호 재설정 인증 코드",
                "다음 인증 코드를 입력해주세요 (5분 유효): " + code);

        log.info("이메일 [{}]로 전송된 인증 코드: {} (5분 유효)", email, code);
    }

    public boolean verifyCode(String email, String code) {
        CodeInfo stored = codeStorage.get(email);
        if (stored == null) return false;
        if (System.currentTimeMillis() > stored.expireTime) {
            codeStorage.remove(email);
            return false;
        }
        if (stored.code.equals(code)) {
            verifiedEmailMap.put(email, true);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String newPassword) {
        String email = verifiedEmailMap.keySet().stream()
                .filter(verifiedEmailMap::get)
                .findFirst()
                .orElse(null);

        if (email == null) return false;

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        clearVerification(email);
        return true;
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }

    public boolean isVerified(String email) {
        return verifiedEmailMap.getOrDefault(email, false);
    }

    public void clearVerification(String email) {
        verifiedEmailMap.remove(email);
        codeStorage.remove(email);
    }
}
