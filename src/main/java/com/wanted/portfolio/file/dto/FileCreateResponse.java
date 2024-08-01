package com.wanted.portfolio.file.dto;

import com.wanted.portfolio.file.model.File;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileCreateResponse {

    private final Long id;

    private final String originalName;

    private final String url;

    public static FileCreateResponse from(File file) {
        return new FileCreateResponse(file.getId(), file.getOriginalName(), file.getUrl());
    }
}
