package com.eduribeiro8.LilMarket.security.ratelimiting;

import com.eduribeiro8.LilMarket.entity.User;
import com.eduribeiro8.LilMarket.entity.UserRole;
import com.eduribeiro8.LilMarket.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitingFilter Unit Tests")
class RateLimitingFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest("GET", "/product/1");
        request.setServletPath("/product/1");
        request.setRemoteAddr("203.0.113.10");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Authenticated Requests")
    class AuthenticatedRequests {

        @Test
        @DisplayName("Deve aplicar rate limit por role e ignorar o bucket de IP quando autenticado")
        void authenticatedManager_ShouldUseRoleBucket_AndSkipIpBucket() throws Exception {
            // Arrange
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            "manager",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
                    )
            );

            User managerUser = User.builder()
                    .username("manager")
                    .role(UserRole.ROLE_MANAGER)
                    .build();

            Mockito.when(userService.findUserByUsername("manager")).thenReturn(managerUser);

            Bucket exhaustedIpBucket = Bucket.builder()
                    .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofMinutes(1))))
                    .build();
            exhaustedIpBucket.tryConsume(1);

            @SuppressWarnings("unchecked")
            Map<String, Bucket> ipBuckets = (Map<String, Bucket>) ReflectionTestUtils.getField(rateLimitingFilter, "IP_BUCKET");
            assertNotNull(ipBuckets);
            ipBuckets.put("203.0.113.10", exhaustedIpBucket);

            // Act
            rateLimitingFilter.doFilter(request, response, filterChain);

            // Assert
            assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Mockito.verify(userService).findUserByUsername("manager");
            Mockito.verify(filterChain).doFilter(any(), any());
            Mockito.verifyNoMoreInteractions(userService, filterChain);
        }
    }

    @Nested
    @DisplayName("Anonymous Requests")
    class AnonymousRequests {

        @Test
        @DisplayName("Deve bloquear requisição anônima quando o bucket de IP estiver esgotado")
        void anonymousRequest_ShouldBeBlockedByIpBucket() throws Exception {
            // Arrange
            Bucket exhaustedIpBucket = Bucket.builder()
                    .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofMinutes(1))))
                    .build();
            exhaustedIpBucket.tryConsume(1);

            @SuppressWarnings("unchecked")
            Map<String, Bucket> ipBuckets = (Map<String, Bucket>) ReflectionTestUtils.getField(rateLimitingFilter, "IP_BUCKET");
            assertNotNull(ipBuckets);
            ipBuckets.put("203.0.113.10", exhaustedIpBucket);

            // Act
            rateLimitingFilter.doFilter(request, response, filterChain);

            // Assert
            assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
            Mockito.verifyNoInteractions(userService, filterChain);
        }
    }
}
