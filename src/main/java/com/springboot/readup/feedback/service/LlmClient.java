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
    핵심 내용 반영 정도, 논리적 흐름, 정보 누락 여부, 정확성을 평가하세요.

    반드시 다음 규칙을 지키세요:

    1. goodPoints는 핵심적인 "잘한 점"을 2개 작성하되, 
       구체적이고 자연스럽게 작성하세요.
    
    2. badPoints는 핵심적인 "개선할 점"을 2개 작성하되,
       단순 지적이 아니라 '왜 개선해야 하는지'가 드러나도록 작성하세요.

    3. feedback은 최소 3~5문장 이상으로 충분한 길이로 작성하세요.
       전체적인 평가와 개선 방향을 자연스럽게 제시해야 합니다.

    4. score는 벡터 유사도(similarity) 값에 따라 아래 기준표에 맞춰 산출하세요.
       similarity는 0~1 범위의 실수 값입니다.

       [점수 계산 규칙]
       - similarity ≥ 0.90  → score는 95~100 사이의 값으로 판단하여 적절한 정수를 사용하세요.
       - 0.80 ≤ similarity < 0.90 → 85~94
       - 0.70 ≤ similarity < 0.80 → 75~84
       - 0.60 ≤ similarity < 0.70 → 65~74
       - 0.50 ≤ similarity < 0.60 → 55~64
       - similarity < 0.50 → 40 이하의 점수로 판단하세요.

       점수는 반드시 위 구간 내의 정수로만 출력하세요.
       
    5. JSON 외의 문장, 설명, 해설은 절대 포함하지 마세요.

    아래 JSON 형식으로만 출력하세요:
    {
      "score": number,
      "goodPoints": ["문장1", "문장2"],
      "badPoints": ["문장1", "문장2"],
      "feedback": "총평 텍스트"
    }
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