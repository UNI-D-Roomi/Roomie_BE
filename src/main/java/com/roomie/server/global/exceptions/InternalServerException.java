package com.roomie.server.global.exceptions;

public class InternalServerException extends BaseRuntimeException {

    public InternalServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
