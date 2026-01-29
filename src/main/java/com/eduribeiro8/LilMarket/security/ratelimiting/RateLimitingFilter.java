package com.eduribeiro8.LilMarket.security.ratelimiting;

import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends HttpFilter {

    private final UserService userDAO;

    private final Map<String, Bucket> USER_BUCKET = new ConcurrentHashMap<>();
    private final Map<String, Bucket> IP_BUCKET = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(UserService userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String name = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        String ipAddress = request.getRemoteAddr();

        Bucket bucket;
        bucket = IP_BUCKET.computeIfAbsent(ipAddress, k -> newBucket("ip"));

        if (bucket.tryConsume(1)){
            User user = userDAO.findUserByUsername(name);
            if (user != null){
                bucket.addTokens(1);
                bucket = USER_BUCKET.computeIfAbsent(name, k -> newBucket(String.valueOf(user.getRole())));
                if (bucket.tryConsume(1)) {
                    chain.doFilter(request, response);
                } else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                }
            }
        }else{
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
//        System.out.println("IP " + ipAddress + " has " + bucket.getAvailableTokens() +" requests left.");
    }

    private Bucket newBucket(String type) {
        if ("ROLE_USER".equals(type)) {
            Bandwidth limit = Bandwidth.classic(100, Refill.greedy(10, Duration.ofMinutes(1)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        } else if ("ROLE_ADMIN".equals(type)) {
            Bandwidth limit = Bandwidth.classic(10000, Refill.greedy(100, Duration.ofMinutes(1)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        }
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(3)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

