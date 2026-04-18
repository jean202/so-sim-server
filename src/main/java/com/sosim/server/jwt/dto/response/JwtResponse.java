package com.sosim.server.jwt.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class JwtResponse {

    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    @JsonIgnore
    private String deviceId;

    public static JwtResponse create(String accessToken, String refreshToken, String deviceId) {
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .build();
    }
}
