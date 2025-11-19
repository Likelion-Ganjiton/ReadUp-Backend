package com.springboot.readup.summary.repository;

import com.springboot.readup.summary.entity.UserSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSummaryRepository extends JpaRepository<UserSummaryEntity, Long> {

    Optional<UserSummaryEntity> findByUserIdAndNewsId(Long userId, Long newsId);

    List<UserSummaryEntity> findByNewsIdIn(List<Long> newsIds);

}