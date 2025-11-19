package com.springboot.readup.community.dto.detail;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private Long id;
    private String title;
    private String category;
    private String content;
    private LocalDateTime publishDate;
}