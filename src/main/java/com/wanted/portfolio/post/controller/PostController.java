package com.wanted.portfolio.post.controller;

import com.wanted.portfolio.auth.dto.CustomUserDetails;
import com.wanted.portfolio.post.dto.PostCreateResponse;
import com.wanted.portfolio.post.dto.PostDetailResponse;
import com.wanted.portfolio.post.dto.PostFileResponse;
import com.wanted.portfolio.post.dto.PostListResponse;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.dto.PostUpdateResponse;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.model.PostFile;
import com.wanted.portfolio.post.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<PostCreateResponse> createPost(@Valid @RequestBody PostRequest postRequest,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        String username = userDetails.getUsername();

        Post post = postService.createPost(postRequest, username);

        PostCreateResponse postResponse = PostCreateResponse.from(post);

        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostUpdateResponse> updatePost(@Valid @RequestBody PostRequest postRequest,
                                                         @PathVariable Long id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();
        String role = userDetails.getRole();

        Post post = postService.updatePost(id, postRequest, username, role);
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

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {

        Post post = postService.findPost(id);
        Integer remainingEditDays = postService.calculateRemainingEditDays(post);

        PostDetailResponse postDetailResponse = PostDetailResponse.of(post, remainingEditDays);

        return ResponseEntity.ok(postDetailResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePost(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        String username = userDetails.getUsername();
        String role = userDetails.getRole();

        postService.softDelete(id, username, role);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeletePost(@PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        String username = userDetails.getUsername();
        String role = userDetails.getRole();

        postService.hardDelete(id, username, role);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<List<PostFileResponse>> getPostFiles(@PathVariable Long id) {
        List<PostFile> postFiles = postService.findPostFilesByPostId(id);

        List<PostFileResponse> postFileResponses = postFiles.stream()
                .map(PostFileResponse::from)
                .toList();

        return ResponseEntity.ok(postFileResponses);
    }
}
