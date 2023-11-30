package com.e1i5.stackOverflow.auth.filter;

import com.e1i5.stackOverflow.auth.jwt.JwtTokenizer;
import com.e1i5.stackOverflow.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 *클라이언트에서 JWT를 이용해 자격 증명이 필요한 리소스에 대한 request 전송 시, request header를 통해 전달받은 JWT를 서버 측에서 검증하는 기능을 구현
 * */
@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, // JwtTokenizer는 JWT를 검증하고 Claims(토큰에 포함된 정보)를 얻는 데 사용됩니다.
                                 CustomAuthorityUtils authorityUtils) {  // CustomAuthorityUtils는 JWT 검증에 성공하면 Authentication 객체에 채울 사용자의 권한을 생성하는 데 사용됩니다.
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Map<String, Object> claims = verifyJws(request); //JWT를 검증하는 데 사용되는 private 메서드
//        setAuthenticationToContext(claims);      // Authentication 객체를 SecurityContext에 저장하기 위한 private 메서드
//
//        filterChain.doFilter(request, response); //문제없이 JWT의 서명 검증에 성공하고, Security Context에 Authentication을 저장한 뒤에는 (5다음(Next) Security Filter를 호출.
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);// security filter 호출
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");  // Authorization header의 값을 얻은 후에

        return authorization == null || !authorization.startsWith("Bearer");  // Authorization header의 값이 null이거나 Authorization header의 값이 “Bearer”로 시작하지 않는다면 해당 Filter의 동작을 수행하지 않도록 정의합니다.
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ", ""); // request의 header에서 JWT를 얻고 있습니다.
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()); //JWT 서명(Signature)을 검증하기 위한 Secret Key를 얻습니다.
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();   //JWT에서 Claims를 파싱 합니다. > 내부적으로 서명검증에 성공했음을 의미.

        return claims;
    }


    private void setAuthenticationToContext(Map<String, Object> claims) { // claims에서 사용자 정보를 controller로 전송 가능 - dto 이용
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("MemberId in SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication().getDetails());
    }

}
