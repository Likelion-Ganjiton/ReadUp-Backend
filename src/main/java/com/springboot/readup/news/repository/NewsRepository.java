package com.springboot.readup.news.repository;

import com.springboot.readup.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    boolean existsByUrl(String url);

    // 관심 카테고리 뉴스 조회 (기존 TodayNews용)
    List<News> findByCategoryIn(List<String> categories);

    // 최신 3개 뉴스 (홈 화면)
    List<News> findTop3ByOrderByPublishDateDesc();

    // 전체 뉴스 조회 (카테고리 선택 가능) → Pageable 적용
    Page<News> findAll(Pageable pageable);

    Page<News> findAllByCategory(String category, Pageable pageable);

    // 기존 List 기반 메서드 유지 가능
    List<News> findAllByCategoryOrderByPublishDateDesc(String category);
    List<News> findAllByOrderByPublishDateDesc();
}