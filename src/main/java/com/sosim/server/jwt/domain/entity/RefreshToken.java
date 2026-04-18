package com.sosim.server.jwt.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshToken {

    private Long userId;
    private String deviceId;
    private String refreshToken;

    public static RefreshToken create(Long userId, String deviceId, String refreshToken) {
        return RefreshToken.builder()
                .userId(userId)
                .deviceId(deviceId)
                .refreshToken(refreshToken)
                .build();
    }
}
