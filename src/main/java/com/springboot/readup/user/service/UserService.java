package com.springboot.readup.user.service;

import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import com.springboot.readup.infra.MailService;
import com.springboot.readup.user.dto.UserResponse;
import com.springboot.readup.user.dto.UserSignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public UserResponse signup(UserSignupRequest req) {
        validateDuplicateFields(req);
        validatePasswordFormat(req.getPassword());

        UserEntity user = UserEntity.builder()
                .loginId(req.getLoginId())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .name(req.getName())
                .nickname(req.getNickname())
                .categories(req.getCategories())
                .build();

        UserEntity saved = userRepository.save(user);

        return UserResponse.builder()
                .userId(saved.getId())
                .loginId(saved.getLoginId())
                .nickname(saved.getNickname())
                .categories(saved.getCategories())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public void findIdAndSendEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));
        mailService.sendIdReminder(email, user.getLoginId());
    }

    private void validateDuplicateFields(UserSignupRequest req) {
        if (userRepository.findByLoginId(req.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.findByNickname(req.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (req.getCategories() != null && req.getCategories().size() > 2) {
            throw new IllegalArgumentException("관심 카테고리는 최대 2개까지 선택할 수 있습니다.");
        }
    }

    private void validatePasswordFormat(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$";
        if (!Pattern.matches(pattern, password)) {
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }
    }
}
