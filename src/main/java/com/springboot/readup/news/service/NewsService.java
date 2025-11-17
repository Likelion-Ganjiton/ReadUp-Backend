package com.springboot.readup.news.service;

import com.springboot.readup.news.dto.HomeNewsResponse;
import com.springboot.readup.news.dto.NewsDetailResponse;
import com.springboot.readup.news.dto.TodayNewsResponse;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TodayNewsResponse getTodayNews(String loginId) {

        // 로그인 아이디 → 유저 조회
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다. loginId=" + loginId));

        // 관심 카테고리
        List<String> categories = user.getCategories();
        if (categories == null || categories.isEmpty()) {
            throw new IllegalStateException("사용자의 관심 카테고리가 설정되어 있지 않습니다.");
        }

        // 뉴스 조회
        List<News> newsList = newsRepository.findByCategoryIn(categories);
        if (newsList.isEmpty()) {
            throw new IllegalStateException("해당 카테고리의 뉴스가 존재하지 않습니다.");
        }

        // 랜덤 선택
        News pick = newsList.get(random.nextInt(newsList.size()));

        return TodayNewsResponse.from(pick);
    }

    public NewsDetailResponse getNewsDetail(Long newsId) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. newsId=" + newsId));

        String publishDate = null;
        if (news.getPublishDate() != null) {
            publishDate = news.getPublishDate()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(SEOUL_ZONE)
                    .format(DATE_FORMAT);
        }

        return NewsDetailResponse.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .category(news.getCategory())
                .publishDate(publishDate)
                .url(news.getUrl())
                .build();
    }

    public News getNewsEntity(Long newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스가 존재하지 않습니다. newsId=" + newsId));
    }

    public List<HomeNewsResponse> getHomeNews() {
        List<News> newsList = newsRepository.findTop3ByOrderByPublishDateDesc();
        return newsList.stream()
                .map(HomeNewsResponse::from)
                .collect(Collectors.toList());
    }
}