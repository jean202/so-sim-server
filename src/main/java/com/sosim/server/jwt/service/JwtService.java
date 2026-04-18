package com.sosim.server.jwt.service;

import com.sosim.server.common.advice.exception.CustomException;
import com.sosim.server.common.response.ResponseCode;
import com.sosim.server.jwt.domain.repository.JwtRepository;
import com.sosim.server.jwt.dto.response.JwtResponse;
import com.sosim.server.jwt.domain.util.JwtFactory;
import com.sosim.server.jwt.domain.util.JwtProvider;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtRepository jwtRepository;
    private final JwtFactory jwtFactory;
    private final JwtProvider jwtProvider;

    public JwtResponse createToken(Long userId) {
        String deviceId = UUID.randomUUID().toString();
        String refreshToken = jwtFactory.createRefreshToken(userId);
        jwtRepository.saveRefreshToken(userId, deviceId, refreshToken);
        return JwtResponse.create(jwtFactory.createAccessToken(userId), refreshToken, deviceId);
    }

    public JwtResponse refresh(String refreshToken, String deviceId) {
        if (refreshToken == null || deviceId == null) {
            throw new CustomException(ResponseCode.NOT_EXIST_TOKEN_COOKIE);
        }

        Long userId = jwtProvider.getRefreshUserId(refreshToken);
        String storedToken = jwtRepository.getRefreshToken(userId, deviceId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(ResponseCode.NOT_FOUNT_REFRESH);
        }
        jwtProvider.validateRefreshToken(refreshToken);

        String reIssuedRefreshToken = jwtFactory.createRefreshToken(userId);
        jwtRepository.saveRefreshToken(userId, deviceId, reIssuedRefreshToken);
        return JwtResponse.create(jwtFactory.createAccessToken(userId), reIssuedRefreshToken, deviceId);
    }

    public void deleteToken(long userId) {
        jwtRepository.deleteAllRefreshTokens(userId);
    }

    public void deleteToken(String refreshToken, String deviceId) {
        if (refreshToken == null || deviceId == null) {
            return;
        }

        try {
            Long userId = jwtProvider.getRefreshUserId(refreshToken);
            jwtRepository.deleteRefreshToken(userId, deviceId);
        } catch (CustomException ignored) {
            // 로그아웃은 쿠키 제거가 우선이므로 서버 측 정리는 가능한 경우에만 수행한다.
        }
    }

    public Map<Object, Object> getAllDevices(Long userId) {
        return jwtRepository.getAllDevices(userId);
    }
}
