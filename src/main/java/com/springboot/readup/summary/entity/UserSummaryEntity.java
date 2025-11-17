package com.springboot.readup.summary.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_summary")
public class UserSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // 작성자 ID

    private Long newsId;      // 뉴스 ID

    @Column(columnDefinition = "TEXT")
    private String userSummary;

    private String visibility;    // public / private

    private int likeCount;        // 좋아요 캐싱

    private int commentCount;     // 댓글 수 캐싱

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}