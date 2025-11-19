package com.springboot.readup.community;

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
        CommunityFeedResponse response = communityService.getFeedByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{summaryId}")
    public ResponseEntity<CommunityDetailResponse> getDetail(
            @PathVariable Long summaryId
    ) {
        CommunityDetailResponse response = communityService.getSummaryDetail(summaryId);
        return ResponseEntity.ok(response);
    }
}
