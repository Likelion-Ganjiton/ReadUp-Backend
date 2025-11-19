package com.springboot.readup.community.controller;

import com.springboot.readup.community.dto.detail.CommunityDetailResponse;
import com.springboot.readup.community.dto.feed.CommunityFeedResponse;
import com.springboot.readup.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public ResponseEntity<CommunityFeedResponse> getFeed(
            @RequestParam String category
    ) {
        return ResponseEntity.ok(
                communityService.getFeedByCategory(category)
        );
    }

    @GetMapping("/{summaryId}")
    public ResponseEntity<CommunityDetailResponse> getDetail(
            @PathVariable Long summaryId
    ) {
        return ResponseEntity.ok(
                communityService.getSummaryDetail(summaryId)
        );
    }
}