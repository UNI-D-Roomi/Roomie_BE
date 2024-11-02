package com.roomie.server.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    @Schema(description = "아이디", example = "testId123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String loginId;

    @Schema(description = "비밀번호", example = "testPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

}
