package com.springboot.readup.feedback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.readup.feedback.dto.AiFeedbackResponseDto;
import com.springboot.readup.feedback.entity.AiFeedback;
import com.springboot.readup.feedback.repository.AiFeedbackRepository;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import com.springboot.readup.vector.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiFeedbackService {

    private final UserRepository userRepository;
    private final UserSummaryRepository userSummaryRepository;
    private final NewsRepository newsRepository;
    private final AiFeedbackRepository aiFeedbackRepository;

    private final EmbeddingService embeddingService;
    private final LlmClient llmClient;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${qdrant.url}")
    private String qdrantUrl;

    @Value("${qdrant.apiKey}")
    private String qdrantApiKey;

    @Value("${qdrant.collection}")
    private String COLLECTION;

    public AiFeedbackResponseDto evaluateSummary(Long summaryId) throws Exception {

        // 1. JWT에서 로그인 ID 가져오기
        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 2. User 조회
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long userId = user.getId();

        // 3. 요약문 조회
        UserSummaryEntity summary = userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("Summary not found"));

        // 4. 본인 요약문인지 검증
        if (!summary.getUserId().equals(userId)) {
            throw new RuntimeException("You cannot evaluate another user's summary.");
        }

        // 5. 뉴스 조회
        News news = newsRepository.findById(summary.getNewsId())
                .orElseThrow(() -> new RuntimeException("News not found"));

        // 6. 요약문 임베딩 생성
        List<Float> summaryVector = embeddingService.embed(summary.getUserSummary());

        // 7. Qdrant 유사도 계산
        Float similarity = searchSimilarity(summaryVector, summary.getNewsId());

        log.info("🟦 Final Similarity (Qdrant) = {}", similarity);

        // 8. LLM 평가 요청
        String llmJson = llmClient.requestFeedback(
                news.getContent(),
                summary.getUserSummary(),
                similarity
        );

        // 9. JSON → DTO
        AiFeedbackResponseDto dto = mapper.readValue(llmJson, AiFeedbackResponseDto.class);

        // 10. 평가 DB 저장
        aiFeedbackRepository.save(
                AiFeedback.builder()
                        .userSummaryId(summaryId)
                        .similarityScore(similarity)
                        .aiScore(dto.getScore())
                        .goodPoints(String.join("||", dto.getGoodPoints()))
                        .badPoints(String.join("||", dto.getBadPoints()))
                        .feedback(dto.getFeedback())
                        .build()
        );

        return dto;
    }

    private Float searchSimilarity(List<Float> queryVector, Long newsId) {

        String url = qdrantUrl + "/collections/" + COLLECTION + "/points/search";

        Map<String, Object> body = Map.of(
                "vector", queryVector,
                "top", 1,
                "filter", Map.of(
                        "must", List.of(
                                Map.of("has_id", List.of(newsId))
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", qdrantApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        Map response = rest.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        ).getBody();

        List<Map<String, Object>> result =
                (List<Map<String, Object>>) response.get("result");

        Float similarity =
                ((Number) result.get(0).get("score")).floatValue();

        log.info("🔍 Qdrant similarity score = {}", similarity);

        return similarity;
    }
}
