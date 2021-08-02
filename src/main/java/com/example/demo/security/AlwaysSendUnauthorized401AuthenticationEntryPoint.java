package com.example.demo.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AlwaysSendUnauthorized401AuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public final void commence(HttpServletRequest request, HttpServletResponse response,
                               AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
