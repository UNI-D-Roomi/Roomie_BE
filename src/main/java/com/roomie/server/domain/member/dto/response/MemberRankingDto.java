package com.roomie.server.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRankingDto {
    private Long id;
    private String name;
    private int points;
}
