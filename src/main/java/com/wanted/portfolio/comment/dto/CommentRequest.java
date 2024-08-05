package com.wanted.portfolio.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@RequiredArgsConstructor
public class CommentRequest {

    @NotNull(message = "게시글 id는 필수 입니다.")
    private final Long postId;

    @Length(max = 200, message = "내용은 200자 이하여야 합니다.")
    @NotBlank(message = "내용은 필수입니다.")
    private final String content;
}
