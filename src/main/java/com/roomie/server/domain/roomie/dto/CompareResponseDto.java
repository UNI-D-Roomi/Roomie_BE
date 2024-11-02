package com.roomie.server.domain.roomie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompareResponseDto {

    Double score;

    String comment;

}
