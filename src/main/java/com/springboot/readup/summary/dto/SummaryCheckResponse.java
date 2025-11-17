package com.springboot.readup.summary.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryCheckResponse {

    private boolean summaryExists; // 요약 존재 여부
    private Long summaryId;        // 없으면 null
}