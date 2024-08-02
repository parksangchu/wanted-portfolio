package com.wanted.portfolio.post.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@SpringBootTest
public class PostRequestValidationTest {

    @Autowired
    private Validator validator;

    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        postRequest = new PostRequest("Valid Title", "Valid Content", null);
    }

    @Test
    @DisplayName("제목과 본문이 올바르면 유효성 검사가 성공한다.")
    void whenValidInput_thenValidationSucceeds() {
        Errors errors = new BeanPropertyBindingResult(postRequest, "postRequest");
        validator.validate(postRequest, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    @DisplayName("제목이 비어 있으면 유효성 검사에 실패한다.")
    void whenTitleIsEmpty_thenValidationFails() {
        postRequest = new PostRequest("", "Valid Content", null);
        Errors errors = new BeanPropertyBindingResult(postRequest, "postRequest");
        validator.validate(postRequest, errors);

        assertThat(errors.hasFieldErrors("title")).isTrue();
        assertThat(errors.getFieldError("title").getDefaultMessage()).isEqualTo("제목은 필수입니다.");
    }

    @Test
    @DisplayName("본문이 비어 있으면 유효성 검사에 실패한다.")
    void whenContentIsEmpty_thenValidationFails() {
        postRequest = new PostRequest("Valid Title", "", null);
        Errors errors = new BeanPropertyBindingResult(postRequest, "postRequest");
        validator.validate(postRequest, errors);

        assertThat(errors.hasFieldErrors("content")).isTrue();
        assertThat(errors.getFieldError("content").getDefaultMessage()).isEqualTo("본문은 필수입니다.");
    }

    @Test
    @DisplayName("제목이 200자를 초과하면 유효성 검사에 실패한다.")
    void whenTitleExceedsMaxLength_thenValidationFails() {
        String longTitle = "a".repeat(201);
        postRequest = new PostRequest(longTitle, "Valid Content", null);
        Errors errors = new BeanPropertyBindingResult(postRequest, "postRequest");
        validator.validate(postRequest, errors);

        assertThat(errors.hasFieldErrors("title")).isTrue();
        assertThat(errors.getFieldError("title").getDefaultMessage()).isEqualTo("제목은 200자 이하여야 합니다.");
    }

    @Test
    @DisplayName("본문이 1000자를 초과하면 유효성 검사에 실패한다.")
    void whenContentExceedsMaxLength_thenValidationFails() {
        String longContent = "a".repeat(1001);
        postRequest = new PostRequest("Valid Title", longContent, null);
        Errors errors = new BeanPropertyBindingResult(postRequest, "postRequest");
        validator.validate(postRequest, errors);

        assertThat(errors.hasFieldErrors("content")).isTrue();
        assertThat(errors.getFieldError("content").getDefaultMessage()).isEqualTo("본문은 1000자 이하여야 합니다.");
    }
}
