package com.roomie.server.domain.roomie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompareType {
    ROOM("ROOM"),
    WASH("WASH");

    private final String content;
}
