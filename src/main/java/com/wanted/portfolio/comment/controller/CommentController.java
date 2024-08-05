package com.wanted.portfolio.comment.controller;

import com.wanted.portfolio.auth.dto.CustomUserDetails;
import com.wanted.portfolio.comment.dto.CommentRequest;
import com.wanted.portfolio.comment.dto.CommentResponse;
import com.wanted.portfolio.comment.model.Comment;
import com.wanted.portfolio.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest commentRequest,
                                                         @AuthenticationPrincipal
                                                         CustomUserDetails userDetails) {
        String username = userDetails.getUsername();

        Comment comment = commentService.createComment(commentRequest, username);

        return ResponseEntity.ok(CommentResponse.from(comment));
    }
}
