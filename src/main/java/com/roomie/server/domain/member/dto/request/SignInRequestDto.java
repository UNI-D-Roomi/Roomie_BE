package com.roomie.server.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SignInRequestDto {

        @Schema(description = "로그인 아이디", example = "testId123")
        private String loginId;

        @Schema(description = "비밀번호", example = "testPassword123")
        private String password;

}
