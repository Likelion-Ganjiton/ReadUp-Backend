package com.springboot.readup.vector.qdrant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QdrantRestService {

    @Value("${qdrant.url}")
    private String baseUrl;

    @Value("${qdrant.apiKey}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("api-key", apiKey);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    // 컬렉션 생성
    public void createCollection(String name) {

        String url = baseUrl + "/collections/" + name;

        Map<String, Object> body = Map.of(
                "vectors", Map.of(
                        "size", 4096,
                        "distance", "Cosine"
                )
        );

        rest.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers()), String.class);
    }

    // 포인트 저장
    public void upsert(String collection, long id, List<Float> vector, Map<String, Object> payload) {

        String url = baseUrl + "/collections/" + collection + "/points";

        Map<String, Object> body = Map.of(
                "points", List.of(
                        Map.of(
                                "id", id,
                                "vector", vector,
                                "payload", payload
                        )
                )
        );

        rest.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, headers()), String.class);
    }
}