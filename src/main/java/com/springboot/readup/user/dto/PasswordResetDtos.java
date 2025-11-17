package com.springboot.readup.user.dto;

import lombok.Data;

public class PasswordResetDtos {

    @Data
    public static class EmailRequest {
        private String email;
    }

    @Data
    public static class VerifyCodeRequest {
        private String email;
        private String code;
    }

    @Data
    public static class ResetPasswordRequest {
        private String newPassword;
    }
}
