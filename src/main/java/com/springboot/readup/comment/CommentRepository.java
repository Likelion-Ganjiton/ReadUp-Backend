package com.springboot.readup.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findBySummaryIdOrderByCreatedAtDesc(Long summaryId);

    long countBySummaryId(Long summaryId);

    CommentEntity findById(long id);
}
