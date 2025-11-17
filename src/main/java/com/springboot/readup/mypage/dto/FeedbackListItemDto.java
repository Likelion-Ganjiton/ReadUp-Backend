package com.springboot.readup.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedbackListItemDto {

    private Long summaryId;
    private String articleTitle;
    private String category;
    private Integer score;
    private String summaryPreview;
    private LocalDateTime createdAt;
}