package com.eduribeiro8.LilMarket.security.logging;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userName = request.getUserPrincipal() == null ? "anonymous" : request.getUserPrincipal().getName();

        Long startTime = System.currentTimeMillis();

        String queryString = request.getQueryString();
        String fullPath = request.getRequestURI() + (queryString != null ? "?" + queryString : "");

        logger.info("Incoming request from user={}({}): {} {}",
                userName,
                request.getRemoteAddr(),
                request.getMethod(),
                fullPath);

        filterChain.doFilter(request, response);

        Long endTime = System.currentTimeMillis();

        logger.info("Response to user={}: status={} duration={}ms",
                userName,
                response.getStatus(),
                endTime - startTime);
    }
}
