package com.roomie.server.global.config.security;

import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.exceptions.UnauthorizedException;
import com.roomie.server.global.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        //디폴트 오류처리 설정
        ErrorCode errorCode = ErrorCode.INVALID_JWT;
        String message = MessageUtil.INVALID_TOKEN;

        UnauthorizedException exception = (UnauthorizedException) request.getAttribute("exception");

        if (exception != null) {
            errorCode = exception.getErrorCode();
            message = exception.getMessage();
        }

        setResponse(response, errorCode, message);
    }

    private void setResponse(
            HttpServletResponse response,
            ErrorCode errorCode,
            String message
    ) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                "{" +
                        "\"errorCode\" : \"" + errorCode.getCode() + "\"," +
                        "\"message\" : \"" + message + "\"," +
                        "\"timeStamp\" : \"" + LocalDateTime.now() + "\"" +
                        "}"
        );
    }

}
