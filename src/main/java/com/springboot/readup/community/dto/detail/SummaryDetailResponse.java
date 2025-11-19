package com.springboot.readup.community.dto.detail;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryDetailResponse {
    private Long summaryId;
    private String userName;
    private String userSummary;
    private int clarityScore;      // 추후 확장 가능 (지금은 0 또는 제외 가능)
    private long likeCount;
    private long commentCount;
    private boolean isLikedByMe;
    private LocalDateTime createdAt;
}