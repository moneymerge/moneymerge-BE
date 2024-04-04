package example.com.meneymergebe.global.security;

import static example.com.meneymergebe.global.jwt.JwtUtil.ACCESS_TOKEN_HEADER;

import example.com.meneymergebe.global.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 토큰을 검증하는 인가 필터
 * @author Lee Yejin
 */
@Slf4j(topic = "JWT validation & authorization")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String accessToken = jwtUtil.getAccessTokenFromCookie(request);
        log.info("Access Token: {}", accessToken);

        // access token 비어있으면 인증 미처리
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // access token 검증
        switch(jwtUtil.validateToken(accessToken)) {
            case VALID -> setAuthentication(jwtUtil.getUserIdFromToken(accessToken));
            case INVALID -> throw new RuntimeException(); // TODO: 공통 예외 처리하기 (Unauthorized)
            case EXPIRED -> authenticateRefreshToken(request, response);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 처리 (인증 객체를 생성하여 context에 설정)
     */
    private void setAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * Refresh token 검증 (access token이 만료된 경우)
     */
    private void authenticateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        log.info("Refresh Token: {}", refreshToken);

        if(refreshToken == null) throw new RuntimeException(); // TODO: 공통 예외 처리하기 (refresh token 요청)

        switch(jwtUtil.validateToken(refreshToken)) {
            case VALID -> renewAccessToken(response, refreshToken);
            case INVALID -> throw new RuntimeException(); // TODO: 공통 예외 처리하기 (Unauthorized)
            case EXPIRED -> throw new RuntimeException(); // TODO: 공통 예외 처리하기 (재로그인 요청)
        }
    }

    /**
     * Access token 재발급 후 요청 처리
     */
    private void renewAccessToken(HttpServletResponse response, String refreshToken) {
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        String accessToken = jwtUtil.createAccessToken(userId, role);
        response.addHeader(ACCESS_TOKEN_HEADER, accessToken);

        setAuthentication(userId);
    }
}
