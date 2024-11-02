package com.roomie.server.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BaseRuntimeException extends RuntimeException {

    private final ErrorCode errorCode;

    private final String message;

}
