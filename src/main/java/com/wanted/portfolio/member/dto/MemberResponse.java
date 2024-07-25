package com.wanted.portfolio.member.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponse {

    private final Long id;

    private final String email;

    private final String phoneNumber;

    private final String name;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;
}
