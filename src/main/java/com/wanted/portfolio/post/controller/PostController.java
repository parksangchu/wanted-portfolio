package com.wanted.portfolio.post.controller;

import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostResponse;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        Post post = postService.createPost(postRequest, 1L); // 로그인 기능 미구현으로 1L로 처리

        PostResponse postResponse = toPostResponse(post);

        return ResponseEntity.ok(postResponse);
    }

    private PostResponse toPostResponse(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent()
                , post.getMember().getName(), post.getView(), post.getCreatedAt(), post.getModifiedAt());
    }
}
