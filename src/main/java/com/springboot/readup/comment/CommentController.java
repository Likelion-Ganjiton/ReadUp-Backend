package com.springboot.readup.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 관련 API만 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService communityCommentService;

    /**
     * 댓글 작성
     * POST /api/community/{summaryId}/comment
     */
    @PostMapping("/{summaryId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long summaryId,
            @RequestBody CommentCreate request
    ) {
        CommentDto response = communityCommentService.addComment(summaryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId
    ) {
        communityCommentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
