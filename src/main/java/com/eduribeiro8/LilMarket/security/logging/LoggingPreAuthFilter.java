package com.eduribeiro8.LilMarket.security.logging;

import com.eduribeiro8.LilMarket.security.RequestIpResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoggingPreAuthFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String clientIp = RequestIpResolver.resolveClientIp(request);

        Long startTime = System.currentTimeMillis();

        logger.info("Incoming request from IP={}: {} {}",
                clientIp,
                request.getMethod(),
                request.getRequestURI());

        chain.doFilter(request, response);

        Long endTime = System.currentTimeMillis();

        logger.info("Response to IP={}: status={} duration={}ms",
                clientIp,
                response.getStatus(),
                endTime - startTime);
    }

}

