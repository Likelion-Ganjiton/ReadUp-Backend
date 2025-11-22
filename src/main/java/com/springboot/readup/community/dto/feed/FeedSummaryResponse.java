package com.springboot.readup.community.dto.feed;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedSummaryResponse {

    private Long summaryId;
    private Long newsId;
    private String category;
    private String title;
    private String userName;
    private String userSummary;
    private int clarityScore;
    private long likeCount;
    private long commentCount;
    private String createdAtText;
    private boolean likedByMe;
}
