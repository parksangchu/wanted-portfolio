package com.wanted.portfolio.post.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.model.Role;
import com.wanted.portfolio.member.service.MemberService;
import com.wanted.portfolio.post.dto.PostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member("email@email.com", "010-1111-1111", "tester", "1234", Role.USER);
    }

    @Test
    @DisplayName("제목과 내용의 길이가 각각 200자, 1000자 이내면 게시글이 생성된다.")
    void createPost_success() throws Exception {

        PostRequest postRequest = new PostRequest("Test Title", "Test Content");

        when(memberService.findMember(anyLong())).thenReturn(member);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.writer").value("tester"));
    }

    @Test
    @DisplayName("제목이 200자를 초과하면 400에러가 발생한다.")
    void createPost_invalidTitle() throws Exception {

        PostRequest postRequest = new PostRequest(getTestString(201), "Test Content");

        when(memberService.findMember(anyLong())).thenReturn(member);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("내용이 1000자를 초과하면 400에러가 발생한다.")
    void createPost_invalidContent() throws Exception {

        PostRequest postRequest = new PostRequest("Test Title", getTestString(1001));

        when(memberService.findMember(anyLong())).thenReturn(member);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().is(400));
    }

    private String getTestString(int length) {
        return "a".repeat(length);
    }
}
