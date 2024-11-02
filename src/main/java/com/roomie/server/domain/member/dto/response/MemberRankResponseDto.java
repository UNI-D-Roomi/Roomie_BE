package com.roomie.server.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberRankResponseDto {
    @Schema(description = "랭크", example = "1")
    private Integer rank;

    @Schema(description = "포인트", example = "100")
    private Integer points;
}
