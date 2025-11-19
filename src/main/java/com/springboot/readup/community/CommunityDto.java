package com.springboot.readup.community;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDto {

    private Long summaryId;
    private String newsTitle;
    private String category;
    private String writerName;
    private String summaryPreview;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;
}
