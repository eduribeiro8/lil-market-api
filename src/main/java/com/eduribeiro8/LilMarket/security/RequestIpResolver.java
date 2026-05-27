package com.eduribeiro8.LilMarket.security;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestIpResolver {

    private RequestIpResolver() {
    }

    public static String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] addresses = forwardedFor.split(",");
            String clientIp = addresses[0].trim();

            if (!clientIp.isEmpty()) {
                return clientIp;
            }
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
