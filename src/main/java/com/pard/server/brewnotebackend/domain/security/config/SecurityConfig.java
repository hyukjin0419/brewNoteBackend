//package com.pard.server.brewnotebackend.domain.security.config;
//
//import com.pard.server.brewnotebackend.domain.security.jwt.JwtAuthenticationFilter;
//import com.pard.server.brewnotebackend.domain.security.jwt.JwtTokenProvider;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfig {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                //session 기능 비활성화
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
//
//                //stateless선언
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                //전역 인가 설정
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/**", "/auth/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                //인증 및 인가에러시 반환 response 설정
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((req, res, ex) ->
//                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
//                        )
//                        .accessDeniedHandler((req, res, ex) ->
//                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
//                        )
//                )
//                //jwt fileter은 기존 필터보다 앞에 배치
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
//                        UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//}
