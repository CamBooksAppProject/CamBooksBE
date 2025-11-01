package org.example.v1.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String token = httpServletRequest.getHeader("Authorization");

        // 토큰이 제공된 경우에만 검증 로직을 수행합니다. 검증 중 발생하는 인증 관련 예외만 여기서 처리하고
        // 그 외 예외는 필터 체인으로 전달하여 적절한 핸들러가 처리하도록 둡니다.
        if (token != null) {
            try {
                if (!token.startsWith("Bearer ")) {
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationServiceException | io.jsonwebtoken.JwtException authEx) {
                // 인증/토큰 오류는 401로 응답합니다. 단, 이미 응답이 커밋되었거나 출력 스트림이 사용된 경우에는
                // 추가로 쓰지 않고 로그만 남깁니다.
                authEx.printStackTrace();
                if (!httpServletResponse.isCommitted()) {
                    try {
                        httpServletResponse.resetBuffer();
                        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                        httpServletResponse.setContentType("application/json;charset=UTF-8");
                        // getWriter()가 이미 OutputStream 사용으로 불가능한 경우 대비
                        try {
                            httpServletResponse.getWriter().write("{\"error\":\"invalid token\"}");
                        } catch (IllegalStateException ise) {
                            // OutputStream이 이미 사용된 경우에는 바이트로 직접 씁니다.
                            try {
                                httpServletResponse.getOutputStream().write("{\"error\":\"invalid token\"}".getBytes());
                            } catch (Exception writeEx) {
                                writeEx.printStackTrace();
                            }
                        }
                    } catch (IllegalStateException e) {
                        // 응답이 이미 커밋됐거나 스트림 상태로 인해 쓰기 불가한 경우 그냥 로그만 남깁니다.
                        System.err.println("응답 작성 불가(이미 커밋됨) - 인증 오류 처리 중:");
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("응답이 이미 커밋되어 인증 오류 응답을 작성하지 못했습니다.");
                }
                return; // 인증 실패이면 필터 체인 진행 중지
            }
        }

        // 토큰이 없거나 정상 처리된 경우에 한해 다음 필터로 진행
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
