package com.springboot.readup.like;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(
        name = "summary_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "summary_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "summary_id", nullable = false)
    private Long summaryId;
}
