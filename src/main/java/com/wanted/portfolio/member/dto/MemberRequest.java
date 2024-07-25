package com.wanted.portfolio.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class MemberRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private final String email;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "올바르지 않은 전화번호 형식입니다.")
    private final String phoneNumber;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]{2,30}$", message = "올바르지 않은 이름 형식입니다.")
    private final String name;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d{5,})(?=.*[@$!%*?&]{2,}).{8,}$",
            message = "비밀번호는 대소문자, 숫자 5개 이상, 특수문자 2개 이상이어야 합니다.")
    private final String password;
}
