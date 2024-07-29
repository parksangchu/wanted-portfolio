package com.wanted.portfolio.post.dto;

import com.wanted.portfolio.post.model.Post;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostDetailResponse {
    private final Long id;

    private final String title;

    private final String content;

    private final String writer;

    private final Integer view;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    private final Integer remainingEditDays;

    public static PostDetailResponse of(Post post, Integer remainingEditDays) {
        return new PostDetailResponse(post.getId(), post.getTitle(), post.getContent(), post.getMember().getName(),
                post.getView(), post.getCreatedAt(), post.getModifiedAt(), remainingEditDays);
    }
}
