package com.springboot.readup.news.service;

import com.springboot.readup.news.dto.NewsDetailResponse;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsDetailResponse getNewsDetail(Long newsId) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. newsId=" + newsId));

        String formattedDate = null;
        if (news.getPublishDate() != null) {
            formattedDate = news.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return NewsDetailResponse.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .category(news.getCategory())
                .publishDate(formattedDate)
                .url(news.getUrl())
                .build();
    }
}