package com.wanted.portfolio.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.wanted.portfolio.global.exception.BadRequestException;
import com.wanted.portfolio.global.exception.ForbiddenException;
import com.wanted.portfolio.global.util.Clock;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.repository.MemberRepository;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.model.Post;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    private Post post;

    @MockBean
    private Clock clock;

    @BeforeEach
    void setUp() {
        member = new Member("tester@mail.com", "010-1111-1111", "tester", "test");
        memberRepository.save(member);

        PostRequest postRequest = new PostRequest("test title", "test content");
        post = postService.createPost(postRequest, member.getId());
    }

    @ParameterizedTest
    @DisplayName("사용자가 동일하고 작성일 기준 10일 이내면 글 수정이 가능하다.")
    @ValueSource(ints = {8, 9, 10})
    void updatePost_success(int plusDays) {
        PostRequest postRequest = new PostRequest("title update", "content update");

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        postService.updatePost(post.getId(), postRequest, member.getId());

        assertThat(post.getTitle()).isEqualTo("title update");
        assertThat(post.getContent()).isEqualTo("content update");
    }

    @Test
    @DisplayName("작성일 기준 10일이 지나면 글 수정이 불가능하다.")
    void updatePost_expiration() {
        PostRequest postRequest = new PostRequest("test update", "content update");

        int plusDays = 11;
        LocalDate createDate = post.getCreateDate();
        System.out.println(createDate);

        LocalDate exDate = createDate.plusDays(plusDays);
        System.out.println(exDate);

        when(clock.getCurrentDate()).thenReturn(exDate);

        assertThatThrownBy(() -> postService.updatePost(post.getId(), postRequest, member.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("사용자가 동일하지 않으면 글 수정이 불가능하다.")
    void updatePost_invalidWriter() {
        Member other = new Member(null, null, null, null);
        memberRepository.save(other);

        PostRequest postRequest = new PostRequest("test update", "content update");

        int plusDays = 9;
        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        assertThatThrownBy(() -> postService.updatePost(post.getId(), postRequest, other.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @ParameterizedTest
    @DisplayName("게시글 작성 후 9일부터는 알림 메시지가 생성된다.")
    @ValueSource(ints = {9, 10})
    void makeAlertMessage_success(int plusDays) {

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));
        String message = postService.makeAlertMessage(post);

        assertThat(message).isEqualTo(String.format(PostService.EXPIRATION_ALERT_MESSAGE, plusDays));
    }

    @Test
    @DisplayName("게시글 작성 후 9일이 지나기 전에는 빈 메시지를 반환한다.")
    void makeAlertMessage_null() {
        int plusDays = 8;

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));
        String message = postService.makeAlertMessage(post);

        assertThat(message).isNull();
    }
}