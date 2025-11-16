package com.springboot.readup.mypage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedbackListResponse {

    private int totalCount;
    private List<FeedbackListItemDto> feedbacks;
}
