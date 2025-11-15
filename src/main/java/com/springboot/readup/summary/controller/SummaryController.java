package com.springboot.readup.summary.controller;

import com.springboot.readup.summary.dto.SummaryCheckResponse;
import com.springboot.readup.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-summary")
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/news/{newsId}")
    public ResponseEntity<SummaryCheckResponse> checkSummary(@PathVariable Long newsId) {

        SummaryCheckResponse response = summaryService.checkSummary(newsId);

        return ResponseEntity.ok(response);
    }
}