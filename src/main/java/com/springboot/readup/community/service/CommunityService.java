package com.springboot.readup.community.service;

import com.springboot.readup.comment.entity.CommentEntity;
import com.springboot.readup.comment.repository.CommentRepository;
import com.springboot.readup.community.dto.detail.*;
import com.springboot.readup.community.dto.feed.*;
import com.springboot.readup.feedback.entity.AiFeedback;
import com.springboot.readup.feedback.repository.AiFeedbackRepository;
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

    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저를 찾을 수 없습니다."));
    }

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

    public CommunityFeedResponse getFeedByCategory(String category) {

        UserEntity me = getCurrentUser();

        List<News> newsList = newsRepository.findByCategory(category);
        if (newsList.isEmpty()) {
            return CommunityFeedResponse.builder()
                    .category(category)
                    .summaries(new ArrayList<>())
                    .build();
        }

        List<Long> newsIds = newsList.stream()
                .map(News::getId)
                .toList();

        List<UserSummaryEntity> summaries =
                userSummaryRepository.findByNewsIdIn(newsIds);

        if (summaries.isEmpty()) {
            return CommunityFeedResponse.builder()
                    .category(category)
                    .summaries(new ArrayList<>())
                    .build();
        }

        List<Long> summaryIds = summaries.stream()
                .map(UserSummaryEntity::getId)
                .toList();

        List<Object[]> likeCountRows = likeRepository.countLikesGroupBySummaryIds(summaryIds);
        Map<Long, Long> likeCountMap = new HashMap<>();
        for (Object[] row : likeCountRows) {
            likeCountMap.put((Long) row[0], (Long) row[1]);
        }

        List<Object[]> commentCountRows = commentRepository.countCommentsGroupBySummaryIds(summaryIds);
        Map<Long, Long> commentCountMap = new HashMap<>();
        for (Object[] row : commentCountRows) {
            commentCountMap.put((Long) row[0], (Long) row[1]);
        }

        Map<Long, Integer> aiScoreMap = new HashMap<>();
        for (Long sId : summaryIds) {
            aiFeedbackRepository.findByUserSummaryId(sId)
                    .ifPresent(a -> aiScoreMap.put(sId, a.getAiScore()));
        }

        List<FeedSummaryResponse> result = new ArrayList<>();

        for (UserSummaryEntity summary : summaries) {

            News news = newsList.stream()
                    .filter(n -> n.getId().equals(summary.getNewsId()))
                    .findFirst()
                    .orElse(null);
            if (news == null) continue;

            UserEntity writer = userRepository.findById(summary.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));

            boolean likedByMe =
                    likeRepository.findByUserIdAndSummaryId(me.getId(), summary.getId()) != null;

            result.add(
                    FeedSummaryResponse.builder()
                            .summaryId(summary.getId())
                            .newsId(news.getId())
                            .category(news.getCategory())
                            .title(news.getTitle())
                            .userName(writer.getNickname())
                            .userSummary(summary.getUserSummary())
                            .clarityScore(aiScoreMap.getOrDefault(summary.getId(), 0))
                            .likeCount(likeCountMap.getOrDefault(summary.getId(), 0L))
                            .commentCount(commentCountMap.getOrDefault(summary.getId(), 0L))
                            .createdAtText(convertToRelativeTime(summary.getCreatedAt()))
                            .likedByMe(likedByMe)   // ⭐ 추가됨
                            .build()
            );
        }

        return CommunityFeedResponse.builder()
                .category(category)
                .summaries(result)
                .build();
    }

    public CommunityDetailResponse getSummaryDetail(Long summaryId) {

        UserEntity me = getCurrentUser();

        UserSummaryEntity summary = userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("요약글을 찾을 수 없습니다."));

        News news = newsRepository.findById(summary.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        UserEntity writer = userRepository.findById(summary.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        long likeCount = likeRepository.countBySummaryId(summaryId);
        boolean likedByMe =
                likeRepository.findByUserIdAndSummaryId(me.getId(), summaryId) != null;

        int clarityScore = aiFeedbackRepository.findByUserSummaryId(summaryId)
                .map(AiFeedback::getAiScore)
                .orElse(0);

        List<CommentEntity> commentEntities =
                commentRepository.findBySummaryIdOrderByCreatedAtDesc(summaryId);

        List<CommentResponse> comments = new ArrayList<>();

        for (CommentEntity c : commentEntities) {

            String writerName = userRepository.findById(c.getUserId())
                    .map(UserEntity::getNickname)
                    .orElse("알 수 없음");

            boolean isMyComment = c.getUserId().equals(me.getId());

            comments.add(
                    CommentResponse.builder()
                            .commentId(c.getId())
                            .userName(writerName)
                            .content(c.getContent())
                            .createdAt(c.getCreatedAt())
                            .isMyComment(isMyComment)
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
                .userName(writer.getNickname())
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