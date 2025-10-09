package com.example.taskapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-Correlation-Id";
    private static final String KEY = "cid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cid = Optional.ofNullable(request.getHeader(HEADER)).orElse(UUID.randomUUID().toString());
        MDC.put(KEY, cid);
        try {
            response.setHeader(HEADER, cid);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(KEY);
        }
    }
}