package com.springboot.readup.like;

import com.springboot.readup.like.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    LikeEntity findByUserIdAndSummaryId(Long userId, Long summaryId);

    long countBySummaryId(Long summaryId);
}
