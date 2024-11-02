package com.roomie.server.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRankResponseDto {
    private int rank;
    private int points;
}
