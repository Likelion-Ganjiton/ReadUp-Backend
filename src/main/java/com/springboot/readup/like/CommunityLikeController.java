package com.springboot.readup.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @PostMapping("/{summaryId}/like")
    public ResponseEntity<LikeToggle> toggleLike(
            @PathVariable Long summaryId
    ) {
        LikeToggle response = communityLikeService.toggleLike(summaryId);
        return ResponseEntity.ok(response);
    }
}
