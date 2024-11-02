package com.roomie.server.domain.member.application;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberRankResponseDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import com.roomie.server.global.dtoMapper.MemberDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        if (memberRepository.existsByLoginId(signUpRequestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.of(
                signUpRequestDto.getLoginId(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                signUpRequestDto.getName(),
                0
        );

        memberRepository.save(member);

        return this.toResponseDto(member);
    }

    private MemberResponseDto toResponseDto(Member member) {
        return MemberDtoMapper.INSTANCE.toMemberResponseDto(member);
    }

    public List<Member> getGradeRank(){
        return memberRepository.findAllByOrderByPointsDesc();
    }

    public Optional<MemberRankResponseDto> getMemberRank(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Optional<Integer> rank = memberRepository.findRankByMemberId(memberId);
        return rank.map(r -> new MemberRankResponseDto(r, member.getPoints()));
    }
}
