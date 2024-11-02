package com.roomie.server.global.config.security;

import com.roomie.server.global.config.jwt.JwtAuthenticationFilter;
import com.roomie.server.global.config.jwt.JwtTokenProvider;
import com.roomie.server.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${deploy.fe}")
    private String feHost;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedOrigin("http://localhost:5173");
        corsConfig.addAllowedOrigin("http://localhost:8080");
        corsConfig.addAllowedOrigin(feHost);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(Duration.ofSeconds(3600));
        corsConfig.addExposedHeader("Authorization");
        corsConfig.addExposedHeader("Authorization-Refresh");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // 설정한 Configuration 적용

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // SessionManagementConfigurer의 대체 방법으로 세션 정책을 수동 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // CORS 테스트 API에 대해서는 모든 요청을 허가
                                .requestMatchers("/cors/**").permitAll()
                                // 로그인 API에 대해서는 모든 요청을 허가
                                .requestMatchers("/api/v1/member/sign-in").permitAll()
                                .requestMatchers("/api/v1/**").permitAll()
                                // 회원 가입 API에 대해서는 모든 요청을 허가
                                .requestMatchers("/api/v1/member/sign-up").permitAll()
                                // 토큰 갱신 API에 대해서는 모든 요청을 허가
                                .requestMatchers("/api/v1/member/update-token").permitAll()
                                // 중복 체크 api에 대해서는 모든 요청을 허가
                                .requestMatchers("/api/v1/member/duplicate_check/**").permitAll()
                                // USER 권한이 있어야 요청할 수 있음
                                .requestMatchers("/api/v1/member/test").hasAnyAuthority("AWAIT", "ACTIVE", "TEMPORARY")
                                // S3 upload 테스트 API
                                .requestMatchers("api/v1/uuid-file/**").permitAll()
                                // Swagger UI에 대해서는 모든 요청을 허가
                                .requestMatchers(
                                        "/auth/**",
                                        "/login/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html"
                                )
                                .permitAll()
                                // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                                .anyRequest().authenticated()
                )

                // 권한 문제 발생 시 AccessDeniedHandler 사용
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler())  // 403 Forbidden 시 커스텀 핸들러
                        .authenticationEntryPoint(authenticationEntryPoint())  // 인증 실패 시 커스텀 핸들러
                )
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(ErrorCode.API_NOT_ACCESSIBLE.getCode());
            response.setContentType("application/json");
            response.getWriter().write("{\"responseCode\":\"API_NOT_ALLOWED\",\"message\":\"API 접근이 허용되지 않습니다.\"}");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new Http403ForbiddenEntryPoint();  // 인증 실패 시 403 Forbidden 응답
    }

}
