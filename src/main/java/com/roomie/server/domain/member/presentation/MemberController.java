package com.roomie.server.domain.member.presentation;

import com.roomie.server.domain.member.application.MemberService;
import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.dto.JwtToken;
import com.roomie.server.domain.member.dto.request.SignInRequestDto;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberRankResponseDto;
import com.roomie.server.domain.member.dto.response.MemberRankingDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import com.roomie.server.global.config.security.SecurityService;
import com.roomie.server.global.config.security.userDetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SecurityService securityService;

    @Operation(summary = "회원가입", description = "회원가입을 합니다.")
    @PostMapping("/sign-up")
    public MemberResponseDto signUp(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        return memberService.signUp(signUpRequestDto);
    }

    @Operation(summary = "자기 방 사진 추가", description = "자기 방 사진을 추가합니다.")
    @PostMapping("/room-image")
    public MemberResponseDto setRoomImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader String imageUrl
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return memberService.setRoomImage(member.getId(), imageUrl);
    }


    @Operation(summary = "로그인", description = "로그인을 합니다.")
    @PostMapping("/sign-in")
    public JwtToken signIn(
            @RequestBody SignInRequestDto signInRequestDto
    ) {
        return memberService.signIn(signInRequestDto.getLoginId(), signInRequestDto.getPassword());
    }

    @Operation(summary = "로그인 테스트 API, test 용도")
    @PostMapping("/test")
    public Long test(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return memberService.test(member);
    }

    @Operation(summary = "전체 순위 조회", description = "전체 순위를 조회합니다.")
    @GetMapping("/grade")
    public List<MemberRankingDto> getGradeRank(){
        return memberService.getGradeRank();
    }

    @Operation(summary = "내 순위 조회", description = "내 순위를 조회합니다.")
    @GetMapping("/grade/self")
    public MemberRankResponseDto getMemberRank(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return memberService.getMemberRank(member.getId());
    }

    @Operation(summary = "현재 사용자 조회", description = "현재 사용자를 조회합니다.")
    @GetMapping("/current")
    public MemberResponseDto getCurrentMember(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = securityService.getUserByUserDetails(userDetails);

        return memberService.getCurrentUser(member.getId());
    }

}
