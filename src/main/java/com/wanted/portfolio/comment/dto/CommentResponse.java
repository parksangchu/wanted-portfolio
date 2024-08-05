package com.wanted.portfolio.comment.dto;

import com.wanted.portfolio.comment.model.Comment;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentResponse {

    private final Long id;

    private final String writer;

    private final String content;

    private final LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getMember().getName(), comment.getContent(),
                comment.getCreatedAt());
    }
}
