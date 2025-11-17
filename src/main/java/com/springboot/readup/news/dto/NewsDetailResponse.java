package com.springboot.readup.news.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDetailResponse {

    private Long newsId;
    private String title;
    private String content;
    private String category;
    private String publishDate;
    private String url;
}