package com.springboot.readup.summary.service;

import com.springboot.readup.summary.dto.SummaryCheckResponse;
import com.springboot.readup.summary.dto.SummarySubmitRequest;
import com.springboot.readup.summary.dto.SummarySubmitResponse;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import com.springboot.readup.vector.qdrant.QdrantRestService;
import com.springboot.readup.vector.upstage.UpstageEmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final UserSummaryRepository summaryRepository;
    private final UserRepository userRepository;
    private final UpstageEmbeddingService embeddingService;
    private final QdrantRestService qdrantService;

    @Value("${qdrant.collection}")
    private String COLLECTION;

    public SummaryCheckResponse checkSummary(Long newsId) {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        return summaryRepository.findByUserIdAndNewsId(user.getId(), newsId)
                .map(summary -> SummaryCheckResponse.builder()
                        .summaryExists(true)
                        .summaryId(summary.getId())
                        .build()
                )
                .orElseGet(() -> SummaryCheckResponse.builder()
                        .summaryExists(false)
                        .build()
                );
    }

    public SummarySubmitResponse submitSummary(SummarySubmitRequest req) {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        summaryRepository.findByUserIdAndNewsId(user.getId(), req.getNewsId())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("이미 해당 뉴스에 대한 요약을 제출했습니다.");
                });

        UserSummaryEntity saved = summaryRepository.save(
                UserSummaryEntity.builder()
                        .userId(user.getId())
                        .newsId(req.getNewsId())
                        .userSummary(req.getUserSummary())
                        .visibility(req.getVisibility())
                        .build()
        );

        Long summaryId = saved.getId();

        List<Float> vector = embeddingService.createEmbedding(req.getUserSummary());

        long vectorId = summaryId + 1_000_000;

        qdrantService.upsert(
                COLLECTION,
                vectorId,
                vector,
                Map.of(
                        "type", "summary",
                        "summaryId", summaryId,
                        "newsId", req.getNewsId(),
                        "userId", user.getId()
                )
        );

        return SummarySubmitResponse.builder()
                .summaryId(summaryId)
                .message("요약문이 성공적으로 제출되었습니다.")
                .status("processing")
                .next("/api/feedback/" + summaryId)
                .build();
    }
}