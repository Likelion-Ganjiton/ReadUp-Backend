package com.springboot.readup.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserLoginResponse {
    private int status;
    private String message;
    private TokenInfo token;
    private UserInfo user;

    @Getter
    @Builder
    public static class TokenInfo {
        private String accessToken;
        private long expiresIn;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private List<String> categories;
    }
}