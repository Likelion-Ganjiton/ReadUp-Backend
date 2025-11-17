package com.springboot.readup.feedback.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiFeedbackResponseDto {

    private int score;

    private List<String> goodPoints;

    private List<String> badPoints;

    private String feedback;
}