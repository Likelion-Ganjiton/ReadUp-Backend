package com.springboot.readup.mypage.controller;

import com.springboot.readup.mypage.dto.*;
import com.springboot.readup.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/stats")
    public ResponseEntity<MypageStatsResponse> getStats() {
        return ResponseEntity.ok(mypageService.getStats());
    }

    @PatchMapping("/categories")
    public ResponseEntity<CategoryUpdateResponse> updateCategories(
            @RequestBody CategoryUpdateRequest req
    ) {
        return ResponseEntity.ok(mypageService.updateCategories(req));
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<FeedbackListResponse> getFeedbackList() {
        return ResponseEntity.ok(mypageService.getFeedbackList());
    }

    @GetMapping("/feedbacks/{summaryId}")
    public ResponseEntity<FeedbackDetailDto> getFeedbackDetail(
            @PathVariable Long summaryId
    ) {
        return ResponseEntity.ok(mypageService.getFeedbackDetail(summaryId));
    }
    @GetMapping("/categories")
    public ResponseEntity<CategoryListResponse> getCategories() {
        return ResponseEntity.ok(mypageService.getCategories());
    }
}