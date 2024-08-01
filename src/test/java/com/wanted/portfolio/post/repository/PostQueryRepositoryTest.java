package com.wanted.portfolio.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.model.Role;
import com.wanted.portfolio.member.repository.MemberRepository;
import com.wanted.portfolio.post.dto.PostSearchCondition;
import com.wanted.portfolio.post.model.Post;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PostQueryRepositoryTest {

    @Autowired
    private PostQueryRepository postQueryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        initializeTestData();
    }

    private void initializeTestData() {
        // Create and save members
        Member member1 = new Member("john@example.com", null, "John Doe", null, Role.USER);
        Member member2 = new Member("jane@example.com", null, "Jane Smith", null, Role.USER);
        memberRepository.saveAll(Arrays.asList(member1, member2));

        // Create and save posts
        Post post1 = new Post("First Post", "This is the first post content.", member1, 10);
        Post post2 = new Post("Second Post", "This is the second post content.", member2, 20);
        Post post3 = new Post("Another Post by John", "John's additional post content.", member1, 30);

        postRepository.saveAll(Arrays.asList(post1, post2, post3));
    }

    @Test
    @DisplayName("제목으로 게시글을 정렬 및 검색할 수 있다.")
    public void testFindAllByTitle() {
        // Given
        String title = "Post";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        // When
        Page<Post> result = postQueryRepository.findAll(pageable, new PostSearchCondition(title, null, null));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(Post::getTitle)
                .containsExactly("Another Post by John", "First Post", "Second Post");
    }

    @Test
    @DisplayName("작성자로 게시글을 정렬 및 검색할 수 있다.")
    public void testFindAllByWriterName() {
        // Given
        String writer = "Jane";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("writer").descending());

        // When
        Page<Post> result = postQueryRepository.findAll(pageable, new PostSearchCondition(null, null, writer));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).extracting(post -> post.getMember().getName()).containsExactly("Jane Smith");
    }

    @Test
    @DisplayName("내용으로 정렬 및 검색할 수 있다.")
    public void testFindAllByContent() {
        // Given
        String content = "John";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("content").descending());

        // When
        Page<Post> result = postQueryRepository.findAll(pageable, new PostSearchCondition(null, content, null));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).extracting(Post::getContent).containsExactly("John's additional post content.");
    }

    @Test
    @DisplayName("게시글을 조회수로 정렬할 수 있다.")
    public void testFindAllWithDifferentSort() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("view").descending());

        // When
        Page<Post> result = postQueryRepository.findAll(pageable, new PostSearchCondition(null, null, null));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).extracting(Post::getView).containsExactly(30, 20, 10);
    }

    @Test
    @DisplayName("정렬 조건이 없다면 createAt을 기준으로 내림차 정렬한다.")
    public void testFindAllPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

        // When
        Page<Post> result = postQueryRepository.findAll(pageable, new PostSearchCondition(null, null, null));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).extracting(Post::getTitle)
                .containsExactly("Another Post by John", "Second Post", "First Post");
    }
}
