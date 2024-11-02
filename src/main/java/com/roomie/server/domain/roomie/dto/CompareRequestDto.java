package com.roomie.server.domain.roomie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompareRequestDto {
    CompareType compareType;

    String beforeUrl;

    String afterUrl;
}
