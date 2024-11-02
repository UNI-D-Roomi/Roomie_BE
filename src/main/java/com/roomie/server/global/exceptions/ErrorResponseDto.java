package com.roomie.server.global.exceptions;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponseDto {

    private Integer errorCode;

    private String message;

    public static ErrorResponseDto of(BaseRuntimeException baseRuntimeException) {
        if (!baseRuntimeException.getMessage().equals(baseRuntimeException.getErrorCode().getMessage())) {
            return ErrorResponseDto.builder()
                    .errorCode(baseRuntimeException.getErrorCode().getCode())
                    .message(baseRuntimeException.getMessage())
                    .build();
        } else {
            return ErrorResponseDto.builder()
                    .errorCode(baseRuntimeException.getErrorCode().getCode())
                    .message(baseRuntimeException.getErrorCode().getMessage())
                    .build();
        }
    }

}
