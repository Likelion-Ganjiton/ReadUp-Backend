package com.springboot.readup.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String loginId;
    private String nickname;
    private List<String> categories;
    private LocalDateTime createdAt;
}