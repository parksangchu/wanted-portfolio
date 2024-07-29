package com.wanted.portfolio.post.service;

import com.wanted.portfolio.global.exception.BadRequestException;
import com.wanted.portfolio.global.exception.ForbiddenException;
import com.wanted.portfolio.global.exception.NotFoundException;
import com.wanted.portfolio.global.util.Clock;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.service.MemberService;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.repository.PostQueryRepository;
import com.wanted.portfolio.post.repository.PostRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class PostService {
    private static final int EDITABLE_PERIOD_DAYS = 10;
    private static final int ALERT_PERIOD_DAYS = 9;
    public static final String EXPIRATION_ALERT_MESSAGE = "게시글 생성 %d일째 입니다. 생성일 기준 10일 이후에는 수정이 불가능합니다.";

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberService memberService;
    private final Clock clock;

    public Post createPost(PostRequest postRequest, Long memberId) {
        Member member = memberService.findMember(memberId);

        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), member, 0);

        postRepository.save(post);

        log.info("새로운 게시글이 생성되었습니다. = {}", post);

        return post;
    }

    public Post updatePost(Long id, PostRequest postRequest, Long memberId) {
        Member member = memberService.findMember(memberId);
        Post post = findPost(id);

        validateWriter(post, member);
        validateExpiration(post.getCreateDate());

        post.changeTitle(postRequest.getTitle());
        post.changeContent(postRequest.getContent());

        log.info("게시글이 수정되었습니다. = {}", post);

        return post;
    }

    @Transactional(readOnly = true)
    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("id와 일치하는 게시글을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public String makeAlertMessage(Post post) {
        LocalDate createdDate = post.getCreateDate();
        LocalDate nowDate = clock.getCurrentDate();

        long days = ChronoUnit.DAYS.between(createdDate, nowDate) + 1; // 당일까지 적용

        if (days >= ALERT_PERIOD_DAYS) {
            return String.format(EXPIRATION_ALERT_MESSAGE, days);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable, PostSearchCondition postSearchCondition) {
        return postQueryRepository.findAll(pageable, postSearchCondition);
    }

    private void validateWriter(Post post, Member member) {
        if (!post.isWriter(member)) {
            throw new ForbiddenException("해당 글 수정 권한이 없는 사용자입니다.");
        }
    }

    private void validateExpiration(LocalDate createDate) {
        LocalDate nowDate = clock.getCurrentDate();
        long days = ChronoUnit.DAYS.between(createDate, nowDate) + 1; // 당일까지 적용

        if (days > EDITABLE_PERIOD_DAYS) {
            throw new BadRequestException("해당 글의 수정 가능 기간이 지났습니다.");
        }
    }
}
