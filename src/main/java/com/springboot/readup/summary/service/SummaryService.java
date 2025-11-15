package com.springboot.readup.summary.service;

import com.springboot.readup.summary.dto.SummaryCheckResponse;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final UserSummaryRepository summaryRepository;
    private final UserRepository userRepository;

    public SummaryCheckResponse checkSummary(Long newsId) {

        // 1) 토큰에서 loginId 추출
        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 2) loginId → userId 찾기
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 3) user_summary 조회
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
}