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

    @Value("${upstage.embedding.api.key}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();

    public List<Float> createEmbedding(String text) {

        String url = "https://api.upstage.ai/v1/embeddings";

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

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) res.getBody().get("data");

        Map<String, Object> first = data.get(0);

        List<Float> vector = ((List<?>) first.get("embedding"))
                .stream()
                .map(v -> ((Number) v).floatValue())
                .toList();

        return vector;
    }
}
