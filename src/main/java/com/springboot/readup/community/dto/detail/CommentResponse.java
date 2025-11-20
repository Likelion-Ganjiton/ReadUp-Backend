package com.springboot.readup.community.dto.detail;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long commentId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private boolean isMyComment;

}