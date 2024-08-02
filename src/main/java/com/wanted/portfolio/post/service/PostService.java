package com.wanted.portfolio.post.service;

import com.wanted.portfolio.file.model.File;
import com.wanted.portfolio.file.service.FileService;
import com.wanted.portfolio.global.exception.BadRequestException;
import com.wanted.portfolio.global.exception.ForbiddenException;
import com.wanted.portfolio.global.exception.NotFoundException;
import com.wanted.portfolio.global.util.Clock;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.model.Role;
import com.wanted.portfolio.member.service.MemberService;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.model.PostFile;
import com.wanted.portfolio.post.repository.PostFileRepository;
import com.wanted.portfolio.post.repository.PostQueryRepository;
import com.wanted.portfolio.post.repository.PostRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    public static final int EDITABLE_PERIOD_DAYS = 10;
    private static final int ALERT_PERIOD_DAYS = 9;
    public static final String EXPIRATION_ALERT_MESSAGE = "게시글 생성 %d일째 입니다. 생성일 기준 10일 이후에는 수정이 불가능합니다.";

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostFileRepository postFileRepository;
    private final MemberService memberService;
    private final FileService fileService;
    private final Clock clock;

    public Post createPost(PostRequest postRequest, String memberName) {
        Member member = memberService.findMemberByName(memberName);

        Post post = new Post(postRequest.getTitle(), postRequest.getContent(), member, 0);

        postRepository.save(post);

        List<File> files = fileService.findAll(postRequest.getFileIds());

        List<PostFile> postFiles = getPostFiles(post, files);

        postFileRepository.saveAll(postFiles);

        log.info("새로운 게시글이 생성되었습니다. = {}", post);

        return post;
    }

    public Post updatePost(Long id, PostRequest postRequest, String memberName, String memberRole) {
        Post post = findPost(id);

        validatePermission(post, memberName, memberRole);
        validateExpiration(post.getCreateDate());

        post.changeTitle(postRequest.getTitle());
        post.changeContent(postRequest.getContent());

        postFileRepository.deleteAllByPostId(post.getId());

        List<File> files = fileService.findAll(postRequest.getFileIds());

        List<PostFile> postFiles = getPostFiles(post, files);

        postFileRepository.saveAll(postFiles);

        log.info("게시글이 수정되었습니다. = {}", post);

        return post;
    }

    @Transactional(readOnly = true)
    public Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("id와 일치하는 게시글을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public String makeAlertMessage(Post post) {
        long days = calculateDaysFrom(post.getCreateDate());

        if (days >= ALERT_PERIOD_DAYS) {
            return String.format(EXPIRATION_ALERT_MESSAGE, days);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public Integer calculateRemainingEditDays(Post post) {
        int days = calculateDaysFrom(post.getCreateDate());

        int remainingEditDays = EDITABLE_PERIOD_DAYS - days;

        return remainingEditDays >= 0 ? remainingEditDays : null;
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable, PostSearchCondition postSearchCondition) {
        return postQueryRepository.findAll(pageable, postSearchCondition);
    }

    public void softDelete(Long id, String memberName, String memberRole) {
        Post post = findPost(id);

        validatePermission(post, memberName, memberRole);

        post.softDelete();

        log.info("게시글이 삭제 되었습니다. id = {}", id);
    }

    public void hardDelete(Long id, String memberName, String memberRole) {
        Post post = findPost(id);

        validatePermission(post, memberName, memberRole);

        postRepository.delete(post);

        log.info("게시글이 영구 삭제되었습니다. id = {}", id);
    }

    private void validatePermission(Post post, String memberName, String memberRole) {
        if (!post.isWriter(memberName) && Role.from(memberRole) != Role.ADMIN) { // 작성자가 아니면서 관리자가 아닐 경우
            throw new ForbiddenException("해당 글 수정 권한이 없는 사용자입니다.");
        }
    }

    private void validateExpiration(LocalDate createDate) {
        long days = calculateDaysFrom(createDate);

        if (days > EDITABLE_PERIOD_DAYS) {
            throw new BadRequestException("해당 글의 수정 가능 기간이 지났습니다.");
        }
    }

    private int calculateDaysFrom(LocalDate createDate) {
        LocalDate nowDate = clock.getCurrentDate();

        return (int) (ChronoUnit.DAYS.between(createDate, nowDate) + 1);
    }

    private List<PostFile> getPostFiles(Post post, List<File> files) {
        return files.stream()
                .map(file -> new PostFile(post, file))
                .toList();
    }
}
