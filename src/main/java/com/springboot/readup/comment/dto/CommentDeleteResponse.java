package com.springboot.readup.comment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDeleteResponse {
    private Long commentId;
    private String message;
}