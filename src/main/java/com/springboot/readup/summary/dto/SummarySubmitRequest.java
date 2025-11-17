package com.springboot.readup.summary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummarySubmitRequest {
    private Long newsId;
    private String userSummary;
    private String visibility;   // public / private
}