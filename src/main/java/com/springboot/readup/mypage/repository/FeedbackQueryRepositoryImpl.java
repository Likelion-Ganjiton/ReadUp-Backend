package com.springboot.readup.mypage.repository;

import com.springboot.readup.mypage.dto.FeedbackListItemDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FeedbackQueryRepositoryImpl implements FeedbackQueryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FeedbackListItemDto> findFeedbackList(Long userId) {

        String jpql = """
            SELECT new com.springboot.readup.mypage.dto.FeedbackListItemDto(
                us.id,
                n.title,
                n.category,
                f.aiScore,
                SUBSTRING(us.userSummary, 1, 40),
                us.createdAt
            )
            FROM UserSummaryEntity us
            JOIN AiFeedback f ON f.userSummaryId = us.id
            JOIN News n ON n.id = us.newsId
            WHERE us.userId = :userId
            ORDER BY us.createdAt DESC
            """;

        return em.createQuery(jpql, FeedbackListItemDto.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}