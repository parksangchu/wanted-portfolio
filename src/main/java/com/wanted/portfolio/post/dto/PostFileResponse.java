package com.wanted.portfolio.post.dto;

import com.wanted.portfolio.file.model.File;
import com.wanted.portfolio.post.model.PostFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostFileResponse {

    private final Long fileId;

    private final String originalName;

    private final String url;

    public static PostFileResponse from(PostFile postFile) {
        File file = postFile.getFile();

        return new PostFileResponse(file.getId(), file.getOriginalName(), file.getUrl());
    }
}
