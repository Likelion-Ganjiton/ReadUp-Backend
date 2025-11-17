package com.springboot.readup.news.service;

import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsPayloadFixService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${qdrant.url}")
    private String qdrantUrl;

    @Value("${qdrant.apiKey}")
    private String qdrantApiKey;

    @Value("${qdrant.collection}")
    private String collection;

    public void fixPayloads() {

        List<News> allNews = newsRepository.findAll();

        for (News news : allNews) {

            // Qdrant payload update body
            Map<String, Object> body = Map.of(
                    "filter", Map.of(
                            "must", List.of(
                                    Map.of("has_id", List.of(news.getId()))
                            )
                    ),
                    "payload", Map.of(
                            "newsId", news.getId(),
                            "type", "news"
                    ),
                    "overwrite_payload", true // 기존 payload 완전 교체
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", qdrantApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(body, headers);

            String endpoint = qdrantUrl + "/collections/" + collection + "/points/payload";

            try {
                restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
                System.out.println("✔ Updated payload for news " + news.getId());
            } catch (Exception e) {
                System.out.println("❌ FAILED for news " + news.getId());
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("🎉 모든 payload 업데이트 완료!");
    }
}
