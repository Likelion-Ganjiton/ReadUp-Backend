package com.springboot.readup.feedback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userSummaryId;

    private Float similarityScore;

    private Integer aiScore;

    @Column(columnDefinition = "TEXT")
    private String goodPoints;

    @Column(columnDefinition = "TEXT")
    private String badPoints;

    @Column(columnDefinition = "TEXT")
    private String aiRevisedSummary;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}