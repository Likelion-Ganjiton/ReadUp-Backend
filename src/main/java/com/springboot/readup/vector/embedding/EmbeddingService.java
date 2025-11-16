package com.springboot.readup.vector.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${upstage.embedding.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.upstage.ai/v1/embeddings";

    public List<Float> embed(String text) {

        Map<String, Object> body = Map.of(
                "model", "embedding-passage",
                "input", List.of(text)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                API_URL,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map first = (Map) ((List) response.getBody().get("data")).get(0);
        List<?> vector = (List<?>) first.get("embedding");

        return vector.stream().map(v -> ((Number) v).floatValue()).toList();
    }
}