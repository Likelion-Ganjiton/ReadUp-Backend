package com.springboot.readup.comment.controller;

import com.springboot.readup.comment.dto.CommentCreate;
import com.springboot.readup.comment.dto.CommentDto;
import com.springboot.readup.comment.dto.CommentDeleteResponse;
import com.springboot.readup.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{summaryId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long summaryId,
            @RequestBody CommentCreate request
    ) {
        CommentDto response = commentService.addComment(summaryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<CommentDeleteResponse> deleteComment(
            @PathVariable Long commentId
    ) {
        String msg = commentService.deleteComment(commentId);

        return ResponseEntity.ok(
                CommentDeleteResponse.builder()
                        .commentId(commentId)
                        .message(msg)
                        .build()
        );
    }
}