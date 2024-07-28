package com.wanted.portfolio.post.controller;

import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostResponse;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        Long memberId = 1L;

        Post post = postService.createPost(postRequest, memberId); // 로그인 기능 미구현으로 1L로 처리

        PostResponse postResponse = toPostResponse(post, null);

        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@Valid @RequestBody PostRequest postRequest, @PathVariable Long id) {
        Long memberId = 1L; // 로그인 기능 미구현으로 1L로 처리

        Post post = postService.updatePost(id, postRequest, memberId);
        String message = postService.makeAlertMessage(post);

        PostResponse postResponse = toPostResponse(post, message);

        return ResponseEntity.ok(postResponse);
    }

    private PostResponse toPostResponse(Post post, String alertMessage) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent()
                , post.getMember().getName(), post.getView(), post.getCreatedAt(), post.getModifiedAt(), alertMessage);
    }
}
