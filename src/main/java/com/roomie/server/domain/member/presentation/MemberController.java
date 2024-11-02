package com.roomie.server.domain.member.presentation;

import com.roomie.server.domain.member.application.MemberService;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "회원가입을 합니다.")
    public ResponseEntity<MemberResponseDto> signUp(
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberService.signUp(signUpRequestDto));
    }

}
