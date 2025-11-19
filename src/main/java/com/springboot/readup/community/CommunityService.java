package com.springboot.readup.community;

import com.springboot.readup.comment.CommentDto;
import com.springboot.readup.comment.CommentEntity;
import com.springboot.readup.comment.CommentRepository;
import com.springboot.readup.like.LikeEntity;
import com.springboot.readup.like.LikeRepository;
import com.springboot.readup.news.entity.News;
import com.springboot.readup.news.repository.NewsRepository;
import com.springboot.readup.summary.entity.UserSummaryEntity;
import com.springboot.readup.summary.repository.UserSummaryRepository;
import com.springboot.readup.user.entity.UserEntity;
import com.springboot.readup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserSummaryRepository userSummaryRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    private UserEntity getCurrentUser() {
        String loginId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Optional<UserEntity> userOpt = userRepository.findByLoginId(loginId);

        return userOpt.orElseThrow(
                () -> new IllegalArgumentException("로그인된 유저를 찾을 수 없습니다.")
        );
    }

    public CommunityFeedResponse getFeedByCategory(String category) {
        List<News> newsList =
                newsRepository.findByCategory(category);

        if(newsList.isEmpty()) {
            return CommunityFeedResponse.builder()
                    .category(category)
                    .items(new ArrayList<>())
                    .build();
        }

        List<Long> newsIds = new ArrayList<>();
        for (News n : newsList) {
            newsIds.add(n.getId());
        }

        List<UserSummaryEntity> summaries =
                userSummaryRepository.findByNewsIdIn(newsIds);

        List<CommunityDto> items =new ArrayList<>();

        for(UserSummaryEntity summary : summaries) {

            News news = null;
            for (News n : newsList) {
                if (n.getId().equals(summary.getNewsId())){
                    news = n;
                    break;
                }
            }
            if(news==null) continue;

            UserEntity writer = userRepository.findById(summary.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));

            long likeCount = likeRepository.countBySummaryId(summary.getId());
            long commentCount = commentRepository.countBySummaryId(summary.getId());

            String preview = summary.getUserSummary();
            if(preview.length() > 50) {
                preview = preview.substring(0,50) + "...";
            }

            CommunityDto item = CommunityDto.builder()
                    .summaryId(summary.getId())
                    .newsTitle(news.getTitle())
                    .category(news.getCategory())
                    .writerName(writer.getLoginId())
                    .summaryPreview(preview)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .createdAt(summary.getCreatedAt())
                    .build();

            items.add(item);
        }

        return CommunityFeedResponse.builder()
                .category(category)
                .items(items)
                .build();
    }

    public CommunityDetailResponse getSummaryDetail(Long summaryId) {
        UserEntity me = getCurrentUser();

        UserSummaryEntity summary = userSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("요약글을 찾을 수 없습니다."));

        News news = newsRepository.findById(summary.getNewsId())
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        UserEntity writer = userRepository.findById(summary.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        long likeCount = likeRepository.countBySummaryId(summaryId);

        LikeEntity myLike = likeRepository.findByUserIdAndSummaryId(me.getId(), summaryId);
        boolean likedByMe = (myLike != null);

        List<CommentEntity> commentEntities =
                commentRepository.findBySummaryIdOrderByCreatedAtDesc(summaryId);

        List<CommentDto> commentDtos = new ArrayList<>();
        for (CommentEntity c : commentEntities) {
            Optional<UserEntity> commentWriterOpt = userRepository.findById(c.getUserId());

            String writerName = commentWriterOpt
                    .map(UserEntity::getLoginId)
                    .orElse("알 수 없음");

            CommentDto dto = CommentDto.builder()
                    .id(c.getId())
                    .writerName(writerName)
                    .content(c.getContent())
                    .createdAt(c.getCreatedAt())
                    .build();

            commentDtos.add(dto);
        }

        return CommunityDetailResponse.builder()
                .summaryId(summary.getId())
                .newsTitle(news.getTitle())
                .newsCategory(news.getCategory())
                .writerName(writer.getLoginId())
                .summaryText(summary.getUserSummary())
                .createdAt(summary.getCreatedAt())
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .comments(commentDtos)
                .build();
    }
}
