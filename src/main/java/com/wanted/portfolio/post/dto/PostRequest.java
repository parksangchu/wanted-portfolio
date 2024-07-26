package com.wanted.portfolio.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@RequiredArgsConstructor
public class PostRequest {

    @Length(max = 200, message = "제목은 200자 이하여야 합니다.")
    @NotBlank(message = "제목은 필수입니다.")
    private final String title;

    @Length(max = 1000, message = "본문은 1000자 이하여야 합니다.")
    @NotBlank(message = "본문은 필수입니다.")
    private final String content;
}
