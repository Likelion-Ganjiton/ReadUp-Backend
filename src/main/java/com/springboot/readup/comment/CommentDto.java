package com.springboot.readup.comment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String writerName;
    private String content;
    private LocalDateTime createdAt;
}
