package com.wanted.portfolio.comment.service;

import com.wanted.portfolio.comment.dto.CommentRequest;
import com.wanted.portfolio.comment.model.Comment;
import com.wanted.portfolio.comment.repository.CommentRepository;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.service.MemberService;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final MemberService memberService;

    private final PostService postService;

    public Comment createComment(CommentRequest commentRequest, String memberName) {
        Post post = postService.findPost(commentRequest.getPostId());
        Member member = memberService.findMemberByName(memberName);

        Comment comment = new Comment(post, member, commentRequest.getContent());

        commentRepository.save(comment);

        log.info("새로운 댓글이 작성되었습니다. {}", comment);
        return comment;
    }

    @Transactional(readOnly = true)
    public Page<Comment> findCommentsByPostId(Long id, Pageable pageable) {
        return commentRepository.findAllByPostIdAndDeletedAtIsNull(id, pageable);
    }
}
