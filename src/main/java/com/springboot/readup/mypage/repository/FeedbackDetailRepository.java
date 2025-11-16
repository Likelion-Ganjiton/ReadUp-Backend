package com.springboot.readup.mypage.repository;

import com.springboot.readup.mypage.dto.FeedbackDetailDto;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackDetailRepository extends JpaRepository<UserSummaryEntity, Long> {

    @Query("""
        SELECT new com.springboot.readup.mypage.dto.FeedbackDetailDto(
            s.id,
            n.title,
            n.category,
            n.publishDate,
            n.content,
            s.userSummary,
            f.aiScore,
            f.goodPoints,
            f.badPoints,
            f.aiRevisedSummary
        )
        FROM UserSummaryEntity s
        JOIN News n ON s.newsId = n.id
        JOIN AiFeedback f ON f.userSummaryId = s.id
        WHERE s.id = :summaryId
    """)
    FeedbackDetailDto findDetail(Long summaryId);
}
