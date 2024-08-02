package com.wanted.portfolio.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.wanted.portfolio.global.exception.BadRequestException;
import com.wanted.portfolio.global.exception.ForbiddenException;
import com.wanted.portfolio.global.util.Clock;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.model.Role;
import com.wanted.portfolio.member.repository.MemberRepository;
import com.wanted.portfolio.post.dto.PostRequest;
import com.wanted.portfolio.post.model.Post;
import com.wanted.portfolio.post.repository.PostRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        member = new Member("tester@mail.com", "010-1111-1111", "tester", "test", Role.USER);
        memberRepository.save(member);

        PostRequest postRequest = new PostRequest("test title", "test content", null);
        post = postService.createPost(postRequest, member.getName());
    }

    @ParameterizedTest
    @DisplayName("사용자가 동일하고 작성일 기준 10일 이내면 글 수정이 가능하다.")
    @ValueSource(ints = {7, 8, 9})
    void updatePost_success(int plusDays) {
        PostRequest postRequest = new PostRequest("title update", "content update", null);

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        postService.updatePost(post.getId(), postRequest, member.getName(), Role.USER.getValue());

        assertThat(post.getTitle()).isEqualTo("title update");
        assertThat(post.getContent()).isEqualTo("content update");
    }

    @Test
    @DisplayName("작성일 기준 10일이 지나면 글 수정이 불가능하다.")
    void updatePost_expiration() {
        PostRequest postRequest = new PostRequest("test update", "content update", null);

        int plusDays = 10;
        LocalDate createDate = post.getCreateDate();

        LocalDate exDate = createDate.plusDays(plusDays);

        when(clock.getCurrentDate()).thenReturn(exDate);

        assertThatThrownBy(
                () -> postService.updatePost(post.getId(), postRequest, member.getName(), Role.USER.getValue()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("사용자가 동일하지 않으면 글 수정이 불가능하다.")
    void updatePost_invalidWriter() {
        Member other = new Member(null, null, null, null, Role.USER);
        memberRepository.save(other);

        PostRequest postRequest = new PostRequest("test update", "content update", null);

        int plusDays = 9;
        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        assertThatThrownBy(
                () -> postService.updatePost(post.getId(), postRequest, other.getName(), Role.USER.getValue()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("관리자는가 모든 글을 수정할 수 있다.")
    void updatePost_admin() {
        Member other = new Member(null, null, null, null, Role.ADMIN);
        memberRepository.save(other);

        PostRequest postRequest = new PostRequest("test update", "content update", null);

        int plusDays = 9;
        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        assertThatCode(
                () -> postService.updatePost(post.getId(), postRequest, other.getName(), other.getRole().getValue()))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("게시글 작성 후 9일부터는 알림 메시지가 생성된다.")
    @ValueSource(ints = {9, 10})
    void makeAlertMessage_success(int plusDays) {

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));
        String message = postService.makeAlertMessage(post);

        assertThat(message).isEqualTo(String.format(PostService.EXPIRATION_ALERT_MESSAGE, plusDays + 1));
    }

    @Test
    @DisplayName("게시글 작성일 기준 9일 전에는 빈 메시지를 반환한다.")
    void makeAlertMessage_null() {
        int plusDays = 7;

        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));
        String message = postService.makeAlertMessage(post);

        assertThat(message).isNull();
    }

    @ParameterizedTest
    @DisplayName("아직 게시글 수정이 가능하면 남은 기간을 계산해서 반환한다.")
    @CsvSource(value = {"8,1", "9,0"})
        // 각각 9일째, 10일째 이므로 1일 , 0일
    void calculateRemainingEditDays(int plusDays, int remainingEditDays) {
        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        int result = postService.calculateRemainingEditDays(post);

        assertThat(result).isEqualTo(remainingEditDays);
    }

    @ParameterizedTest
    @DisplayName("게시글 수정이 불가하면 남은 기간은 null을 반환한다.")
    @ValueSource(ints = {10, 11, 12})
    void calculateRemainingEditDays_null(int plusDays) {
        when(clock.getCurrentDate()).thenReturn(post.getCreateDate().plusDays(plusDays));

        Integer result = postService.calculateRemainingEditDays(post);

        assertThat(result).isNull();

    }

    @Test
    @DisplayName("게시글의 deleteAt에 값을 부여하여 임시 삭제처리한다.")
    void softDelete() {
        assertThat(post.getDeletedAt()).isNull();

        postService.softDelete(post.getId(), member.getName(), Role.USER.getValue());

        assertThat(post.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("게시글을 db에서 제거하여 영구 제거한다.")
    void hardDelete() {
        assertThat(postRepository.findById(post.getId())).isNotEmpty();

        postService.hardDelete(post.getId(), member.getName(), Role.USER.getValue());

        assertThat(postRepository.findById(post.getId())).isEmpty();
    }
}