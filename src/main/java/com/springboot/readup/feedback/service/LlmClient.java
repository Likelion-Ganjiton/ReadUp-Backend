package com.springboot.readup.feedback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LlmClient {

    @Value("${upstage.llm.api.key}")
    private String apiKey;

    @Value("${upstage.llm.url}")
    private String llmUrl;

    private static final String MODEL = "solar-pro2-250909";
    private final RestTemplate rest = new RestTemplate();

    private static final String SYSTEM_PROMPT = """
            당신은 뉴스 요약 평가 전문가입니다.
            입력된 원문 기사와 사용자가 작성한 요약문을 비교하여
            핵심 정보 반영 정도, 논리성, 정보 누락, 왜곡 여부를 객관적으로 평가하세요.

            출력 시 JSON 형식만 반환해야 합니다.
            다른 문장이나 설명은 절대 포함하지 마세요.
            """;

    public String requestFeedback(String originText, String summary, Float similarity) {

        String userPrompt = """
                [원문 뉴스]
                %s

                [사용자 요약]
                %s

                [벡터 유사도 점수]
                %s

                아래 JSON 형식으로만 답변하세요:

                {
                  "score": number,
                  "goodPoints": ["문장1", "문장2"],
                  "badPoints": ["문장1", "문장2"],
                  "feedback": "총평 텍스트"
                }

                JSON만 반환하세요.
                """.formatted(originText, summary, similarity);

        Map<String, Object> body = Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        Map result = rest.postForObject(llmUrl, entity, Map.class);

        // LLM 응답 파싱
        Map firstChoice = (Map) ((List) result.get("choices")).get(0);
        Map message = (Map) firstChoice.get("message");

        return (String) message.get("content");
    }
}