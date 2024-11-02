package com.roomie.server.global.config.jwt;

import com.roomie.server.domain.member.dto.JwtToken;
import com.roomie.server.global.config.redis.RedisUtils;
import com.roomie.server.global.config.security.userDetails.CustomUserDetailsService;
import com.roomie.server.global.exceptions.ErrorCode;
import com.roomie.server.global.exceptions.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final RedisUtils redisUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final Long expirationMs;
    private final Long refreshExpirationMs;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secretKey,
            RedisUtils redisUtils,
            CustomUserDetailsService customUserDetailsService,
            @Value("${spring.jwt.expirationMs}") Long expirationMs,
            @Value("${spring.jwt.refreshExpirationMs}") Long refreshExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisUtils = redisUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + expirationMs);
        String accessToken = Jwts.builder()
                .setIssuedAt(new Date(now))
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        System.out.println(authentication.getName());

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + refreshExpirationMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        /*
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);

         */
        /*
        String userPk = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();

         */

        String userPk = getUserPk(accessToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userPk);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String accessToken) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(accessToken)
                .getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 정보를 검증하는 메서드, (토큰이 만료되었는지, 블랙리스트에 있는지)
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(this.key).parseClaimsJws(token);

            if (redisUtils.isTokenBlacklisted(token)) {
                throw new UnauthorizedException(ErrorCode.INVALID_JWT, "블랙리스트에 등록된 토큰입니다.");
            }

            return true;

        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new UnauthorizedException(ErrorCode.INVALID_JWT, "유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new UnauthorizedException(ErrorCode.INVALID_JWT, "만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new UnauthorizedException(ErrorCode.INVALID_JWT, "지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new UnauthorizedException(ErrorCode.INVALID_JWT, "토큰이 비어있습니다.");
        }
    }


}