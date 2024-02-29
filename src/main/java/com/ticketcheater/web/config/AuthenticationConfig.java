package com.ticketcheater.web.config;

import com.ticketcheater.web.exception.CustomAuthenticationEntryPoint;
import com.ticketcheater.web.jwt.JwtTokenFilter;
import com.ticketcheater.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final UserService userService;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/login", "/users/signup").permitAll()
                        .requestMatchers("/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(c -> c.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
