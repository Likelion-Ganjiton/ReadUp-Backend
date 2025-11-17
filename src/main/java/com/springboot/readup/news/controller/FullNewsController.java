package com.springboot.readup.news.controller;

import com.springboot.readup.news.dto.FullNewsResponse;
import com.springboot.readup.news.dto.FullNewsWrapperResponse;
import com.springboot.readup.news.service.FullNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class FullNewsController {

    private final FullNewsService fullNewsService;

    @GetMapping("/list")
    public ResponseEntity<FullNewsWrapperResponse> getFullNewsList(
            @AuthenticationPrincipal String loginId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size  // 한 페이지 5개 뉴스
    ) {

        // TODO: TodayNewsService에서 오늘 뉴스 ID 목록 가져오기
        List<Long> todayNewsIds = List.of(); // 임시: 빈 리스트

        // 뉴스 페이지 조회
        Page<FullNewsResponse> newsPage = fullNewsService.getFullNewsList(category, page, size, todayNewsIds);

        // 응답 DTO 구성
        FullNewsWrapperResponse response = FullNewsWrapperResponse.builder()
                .status(200)
                .message("전체 뉴스 목록을 불러왔습니다.")
                .data(FullNewsWrapperResponse.FullNewsData.builder()
                        .newsList(newsPage.getContent())
                        .page(page)
                        .size(size)
                        .totalElements(newsPage.getTotalElements())
                        .totalPages(newsPage.getTotalPages())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
