package com.roomie.server.domain.member.domain.repository;

import com.roomie.server.domain.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    Boolean existsByLoginId(String loginId);

    List<Member> findAllByOrderByPointsDesc();

    @Query("SELECT COUNT(m) + 1 FROM Member m WHERE m.points > (SELECT points FROM Member WHERE id = :memberId)")
    Optional<Integer> findRankByMemberId(@Param("memberId") Long memberId);

}
