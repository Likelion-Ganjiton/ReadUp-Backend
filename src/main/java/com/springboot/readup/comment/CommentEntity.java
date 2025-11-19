package com.springboot.readup.comment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 요약글에 단 댓글인지
    @Column(name = "summary_id", nullable = false)
    private Long summaryId;

    // 누가 썼는지 (UserEntity.id)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 댓글 내용
    @Column(nullable = false, length = 1000)
    private String content;

    // 작성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
