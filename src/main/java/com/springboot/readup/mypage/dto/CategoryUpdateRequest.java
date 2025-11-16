package com.springboot.readup.mypage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryUpdateRequest {
    private List<String> categories;
}