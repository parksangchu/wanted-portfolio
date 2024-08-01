package com.wanted.portfolio.member.controller;

import com.wanted.portfolio.member.dto.MemberRequest;
import com.wanted.portfolio.member.dto.MemberResponse;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest memberRequest) {
        Member member = memberService.createMember(memberRequest);

        return ResponseEntity.ok(toMemberResponse(member));
    }

    private MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getPhoneNumber(), member.getName(),
                member.getCreatedAt(), member.getModifiedAt());
    }
}
