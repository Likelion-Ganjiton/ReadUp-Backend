package com.springboot.readup.news.service;

import com.springboot.readup.news.dto.TodayNewsResponse;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TodayNewsService {

    private final NewsService newsService;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public TodayNewsResponse getTodayFixedNews(String loginId) {

        // loginId → user 조회
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다. loginId=" + loginId));

        Long userId = user.getId();

        String today = LocalDate.now().toString();
        String key = "today_news:" + userId + ":" + today;

        // 1) Redis에 있는지 확인
        String savedNewsId = redisTemplate.opsForValue().get(key);

        if (savedNewsId != null) {
            Long newsId = Long.parseLong(savedNewsId);
            News news = newsService.getNewsEntity(newsId);
            return TodayNewsResponse.from(news);
        }

        // 2) 없으면 새로 랜덤 추천
        TodayNewsResponse pick = newsService.getTodayNews(loginId);

        // 3) Redis에 오늘의 뉴스 저장
        redisTemplate.opsForValue().set(
                key,
                String.valueOf(pick.getNewsId()),
                Duration.ofHours(24)
        );

        return pick;
    }
}