package com.wanted.portfolio.post.service;

import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.service.MemberService;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberService memberService;

    public Post createPost(PostRequest postRequest, Long memberId) {
        Member member = memberService.findMember(memberId);

        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), member, 0);

        postRepository.save(post);

        log.info("새로운 게시글이 생성되었습니다. = {}", post);

        return post;
    }
}
