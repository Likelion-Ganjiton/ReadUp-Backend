package com.springboot.readup.mypage.repository;

import com.springboot.readup.mypage.dto.FeedbackListItemDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackQueryRepository {

    @Query("""
        SELECT new com.springboot.readup.mypage.dto.FeedbackListItemDto(
            s.id,
            n.title,
            n.category,
            COALESCE(f.aiScore, 0),
            SUBSTRING(s.userSummary, 1, 40),
            s.createdAt
        )
        FROM UserSummaryEntity s
        JOIN News n ON s.newsId = n.id
        LEFT JOIN AiFeedback f ON f.userSummaryId = s.id
        WHERE s.userId = :userId
        ORDER BY s.createdAt DESC
    """)
    List<FeedbackListItemDto> findFeedbackList(Long userId);
}
