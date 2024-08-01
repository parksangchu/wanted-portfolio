package com.wanted.portfolio.auth.dto;

import com.wanted.portfolio.member.model.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class LoginUser {

    private final String username;

    private final String password;

    private final String role;

    public static LoginUser from(Member member) {
        return new LoginUser(member.getName(), member.getPassword(), member.getRole().getValue());
    }
}
