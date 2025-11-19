package com.springboot.readup.comment.service;

import com.springboot.readup.comment.dto.CommentCreate;
import com.springboot.readup.comment.dto.CommentDto;
import com.springboot.readup.comment.entity.CommentEntity;
import com.springboot.readup.comment.repository.CommentRepository;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserSummaryRepository userSummaryRepository;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal == null || principal.equals("anonymousUser")) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        String loginId = (String) principal;

        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저 정보를 찾을 수 없습니다."));
    }

    public CommentDto addComment(Long summaryId, CommentCreate req) {

        UserEntity me = getCurrentUser();

        userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new RuntimeException("요약글을 찾을 수 없습니다."));

        CommentEntity saved = commentRepository.save(
                CommentEntity.builder()
                        .summaryId(summaryId)
                        .userId(me.getId())
                        .content(req.getContent())
                        .build()
        );

        return CommentDto.builder()
                .commentId(saved.getId())          // 명세서 필드명
                .userName(me.getNickname())        // 명세서: userName(nickname)
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public String deleteComment(Long commentId) {

        UserEntity me = getCurrentUser();

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getUserId().equals(me.getId())) {
            throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }
}