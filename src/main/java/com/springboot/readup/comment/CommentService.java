package com.springboot.readup.comment;

import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserSummaryRepository userSummaryRepository;
    private final UserRepository userRepository;

    // 현재 로그인한 사용자 조회
    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<UserEntity> user = userRepository.findByLoginId(loginId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("로그인된 유저를 찾을 수 없습니다.");
        }

        return user.orElse(null);
    }

    public CommentDto addComment(Long summaryId, CommentCreate request) {
        UserEntity me = getCurrentUser();

        // 요약글 존재 확인
        Optional<UserSummaryEntity> summary = userSummaryRepository.findById(summaryId);
        if (summary.isEmpty()) {
            throw new IllegalArgumentException("요약글을 찾을 수 없습니다.");
        }

        CommentEntity saved = commentRepository.save(
                CommentEntity.builder()
                        .summaryId(summaryId)
                        .userId(me.getId())
                        .content(request.getContent())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return CommentDto.builder()
                .id(saved.getId())
                .writerName(me.getLoginId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public void deleteComment(Long commentId) {
        UserEntity me = getCurrentUser();

        Optional<CommentEntity> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }

        if (!comment.get().getUserId().equals(me.getId())) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        CommentEntity OptionalComment = comment.get();
        commentRepository.delete(OptionalComment);
    }
}
