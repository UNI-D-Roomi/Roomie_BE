package com.roomie.server.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class MemberResponseDto {

    @Schema(description = "회원 식별자", example = "1")
    private Long id;

    @Schema(description = "아이디", example = "testId123")
    private String loginId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

}
