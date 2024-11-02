package com.roomie.server.domain.member.presentation;

import com.roomie.server.domain.member.application.MemberService;
import com.roomie.server.domain.member.domain.Member;
import com.roomie.server.domain.member.dto.request.SignUpRequestDto;
import com.roomie.server.domain.member.dto.response.MemberRankResponseDto;
import com.roomie.server.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/member")
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

    @GetMapping("/grade")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "전체 순위 조회", description = "전체 순위를 조회합니다.")
    public List<Member> getGradeRank(){
        return memberService.getGradeRank();
    }

    @GetMapping("/grade/{memberId}/rank")
    public ResponseEntity<Object> getMemberRank(@PathVariable Long memberId) {
        Optional<MemberRankResponseDto> memberRankResponse = memberService.getMemberRank(memberId);
        if (memberRankResponse.isPresent()) {
            return ResponseEntity.ok(memberRankResponse.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Member not found in ranking list.");
        }
    }
}
