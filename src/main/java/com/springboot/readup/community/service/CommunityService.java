package com.springboot.readup.community.service;

import com.springboot.readup.comment.entity.CommentEntity;
import com.springboot.readup.comment.repository.CommentRepository;
import com.springboot.readup.community.dto.detail.*;
import com.springboot.readup.community.dto.feed.*;
import com.springboot.readup.feedback.entity.AiFeedback;
import com.springboot.readup.feedback.repository.AiFeedbackRepository;
import com.springboot.readup.like.entity.LikeEntity;
import com.springboot.readup.like.repository.LikeRepository;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserSummaryRepository userSummaryRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AiFeedbackRepository aiFeedbackRepository;

    // 현재 로그인 사용자 조회
    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저를 찾을 수 없습니다."));
    }

    // 상대시간 계산
    private String convertToRelativeTime(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        return days + "일 전";
    }

    // 커뮤니티 피드 조회
    public CommunityFeedResponse getFeedByCategory(String category) {

        // 1) 뉴스 목록
        List<News> newsList = newsRepository.findByCategory(category);
        if (newsList.isEmpty()) {
            return CommunityFeedResponse.builder()
                    .category(category)
                    .summaries(new ArrayList<>())
                    .build();
        }

        // 뉴스 ID만 추출
        List<Long> newsIds = newsList.stream()
                .map(News::getId)
                .toList();

        // 2) 요약문 모두 가져오기
        List<UserSummaryEntity> summaries =
                userSummaryRepository.findByNewsIdIn(newsIds);

        if (summaries.isEmpty()) {
            return CommunityFeedResponse.builder()
                    .category(category)
                    .summaries(new ArrayList<>())
                    .build();
        }

        // summaryId 리스트
        List<Long> summaryIds = summaries.stream()
                .map(UserSummaryEntity::getId)
                .toList();

        // 3) 좋아요 카운트를 summaryId 기준으로 한 번에 조회
        List<Object[]> likeCountRows = likeRepository.countLikesGroupBySummaryIds(summaryIds);

        Map<Long, Long> likeCountMap = new HashMap<>();
        for (Object[] row : likeCountRows) {
            Long sId = (Long) row[0];
            Long cnt = (Long) row[1];
            likeCountMap.put(sId, cnt);
        }

        // 4) 댓글 카운트도 한 번에 조회
        List<Object[]> commentCountRows = commentRepository.countCommentsGroupBySummaryIds(summaryIds);

        Map<Long, Long> commentCountMap = new HashMap<>();
        for (Object[] row : commentCountRows) {
            Long sId = (Long) row[0];
            Long cnt = (Long) row[1];
            commentCountMap.put(sId, cnt);
        }

        // 5) AI 점수도 summaryId 기준으로 조회(있으면 사용)
        Map<Long, Integer> aiScoreMap = new HashMap<>();
        for (Long sId : summaryIds) {
            aiFeedbackRepository.findByUserSummaryId(sId)
                    .ifPresent(a -> aiScoreMap.put(sId, a.getAiScore()));
        }

        // 6) DTO 변환
        List<FeedSummaryResponse> result = new ArrayList<>();

        for (UserSummaryEntity summary : summaries) {

            News news = newsList.stream()
                    .filter(n -> n.getId().equals(summary.getNewsId()))
                    .findFirst()
                    .orElse(null);
            if (news == null) continue;

            UserEntity writer = userRepository.findById(summary.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));

            result.add(
                    FeedSummaryResponse.builder()
                            .summaryId(summary.getId())
                            .newsId(news.getId())
                            .category(news.getCategory())
                            .title(news.getTitle())
                            .userName(writer.getLoginId())
                            .userSummary(summary.getUserSummary())
                            .clarityScore(aiScoreMap.getOrDefault(summary.getId(), 0))
                            .likeCount(likeCountMap.getOrDefault(summary.getId(), 0L))
                            .commentCount(commentCountMap.getOrDefault(summary.getId(), 0L))
                            .createdAtText(convertToRelativeTime(summary.getCreatedAt()))
                            .build()
            );
        }

        return CommunityFeedResponse.builder()
                .category(category)
                .summaries(result)
                .build();
    }

    // 요약문 상세보기
    public CommunityDetailResponse getSummaryDetail(Long summaryId) {

        UserEntity me = getCurrentUser();

        // 1) 요약문
        UserSummaryEntity summary = userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("요약글을 찾을 수 없습니다."));

        // 2) 뉴스
        News news = newsRepository.findById(summary.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        // 3) 작성자
        UserEntity writer = userRepository.findById(summary.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        // 4) 좋아요 수 / 내가 좋아요 눌렀는지
        long likeCount = likeRepository.countBySummaryId(summaryId);
        boolean likedByMe =
                likeRepository.findByUserIdAndSummaryId(me.getId(), summaryId) != null;

        // 5) AI 점수 (optional)
        int clarityScore = aiFeedbackRepository.findByUserSummaryId(summaryId)
                .map(AiFeedback::getAiScore)
                .orElse(0);

        // 6) 댓글 전체 조회
        List<CommentEntity> commentEntities =
                commentRepository.findBySummaryIdOrderByCreatedAtDesc(summaryId);

        List<CommentResponse> comments = new ArrayList<>();

        for (CommentEntity c : commentEntities) {
            String writerName = userRepository.findById(c.getUserId())
                    .map(UserEntity::getLoginId)
                    .orElse("알 수 없음");

            comments.add(
                    CommentResponse.builder()
                            .commentId(c.getId())
                            .userName(writerName)
                            .content(c.getContent())
                            .createdAt(c.getCreatedAt())
                            .build()
            );
        }

        NewsResponse newsResponse = NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .category(news.getCategory())
                .content(news.getContent())
                .publishDate(news.getPublishDate())
                .build();

        SummaryDetailResponse summaryResponse = SummaryDetailResponse.builder()
                .summaryId(summary.getId())
                .userName(writer.getLoginId())
                .userSummary(summary.getUserSummary())
                .clarityScore(clarityScore)
                .likeCount(likeCount)
                .commentCount((long) commentEntities.size())
                .isLikedByMe(likedByMe)
                .createdAt(summary.getCreatedAt())
                .build();

        return CommunityDetailResponse.builder()
                .news(newsResponse)
                .summary(summaryResponse)
                .comments(comments)
                .build();
    }
}