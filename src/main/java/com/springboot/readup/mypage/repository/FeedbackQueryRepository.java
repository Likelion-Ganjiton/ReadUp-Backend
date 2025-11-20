package com.springboot.readup.mypage.repository;

import com.springboot.readup.mypage.dto.FeedbackListItemDto;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackQueryRepository extends JpaRepository<UserSummaryEntity, Long> {

    @Query("""
        SELECT new com.springboot.readup.mypage.dto.FeedbackListItemDto(
            s.id,
            n.title,
            n.category,
            COALESCE(f.aiScore, 0),
            SUBSTRING(s.userSummary, 1, 40),
            s.createdAt,
            COUNT(l.id)
        )
        FROM UserSummaryEntity s
        JOIN News n ON s.newsId = n.id
        LEFT JOIN AiFeedback f ON f.userSummaryId = s.id
        LEFT JOIN LikeEntity l ON l.summaryId = s.id
        WHERE s.userId = :userId
        GROUP BY s.id, n.title, n.category, f.aiScore, s.userSummary, s.createdAt
        ORDER BY s.createdAt DESC
    """)
    List<FeedbackListItemDto> findFeedbackList(Long userId);
}