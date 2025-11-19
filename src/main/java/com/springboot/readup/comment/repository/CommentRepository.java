package com.springboot.readup.comment.repository;

import com.springboot.readup.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findBySummaryIdOrderByCreatedAtDesc(Long summaryId);

    long countBySummaryId(Long summaryId);

    CommentEntity findById(long id);

    @Query("""
        SELECT c.summaryId, COUNT(c)
        FROM CommentEntity c
        WHERE c.summaryId IN :summaryIds
        GROUP BY c.summaryId
    """)
    List<Object[]> countCommentsGroupBySummaryIds(List<Long> summaryIds);
}