package com.springboot.readup.mypage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateResponse {
    private String message;
    private List<String> categories;
}