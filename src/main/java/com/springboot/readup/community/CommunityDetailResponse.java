package com.springboot.readup.community;

import com.springboot.readup.comment.CommentDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDetailResponse {

    private Long summaryId;

    private String newsTitle;
    private String newsCategory;
    private String writerName;
    private String summaryText;
    private LocalDateTime createdAt;

    private long likeCount;
    private boolean likedByMe;

    private List<CommentDto> comments;
}
