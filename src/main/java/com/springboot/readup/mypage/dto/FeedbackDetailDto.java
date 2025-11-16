package com.springboot.readup.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeedbackDetailDto {

    private Long summaryId;

    // 뉴스 정보
    private String title;
    private String category;
    private Object publishDate;
    private String newsContent;

    // 사용자 요약문
    private String userSummary;

    // AI 평가 결과
    private Integer aiScore;
    private String goodPoints;
    private String badPoints;
    private String aiRevisedSummary;
}
