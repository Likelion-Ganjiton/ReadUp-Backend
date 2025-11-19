package com.springboot.readup.like.repository;

import com.springboot.readup.like.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    LikeEntity findByUserIdAndSummaryId(Long userId, Long summaryId);

    long countBySummaryId(Long summaryId);

    @Query("""
        SELECT l.summaryId, COUNT(l)
        FROM LikeEntity l
        WHERE l.summaryId IN :summaryIds
        GROUP BY l.summaryId
    """)
    List<Object[]> countLikesGroupBySummaryIds(List<Long> summaryIds);
}