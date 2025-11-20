package com.springboot.readup.mypage.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class AlertRequestDto {
    private List<AlertTimeDto> alertTimes;
}