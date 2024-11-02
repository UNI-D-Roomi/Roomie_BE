package com.roomie.server.domain.member.presentation;

import com.roomie.server.domain.member.application.MemberService;
import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.dto.JwtToken;
import com.roomie.server.domain.member.dto.request.SignInRequestDto;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import com.roomie.server.global.config.security.SecurityService;
import com.roomie.server.global.config.security.userDetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final SecurityService securityService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입을 합니다.")
    public MemberResponseDto signUp(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        return memberService.signUp(signUpRequestDto);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "로그인을 합니다.")
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

}
