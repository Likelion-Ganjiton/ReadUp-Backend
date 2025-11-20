package com.springboot.readup.like;

import com.springboot.readup.like.entity.LikeEntity;
import com.springboot.readup.like.repository.LikeRepository;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityLikeService {

    private final LikeRepository likeRepository;
    private final UserSummaryRepository userSummaryRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal == null || principal.equals("anonymousUser")) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String loginId = (String) principal;

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("로그인된 유저를 찾을 수 없습니다."));
    }

    public LikeToggle toggleLike(Long summaryId) {

        UserEntity me = getCurrentUser();

        // 요약글 존재 확인
        UserSummaryEntity summary = userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("요약글을 찾을 수 없습니다."));

        // 기존 좋아요 여부 확인
        LikeEntity like = likeRepository.findByUserIdAndSummaryId(me.getId(), summaryId);

        boolean nowLiked;

        if (like == null) {
            // 좋아요 추가
            likeRepository.save(
                    LikeEntity.builder()
                            .userId(me.getId())
                            .summaryId(summaryId)
                            .build()
            );
            nowLiked = true;
        } else {
            // 좋아요 취소
            likeRepository.delete(like);
            nowLiked = false;
        }

        // 최신 좋아요 수
        long likeCount = likeRepository.countBySummaryId(summaryId);

        // 명세서 맞춰 반환
        return LikeToggle.builder()
                .summaryId(summaryId)
                .liked(nowLiked)
                .likeCount(likeCount)
                .build();
    }
}