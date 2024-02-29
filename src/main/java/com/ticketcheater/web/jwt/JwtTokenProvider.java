package com.ticketcheater.web.jwt;

import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.repository.TokenCacheRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final TokenCacheRepository tokenCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-key}")
    private String refreshKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpiredTimeMs;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpiredTimeMs;

    public static Boolean validate(String token, String username, String key) {
        String usernameByToken = getUsername(token, key);
        return usernameByToken.equals(username) && !isTokenExpired(token, key);
    }

    public static Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token, String key) {
        return extractAllClaims(token, key).get("username", String.class);
    }

    private static Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Boolean isTokenExpired(String token, String key) {
        Date expiration = extractAllClaims(token, key).getExpiration();
        return expiration.before(new Date());
    }

    public String generateAccessToken(String username) {
        return doGenerateToken(username, accessExpiredTimeMs, secretKey);
    }

    public String generateRefreshToken(String username) {
        String refreshToken = doGenerateToken(username, refreshExpiredTimeMs, refreshKey);
        tokenCacheRepository.setToken(username, refreshKey, refreshExpiredTimeMs);
        return refreshToken;
    }

    public void deleteRefreshToken(String username) {
        tokenCacheRepository.deleteToken(username);
    }

    public String reissueAccessToken(String username, String rtk) {
        String refreshToken = tokenCacheRepository.getToken(username);
        if (Objects.isNull(refreshToken)) {
            throw new TicketApplicationException(
                    ErrorCode.EXPIRED_TOKEN, String.format("The refresh token of username %s has expired", username)
            );
        }
        if (!username.equals(getUsername(rtk, refreshKey))) {
            throw new TicketApplicationException(
                    ErrorCode.INVALID_TOKEN, String.format("The refresh token of username %s is not valid", username)
            );
        }
        return generateAccessToken(username);
    }

    private static String doGenerateToken(String username, long expireTime, String key) {
        Claims claims = Jwts.claims();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

}
