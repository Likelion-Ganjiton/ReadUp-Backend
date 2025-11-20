package com.springboot.readup.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class AlertListResponseDto {
    private List<String> alertTimes;
}