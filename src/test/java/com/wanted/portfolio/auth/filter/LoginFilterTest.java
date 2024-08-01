package com.wanted.portfolio.auth.filter;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.portfolio.auth.dto.LoginRequest;
import com.wanted.portfolio.member.dto.MemberRequest;
import com.wanted.portfolio.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MemberRequest memberRequest = new MemberRequest(
                "test@example.com",
                "010-1234-5678",
                "test",
                "1234"
        );

        memberService.createMember(memberRequest);
    }

    @ParameterizedTest
    @DisplayName("유저네임(이메일)과 비밀번호가 일치하면 jwt토큰을 생성한다.")
    @ValueSource(strings = {"test", "test@example.com"})
    public void testSuccessfulAuthentication(String username) throws Exception {

        String password = "1234";
        LoginRequest loginRequest = new LoginRequest(username, password);
        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization")); // JWT 토큰이 응답 헤더에 포함되어 있는지 확인
    }

    @ParameterizedTest
    @DisplayName("사용자 이름이나 비밀번호가 맞지 않으면 401 에러를 발생시킨다.")
    @CsvSource(value = {"test,12345", "tes,1234"})
    public void testUnsuccessfulAuthentication(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);

        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(json))
                .andExpect(status().isUnauthorized()); // 인증 실패 시 401 상태 코드를 확인
    }
}
