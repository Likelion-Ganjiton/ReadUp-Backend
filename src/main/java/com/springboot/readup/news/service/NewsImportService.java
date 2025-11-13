package com.springboot.readup.news.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.readup.news.dto.AIHubNewsItem;
import com.springboot.readup.news.dto.AIHubNewsResponse;
import com.springboot.readup.news.dto.AIHubNewsWrapper;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NewsImportService {

    private final NewsRepository newsRepository;

    public void importJsonFile(String filePath) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        AIHubNewsWrapper wrapper =
                mapper.readValue(new File(filePath), AIHubNewsWrapper.class);

        AIHubNewsResponse response = wrapper.getSJML();

        if (response == null || response.getText() == null) return;

        for (AIHubNewsItem item : response.getText()) {

            if (newsRepository.existsByUrl(item.getUrl())) continue;

            LocalDateTime publishDate = null;
            try {
                publishDate = LocalDateTime.parse(
                        item.getWrite_date(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );
            } catch (Exception ignored) {}

            String cleanedContent = cleanContent(item.getContent());

            News news = News.builder()
                    .title(item.getTitle())
                    .content(cleanedContent)
                    .category("IT/과학")
                    .url(item.getUrl())
                    .publishDate(publishDate)
                    .build();

            newsRepository.save(news);
        }
    }

    private String cleanContent(String content) {
        if (content == null) return null;

        String cleaned = content;

        cleaned = cleaned.replaceAll(
                "^\\[[^\\]]*뉴시스[^\\]]*\\]\\s*\\([^\\)]*\\)\\s*기자\\s*=", ""
        );

        cleaned = cleaned.replaceAll(
                "^\\([^\\)]*뉴스1[^\\)]*\\)\\s*\\([^\\)]*\\)\\s*기자\\s*=", ""
        );

        cleaned = cleaned.replaceAll(
                "^\\([^\\)]*뉴스1[^\\)]*\\)\\s*기자\\s*=", ""
        );

        cleaned = cleaned.replaceAll(
                "^\\([^\\)]*뉴스1[^\\)]*\\)\\s*=", ""
        );

        cleaned = cleaned.replaceAll(
                "^[가-힣A-Za-z]+\\s*기자\\s*=", ""
        );

        cleaned = cleaned.replaceAll("^=+\\s*", "");

        cleaned = cleaned.replaceAll("\\.(\\s*\\.)+", ". ");

        cleaned = cleaned.replaceAll("\\.{3,}", ". ");

        cleaned = cleaned.replaceAll("\\.(?=[^\\s])", ". ");

        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }
}
