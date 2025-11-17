package com.springboot.readup.like;

import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityLikeService {

    private final LikeRepository likeRepository;
    private final UserSummaryRepository userSummaryRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<UserEntity> user = userRepository.findByLoginId(loginId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("로그인된 유저를 찾을 수 없습니다.");
        }

        return user.orElse(null);
    }

    public LikeToggle toggleLike(Long summaryId) {
        UserEntity me = getCurrentUser();

        // 요약글 존재 확인
        Optional<UserSummaryEntity> summary = userSummaryRepository.findById(summaryId);
        if (summary.isEmpty()) {
            throw new IllegalArgumentException("요약글을 찾을 수 없습니다.");
        }

        // 내가 이 글에 눌러둔 좋아요 있는지 확인
        LikeEntity like = likeRepository.findByUserIdAndSummaryId(me.getId(), summaryId);

        boolean nowLiked;

        if (like == null) {
            // 아직 안 눌렀으면 → 새로 추가
            LikeEntity newLike = LikeEntity.builder()
                    .userId(me.getId())
                    .summaryId(summaryId)
                    .build();
            likeRepository.save(newLike);
            nowLiked = true;
        } else {
            // 이미 눌렀으면 → 삭제 (좋아요 취소)
            likeRepository.delete(like);
            nowLiked = false;
        }

        long likeCount = likeRepository.countBySummaryId(summaryId);

        return LikeToggle.builder()
                .liked(nowLiked)
                .likeCount(likeCount)
                .build();
    }
}
