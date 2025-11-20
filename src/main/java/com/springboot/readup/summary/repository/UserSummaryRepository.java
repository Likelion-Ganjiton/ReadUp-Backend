package com.springboot.readup.summary.repository;

import com.springboot.readup.summary.entity.UserSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSummaryRepository extends JpaRepository<UserSummaryEntity, Long> {

    Optional<UserSummaryEntity> findByUserIdAndNewsId(Long userId, Long newsId);

    List<UserSummaryEntity> findByNewsIdIn(List<Long> newsIds);

    @Query("""
        SELECT s
        FROM UserSummaryEntity s
        JOIN FETCH com.springboot.readup.news.entity.News n ON n.id = s.newsId
        JOIN FETCH com.springboot.readup.user.entity.UserEntity u ON u.id = s.userId
        WHERE n.category = :category
    """)
    List<UserSummaryEntity> findAllWithNewsAndUserByCategory(String category);
}