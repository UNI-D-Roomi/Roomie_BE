package com.roomie.server.global.exceptions;

public class BadRequestException extends BaseRuntimeException {

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

}
