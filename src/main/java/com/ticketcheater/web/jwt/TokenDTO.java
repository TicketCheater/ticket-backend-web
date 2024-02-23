package com.ticketcheater.web.jwt;

public record TokenDTO(String accessToken, String refreshToken) {

    public static TokenDTO of(String accessToken, String refreshToken) {
        return new TokenDTO(accessToken, refreshToken);
    }

}
