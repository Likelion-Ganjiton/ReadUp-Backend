package com.springboot.readup.news.controller;

import com.springboot.readup.news.service.NewsEmbeddingBatchService;
import com.springboot.readup.vector.qdrant.QdrantRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsEmbeddingController {

    private final QdrantRestService qdrantRestService;
    private final NewsEmbeddingBatchService newsBatchService;

    @Value("${qdrant.collection}")
    private String collectionName;

    // Qdrant 컬렉션 생성
    @PostMapping("/collection/init")
    public String initCollection() {
        qdrantRestService.createCollection(collectionName);
        return "Collection created: " + collectionName;
    }

    // 전체 뉴스 임베딩 실행
    @PostMapping("/embed-all")
    public String embedAll() {
        int count = newsBatchService.embedAllNews();
        return count + "개 뉴스 임베딩 완료";
    }
}