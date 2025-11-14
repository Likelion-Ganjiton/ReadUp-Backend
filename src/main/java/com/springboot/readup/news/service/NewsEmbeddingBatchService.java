package com.springboot.readup.news.service;

import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import com.springboot.readup.vector.embedding.EmbeddingService;
import com.springboot.readup.vector.qdrant.QdrantRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsEmbeddingBatchService {

    private final NewsRepository newsRepository;
    private final EmbeddingService embeddingService;
    private final QdrantRestService qdrantRestService;

    @Value("${qdrant.collection}")
    private String collection;

    public int embedAllNews() {

        List<News> newsList = newsRepository.findAll();
        int count = 0;

        for (News news : newsList) {
            try {
                List<Float> vector = embeddingService.embed(news.getContent());

                qdrantRestService.upsert(
                        collection,
                        news.getId(),
                        vector,
                        Map.of(
                                "title", news.getTitle(),
                                "content", news.getContent(),
                                "category", news.getCategory(),
                                "url", news.getUrl()
                        )
                );

                count++;

            } catch (Exception e) {
                System.err.println("❌ News ID " + news.getId() + " 임베딩 실패");
            }
        }

        return count;
    }
}