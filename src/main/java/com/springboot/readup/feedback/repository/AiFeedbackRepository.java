package com.springboot.readup.feedback.repository;

import com.springboot.readup.feedback.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

    Optional<AiFeedback> findByUserSummaryId(Long userSummaryId);
}