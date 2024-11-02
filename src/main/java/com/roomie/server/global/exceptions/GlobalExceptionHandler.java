package com.roomie.server.global.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.nio.file.AccessDeniedException;

@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler(View error) {
    }

    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestException(BadRequestException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(exception);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolationException(ConstraintViolationException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                new BadRequestException(ErrorCode.INVALID_PARAMETER)
        );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                new BadRequestException(ErrorCode.INVALID_PARAMETER)
        );
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadHttpRequestMethodException(HttpRequestMethodNotSupportedException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                new BadRequestException(ErrorCode.INVALID_HTTP_METHOD)
        );
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleUnauthorizedException(UnauthorizedException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                exception
        );
    }

    @ExceptionHandler(value = {ServiceUnavailableException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponseDto handleServiceUnavailableException(ServiceUnavailableException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                exception
        );
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleAccessDeniedException(AccessDeniedException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                new UnauthorizedException(ErrorCode.API_NOT_ACCESSIBLE)
        );
    }

    @ExceptionHandler(value = {AuthorizationDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleAuthorizationDeniedException(AuthorizationDeniedException exception) {
        GlobalExceptionHandler.log.debug("[Error message]", exception);
        return ErrorResponseDto.of(
                                new UnauthorizedException(ErrorCode.API_NOT_ACCESSIBLE)
        );
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto unknownException(Exception exception) {
        GlobalExceptionHandler.log.error("[Error message]", exception);
        return ErrorResponseDto.of(
                                new InternalServerException(ErrorCode.INTERNAL_SERVER)
        );
    }


}
