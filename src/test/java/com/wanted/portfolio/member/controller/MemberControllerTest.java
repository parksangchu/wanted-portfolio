package com.wanted.portfolio.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.portfolio.member.dto.MemberRequest;
import com.wanted.portfolio.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("모든 형식을 만족하면 회원가입에 성공한다.")
    void createMember_Success() throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "010-1234-5678",
                "홍길동",
                "Password12345!!"
        );

        mockMvc.perform(post("/api/members/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이미 사용중인 이메일이면 400 에러가 발생한다.")
    void createMember_duplicateEmail() throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "010-1234-5678",
                "홍길동",
                "Password12345!!"
        );

        memberService.createMember(memberRequest);

        mockMvc.perform(post("/api/members/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString();

    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400 에러가 발생한다.")
    void testCreateMember_InvalidEmail() throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "invalid-email",
                "010-1234-5678",
                "홍길동",
                "Password12345!!"
        );

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전화번호 형식이 올바르지 않으면 400 에러가 발생한다.")
    void testCreateMember_InvalidPhone() throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "1234-5678",
                "홍길동",
                "Password12345!!"
        );

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이름 형식이 올바르지 않으면 400 에러가 발생한다.")
    void testCreateMember_InvalidName() throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "010-1234-5678",
                "홍길동123",
                "Password12345!!"
        );

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password",        // 대문자와 숫자 없음
            "Password",        // 숫자와 특수문자 없음
            "Password123",     // 특수문자 없음
            "Password123!",    // 특수문자 1개만
            "Password123!!",   // 숫자 5개 미만
            "Pass123!!",       // 대문자, 소문자, 숫자 5개 이상, 특수문자 2개 있지만, 길이가 8자 미만
            "1234567890!!",    // 대문자와 소문자 없음
            "!@#$$%^&*()_+",   // 대문자, 소문자, 숫자 없음
            ""                 // 빈 문자열
    })
    @DisplayName("비밀번호 형식이 올바르지 않으면 400 에러가 발생한다.")
    void testCreateMember_InvalidPassword(String invalidPassword) throws Exception {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "010-1234-5678",
                "홍길동",
                invalidPassword
        );

        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest());
    }
}