package com.springboot.readup.mypage.service;

import com.springboot.readup.mypage.dto.*;
import com.springboot.readup.mypage.repository.SummaryStatisticsRepository;
import com.springboot.readup.mypage.repository.FeedbackQueryRepository;
import com.springboot.readup.mypage.repository.FeedbackDetailRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final SummaryStatisticsRepository summaryRepo;
    private final UserRepository userRepository;
    private final FeedbackQueryRepository feedbackQueryRepository;
    private final FeedbackDetailRepository feedbackDetailRepository;

    public MypageStatsResponse getStats() {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Long userId = user.getId();

        int total = summaryRepo.countSummaries(userId);
        Double avg = summaryRepo.averageScore(userId);
        if (avg == null) avg = 0.0;

        int streak = summaryRepo.streakDays(userId);

        return MypageStatsResponse.builder()
                .totalSummaries(total)
                .averageScore(avg)
                .streakDays(streak)
                .build();
    }

    private static final List<String> VALID_CATEGORIES = List.of(
            "경제","정치","사회","국제","문화","스포츠","IT/과학","생활/건강"
    );

    public CategoryUpdateResponse updateCategories(CategoryUpdateRequest req) {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        List<String> categories = req.getCategories();

        if (categories.size() > 2) {
            throw new IllegalArgumentException("관심 카테고리는 최대 2개까지 선택 가능합니다.");
        }

        for (String c : categories) {
            if (!VALID_CATEGORIES.contains(c)) {
                throw new IllegalArgumentException("유효하지 않은 카테고리: " + c);
            }
        }

        user.setCategories(categories);
        userRepository.save(user);

        return CategoryUpdateResponse.builder()
                .message("관심 카테고리가 저장되었습니다.")
                .categories(categories)
                .build();
    }

    public FeedbackListResponse getFeedbackList() {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Long userId = user.getId();

        List<FeedbackListItemDto> items = feedbackQueryRepository.findFeedbackList(userId);

        return FeedbackListResponse.builder()
                .totalCount(items.size())
                .feedbacks(items)
                .build();
    }

    public FeedbackDetailDto getFeedbackDetail(Long summaryId) {

        String loginId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        FeedbackDetailDto detail = feedbackDetailRepository.findDetail(summaryId);

        if (detail == null) {
            throw new IllegalArgumentException("해당 요약에 대한 피드백이 존재하지 않습니다.");
        }

        return detail;
    }
}