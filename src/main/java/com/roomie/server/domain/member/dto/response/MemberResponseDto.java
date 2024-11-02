package com.roomie.server.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {

    @Schema(description = "회원 식별자", example = "1")
    private Long id;

    @Schema(description = "아이디", example = "testId123")
    private String loginId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "포인트", example = "100")
    private Integer points;

    @Schema(description = "방 이미지 URL", example = "https://roomie.com/room/1")
    private String roomImageUrl;

    @Schema(description = "Roomie id", example = "1")
    private Long roomieId;

}
