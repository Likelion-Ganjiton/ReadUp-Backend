package com.springboot.readup.mypage.repository;

import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.feedback.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SummaryStatisticsRepository extends JpaRepository<UserSummaryEntity, Long> {

    // 총 요약 수
    @Query("SELECT COUNT(s) FROM UserSummaryEntity s WHERE s.userId = :userId")
    int countSummaries(Long userId);

    // 평균 점수
    @Query("SELECT AVG(f.aiScore) FROM AiFeedback f WHERE f.userSummaryId IN (SELECT s.id FROM UserSummaryEntity s WHERE s.userId = :userId)")
    Double averageScore(Long userId);

    // 연속 학습일수 (전날까지 하루 한 개 이상 작성한 기록을 세는 방식)
    @Query(
            value = """
        SELECT COUNT(*) 
        FROM (
            SELECT DATE(created_at) AS d
            FROM user_summary 
            WHERE user_id = :userId
            GROUP BY DATE(created_at)
            HAVING d >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
        ) AS t
        """,
            nativeQuery = true
    )
    int streakDays(Long userId);
}