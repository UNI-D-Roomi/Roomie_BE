package com.roomie.server.domain.member.presentation;

import com.roomie.server.domain.member.application.MemberService;
import com.roomie.server.domain.member.dto.JwtToken;
import com.roomie.server.domain.member.dto.request.SignInRequestDto;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

}
