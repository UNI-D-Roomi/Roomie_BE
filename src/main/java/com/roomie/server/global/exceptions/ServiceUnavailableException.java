package com.roomie.server.global.exceptions;

public class ServiceUnavailableException extends BaseRuntimeException {

    public ServiceUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ServiceUnavailableException(ErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

}
