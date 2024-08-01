package com.wanted.portfolio.member.service;

import com.wanted.portfolio.global.exception.BadRequestException;
import com.wanted.portfolio.global.exception.NotFoundException;
import com.wanted.portfolio.member.dto.MemberRequest;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.model.Role;
import com.wanted.portfolio.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member createMember(MemberRequest memberRequest) {

        validateDuplicateEmail(memberRequest);
        validateDuplicateName(memberRequest);

        String encodedPassword = passwordEncoder.encode(memberRequest.getPassword());

        Member member = toMember(memberRequest, encodedPassword, Role.USER);
        memberRepository.save(member);

        log.info("새로운 멤버가 생성되었습니다. : {}", member);
        return member;
    }

    @Transactional(readOnly = true)
    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("일치하는 멤버를 찾을 수 없습니다."));
    }

    private void validateDuplicateEmail(MemberRequest memberRequest) {
        if (memberRepository.existsByEmail(memberRequest.getEmail())) {
            throw new BadRequestException("이미 사용중인 이메일입니다.");
        }
    }

    private void validateDuplicateName(MemberRequest memberRequest) {
        if (memberRepository.existsByName(memberRequest.getName())) {
            throw new BadRequestException("이미 사용중인 이름입니다.");
        }
    }

    private Member toMember(MemberRequest memberRequest, String encodedPassword, Role role) {
        return new Member(memberRequest.getEmail(), memberRequest.getPhoneNumber(), memberRequest.getName(),
                encodedPassword, role);
    }
}
