package com.wanted.portfolio.post.controller;

import com.wanted.portfolio.post.dto.PostCreateResponse;
import com.wanted.portfolio.post.dto.PostListResponse;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.dto.PostUpdateResponse;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<PostCreateResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        Long memberId = 1L; // 로그인 기능 미구현으로 1L로 처리

        Post post = postService.createPost(postRequest, memberId);

        PostCreateResponse postResponse = PostCreateResponse.from(post);

        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostUpdateResponse> updatePost(@Valid @RequestBody PostRequest postRequest,
                                                         @PathVariable Long id) {
        Long memberId = 1L; // 로그인 기능 미구현으로 1L로 처리

        Post post = postService.updatePost(id, postRequest, memberId);
        String alertMessage = postService.makeAlertMessage(post);

        PostUpdateResponse postResponse = PostUpdateResponse.of(post, alertMessage);

        return ResponseEntity.ok(postResponse);
    }

    @GetMapping
    public ResponseEntity<Page<PostListResponse>> getAllPosts(Pageable pageable,
                                                              @ModelAttribute PostSearchCondition postSearchCondition) {

        Page<Post> posts = postService.getAllPosts(pageable, postSearchCondition);
        Page<PostListResponse> postResponses = posts.map(PostListResponse::from);
        return ResponseEntity.ok(postResponses);
    }
}
