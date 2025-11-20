package com.springboot.readup.news.dto;

import com.springboot.readup.news.entity.News;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class HomeNewsResponse {

    private Long newsId;
    private String title;
    private String category;
    private String publishDate;
    private String url;

    private String summary;

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static HomeNewsResponse from(News news) {

        String formattedDate = null;
        if (news.getPublishDate() != null) {
            formattedDate = news.getPublishDate()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(SEOUL_ZONE)
                    .format(DATE_FORMAT);
        }

        String content = news.getContent();
        String summary = null;
        if (content != null) {
            summary = content.length() > 120 ? content.substring(0, 120) + "..." : content;
        }

        return HomeNewsResponse.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .category(news.getCategory())
                .publishDate(formattedDate)
                .url(news.getUrl())
                .summary(summary)
                .build();
    }
}