package com.springboot.readup.summary.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryCheckResponse {

    private boolean summaryExists;
    private Long summaryId;   // 없으면 null
}