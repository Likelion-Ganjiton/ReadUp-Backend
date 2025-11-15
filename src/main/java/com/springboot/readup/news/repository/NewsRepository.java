package com.springboot.readup.news.repository;

import com.springboot.readup.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByUrl(String url);
    List<News> findByCategoryIn(List<String> categories);
}