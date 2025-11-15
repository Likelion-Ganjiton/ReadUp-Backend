package com.springboot.readup.summary.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarySubmitResponse {

    private Long summaryId;
    private String message;
    private String status;
    private String next;
}