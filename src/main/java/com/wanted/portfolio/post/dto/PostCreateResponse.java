package com.wanted.portfolio.post.dto;

import com.wanted.portfolio.post.model.Post;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostCreateResponse {
    private final Long id;

    private final String title;

    private final String content;

    private final String writer;

    private final Integer view;

    private final LocalDateTime createdAt;

    public static PostCreateResponse from(Post post) {
        return new PostCreateResponse(post.getId(), post.getTitle(), post.getContent(), post.getMember().getName(),
                post.getView(), post.getCreatedAt());
    }
}
