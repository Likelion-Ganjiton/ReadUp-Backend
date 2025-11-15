package com.springboot.readup.vector.upstage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpstageEmbeddingService {

    @Value("${upstage.api.key}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();

    public List<Float> createEmbedding(String text) {

        String url = "https://api.upstage.ai/v1/embeddings";

        // input은 배열 형태로 보내는 것이 Upstage 공식 표준
        Map<String, Object> body = Map.of(
                "model", "embedding-passage",
                "input", List.of(text)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> res = rest.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        // ⚠ data는 List임
        List<Map<String, Object>> data = (List<Map<String, Object>>) res.getBody().get("data");

        // 첫 번째 벡터 접근
        Map<String, Object> first = data.get(0);

        // embedding은 List<Double>
        List<Double> raw = (List<Double>) first.get("embedding");

        // Double → Float 변환
        return raw.stream()
                .map(Double::floatValue)
                .toList();
    }
}
