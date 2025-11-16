package com.springboot.readup.feedback.controller;

import com.springboot.readup.feedback.dto.AiFeedbackResponseDto;
import com.springboot.readup.feedback.service.AiFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class AiFeedbackController {

    private final AiFeedbackService aiFeedbackService;

    @PostMapping("/{summaryId}")
    public ResponseEntity<AiFeedbackResponseDto> evaluate(
            @PathVariable Long summaryId) throws Exception {

        AiFeedbackResponseDto response = aiFeedbackService.evaluateSummary(summaryId);

        return ResponseEntity.ok(response);
    }
}