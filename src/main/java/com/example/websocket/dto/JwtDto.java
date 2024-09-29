package com.example.websocket.dto;

public record JwtDto(
        String accessToken,
        String refreshToken) {
}
