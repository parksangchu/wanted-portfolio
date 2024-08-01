package com.wanted.portfolio.member.repository;

import com.wanted.portfolio.member.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);
}
