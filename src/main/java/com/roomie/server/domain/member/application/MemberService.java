package com.roomie.server.domain.member.application;

import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.domain.repository.MemberRepository;
import com.roomie.server.domain.member.dto.JwtToken;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberRankResponseDto;
import com.roomie.server.domain.member.dto.response.MemberRankingDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import com.roomie.server.domain.roomie.domain.Roomie;
import com.roomie.server.global.config.jwt.JwtTokenProvider;
import com.roomie.server.global.config.redis.RedisUtils;
import com.roomie.server.global.dtoMapper.MemberDtoMapper;
import com.roomie.server.global.exceptions.BadRequestException;
import com.roomie.server.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtils redisUtils;

    @Transactional
    public MemberResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        if (memberRepository.existsByLoginId(signUpRequestDto.getLoginId())) {
            throw new BadRequestException(ErrorCode.ROW_ALREADY_EXIST, "이미 존재하는 아이디입니다.");
        }

        Roomie roomie = Roomie.of();

        Member member = Member.of(
                signUpRequestDto.getLoginId(),
                passwordEncoder.encode(signUpRequestDto.getPassword()),
                signUpRequestDto.getName(),
                roomie
        );

        memberRepository.save(member);

        return this.toResponseDto(member);
    }


    @Transactional
    public JwtToken signIn(String loginId, String password) {

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        // authenticationManagerBuilder -> authenticationManager 대체 사용(버전 문제)
        // Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        redisUtils.setRefreshTokenData(jwtToken.getRefreshToken(), member.getId());

        return jwtToken;
    }

    private MemberResponseDto toResponseDto(Member member) {
        return MemberDtoMapper.INSTANCE.toMemberResponseDto(member);
    }


    public List<MemberRankingDto> getGradeRank() {
        return memberRepository.findAllByOrderByPointsDesc()
                .stream()
                .map(member -> new MemberRankingDto(member.getId(), member.getName(), member.getPoints()))
                .collect(Collectors.toList());
    }

    public MemberRankResponseDto getMemberRank(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        Optional<Integer> rank = memberRepository.findRankByMemberId(memberId);

        return MemberRankResponseDto.builder()
                .rank(rank.orElse(0))
                .points(member.getPoints())
                .build();
    }

    public MemberResponseDto getCurrentUser(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        return MemberDtoMapper.INSTANCE.toMemberResponseDto(member);
    }




    @Transactional
    public MemberResponseDto setRoomImage(Long id, String imageUrl) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        member.setRoomImageUrl(imageUrl);

        memberRepository.save(member);

        return this.toResponseDto(member);
    }



    public Long test(Member member) {
        Member member1 = memberRepository.findById(member.getId()).orElseThrow(
                () -> new BadRequestException(ErrorCode.ROW_DOES_NOT_EXIST, "해당하는 회원을 찾을 수 없습니다."));

        return member1.getId();
    }

}
