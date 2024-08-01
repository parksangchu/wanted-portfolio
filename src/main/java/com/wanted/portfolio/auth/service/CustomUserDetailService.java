package com.wanted.portfolio.auth.service;

import com.wanted.portfolio.auth.dto.CustomUserDetails;
import com.wanted.portfolio.auth.dto.LoginUser;
import com.wanted.portfolio.member.model.Member;
import com.wanted.portfolio.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //이메일과 이름 둘 모두 사용 가능하도록 하였음
        Optional<Member> member = memberRepository.findByEmail(username);

        if (member.isEmpty()) {
            member = memberRepository.findByName(username);
        }

        LoginUser loginUser = LoginUser.from(
                member.orElseThrow(() -> new UsernameNotFoundException("username 혹은 password가 일치하지 않습니다.")));

        return new CustomUserDetails(loginUser);
    }
}
