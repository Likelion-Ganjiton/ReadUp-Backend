package com.springboot.readup.mypage.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MypageStatsResponse {

    private int totalSummaries;
    private Double averageScore;
    private int streakDays;
}