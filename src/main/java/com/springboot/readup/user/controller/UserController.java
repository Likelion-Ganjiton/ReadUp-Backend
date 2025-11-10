package com.springboot.readup.user.controller;

import com.springboot.readup.user.dto.*;
import com.springboot.readup.user.service.PasswordResetService;
import com.springboot.readup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest req) {
        try {
            UserResponse res = userService.signup(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", 201,
                    "message", "회원가입이 완료되었습니다.",
                    "data", res
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", 400,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.findIdAndSendEmail(email);

        return ResponseEntity.ok(Map.of(
                "message", "입력한 이메일로 아이디 안내 메일을 발송했습니다."
        ));
    }

    @PostMapping("/find-password")
    public ResponseEntity<?> sendResetCode(@RequestBody PasswordResetDtos.EmailRequest request) {
        passwordResetService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("코드가 전송되었습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody PasswordResetDtos.VerifyCodeRequest request) {
        boolean success = passwordResetService.verifyCode(request.getEmail(), request.getCode());
        return success
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.badRequest().body("코드 불일치");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDtos.ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(request.getNewPassword());
        return success
                ? ResponseEntity.ok("비밀번호 변경 완료")
                : ResponseEntity.badRequest().body("이메일 인증이 완료되지 않았습니다.");
    }
}
