package com.springboot.readup.news.service;

import com.springboot.readup.news.dto.FullNewsResponse;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FullNewsService {

    private final NewsRepository newsRepository;

    private static final int SUMMARY_LENGTH = 100; // summary 길이

    public Page<FullNewsResponse> getFullNewsList(String category, int page, int size, List<Long> todayNewsIds) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishDate").descending());
        Page<News> newsPage;

        if (category == null || category.isEmpty() || category.equals("전체")) {
            newsPage = newsRepository.findAll(pageable);
        } else {
            newsPage = newsRepository.findAllByCategory(category, pageable);
        }

        // TodayNews 제외
        List<FullNewsResponse> newsList = newsPage.getContent().stream()
                .filter(n -> todayNewsIds == null || !todayNewsIds.contains(n.getId()))
                .map(n -> FullNewsResponse.from(n, SUMMARY_LENGTH))
                .collect(Collectors.toList());

        return new PageImpl<>(newsList, pageable, newsPage.getTotalElements());
    }
}