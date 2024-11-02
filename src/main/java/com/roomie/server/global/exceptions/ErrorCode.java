package com.roomie.server.global.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    ROW_DOES_NOT_EXIST(4000, "해당 데이터가 존재하지 않습니다."),
    ROW_ALREADY_EXIST(4001, "해당 데이터가 이미 존재합니다."),
    INVALID_PARAMETER(4002, "파라미터가 유효하지 않습니다."),            // Parameter format error
    INVALID_USER_DATA_REQUEST(4003, "유효하지 않은 사용자 데이터 입니다."),    // User signup & update validation error
    TARGET_DELETED(4004, "타겟이 삭제되었습니다."),
    INVALID_HTTP_METHOD(4005, "유효하지 않은 HTTP 메서드입니다."),
    INVALID_FILE_EXTENSION(4016, "유효하지 않은 파일 확장자 입니다."),

    /**
     * 401 Unauthorized
     */
    API_NOT_ACCESSIBLE(4100, "접근 권한이 없습니다."),
    INVALID_SIGNIN(4101, "유효하지 않은 로그인입니다."),
    INACTIVE_USER(4102, "사용자가 활성화 되어있지 않습니다."),            // User is not activated
    INVALID_JWT(4103, "유효하지 않은 토큰입니다."),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER(5000, "서버 에러 입니다. 관리자에게 문의 바랍니다."),
    FILE_UPLOAD_FAIL(5001, "파일 업로드를 실패했습니다. 재시도 후 작동하지 않는다면 관리자 문의 바랍니다."),
    FILE_DELETE_FAIL(5002, "파일 삭제를 실패했습니다. 재시도 후 작동하지 않는다면 관리자 문의 바랍니다."),
    DATA_INTEGRITY_INVALID(5003, "데이터 무결성에 문제가 발생했습니다. 관리자에게 문의 바랍니다."),

    /**
     * 503 Service Unavailable Error
     */
    SERVICE_UNAVAILABLE(5030, "서비스가 유효하지 않습니다. 관리자에게 문의 바랍니다.");

    private final int code;

    private final String message;

}
