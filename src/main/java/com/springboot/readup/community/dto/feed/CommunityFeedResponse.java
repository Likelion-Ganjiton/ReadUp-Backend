package com.springboot.readup.community.dto.feed;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityFeedResponse {

    private String category;
    private List<FeedSummaryResponse> summaries;

}