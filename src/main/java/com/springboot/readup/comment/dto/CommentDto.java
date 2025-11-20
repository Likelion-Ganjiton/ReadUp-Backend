package com.springboot.readup.comment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long commentId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
}