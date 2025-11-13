package com.springboot.readup.news.dto;

import lombok.Data;

@Data
public class AIHubNewsItem {
    private String title;
    private String subtitle;
    private String content;
    private String board;
    private String writer;
    private String write_date;
    private String url;
    private String source_site;
}
