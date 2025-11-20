package com.springboot.readup.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryListResponse {
    private List<String> categories;
}