package com.roomie.server.global.exceptions;

public class UnauthorizedException extends BaseRuntimeException {

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

}
