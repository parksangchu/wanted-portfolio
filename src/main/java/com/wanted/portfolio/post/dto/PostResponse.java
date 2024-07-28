package com.wanted.portfolio.post.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostResponse {

    private final Long id;

    private final String title;

    private final String content;

    private final String writer;

    private final Integer view;

    private final LocalDateTime createAt;

    private final LocalDateTime modifiedAt;

    private final String alertMessage;
}
