package com.wanted.portfolio.post.dto;

import com.wanted.portfolio.post.model.Post;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PostListResponse {
    private final Long id;

    private final String title;

    private final String writer;

    private final Integer view;

    private final LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return new PostListResponse(post.getId(), post.getTitle(), post.getMember().getName(), post.getView(),
                post.getCreatedAt());
    }
}
