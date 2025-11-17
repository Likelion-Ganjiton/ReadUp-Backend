package com.springboot.readup.news.dto;

import com.springboot.readup.news.entity.News;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class FullNewsResponse {

    private Long newsId;
    private String title;
    private String category;
    private String publishDate;
    private String url;
    private String summary;

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static FullNewsResponse from(News news, int summaryLength) {
        String formattedDate = null;
        if (news.getPublishDate() != null) {
            formattedDate = news.getPublishDate()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(SEOUL_ZONE)
                    .format(DATE_FORMAT);
        }

        String summary = "";
        if (news.getContent() != null) {
            summary = news.getContent().length() > summaryLength
                    ? news.getContent().substring(0, summaryLength) + "..."
                    : news.getContent();
        }

        return FullNewsResponse.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .category(news.getCategory())
                .publishDate(formattedDate)
                .url(news.getUrl())
                .summary(summary)
                .build();
    }
}