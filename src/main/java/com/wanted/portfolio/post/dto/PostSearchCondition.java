package com.wanted.portfolio.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostSearchCondition {
    private final String title;
    private final String content;
    private final String writer;
}