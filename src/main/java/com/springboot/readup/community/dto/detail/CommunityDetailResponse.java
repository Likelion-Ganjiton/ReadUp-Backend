package com.springboot.readup.community.dto.detail;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDetailResponse {

    private NewsResponse news;
    private SummaryDetailResponse summary;
    private List<CommentResponse> comments;

}