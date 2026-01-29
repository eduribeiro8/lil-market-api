package com.eduribeiro8.LilMarket.security.ratelimiting;

import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.rest.exception.UserNotFoundException;
import com.eduribeiro8.LilMarket.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final UserService userDAO;
    private final Map<String, Bucket> USER_BUCKET = new ConcurrentHashMap<>();
    private final Map<String, Bucket> IP_BUCKET = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(UserService userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String ipAddress = request.getRemoteAddr();
        Bucket ipBucket = IP_BUCKET.computeIfAbsent(ipAddress, k -> newBucket("ip"));

        if (!ipBucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        String name = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;

        if (name == null || name.equals("anonymousUser")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            User user = userDAO.findUserByUsername(name);
            Bucket userBucket = USER_BUCKET.computeIfAbsent(name, k -> newBucket(String.valueOf(user.getRole())));

            if (userBucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
        } catch (UserNotFoundException ex) {
            chain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars");
    }

    private Bucket newBucket(String type) {
        if ("ROLE_USER".equals(type)) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(100, Refill.greedy(10, Duration.ofMinutes(1))))
                    .build();
        } else if ("ROLE_ADMIN".equals(type)) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(10000, Refill.greedy(100, Duration.ofMinutes(1))))
                    .build();
        } else if ("ip".equals(type)) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                    .build();
        }
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(3))))
                .build();
    }
}