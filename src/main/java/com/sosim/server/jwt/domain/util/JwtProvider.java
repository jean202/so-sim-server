package com.sosim.server.jwt.domain.util;

import com.sosim.server.common.advice.exception.CustomException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.sosim.server.common.response.ResponseCode.*;

@Component
public class JwtProvider {

    @Value("${jwt.access.key}")
    private String accessKey;

    @Value("${jwt.refresh.key}")
    private String refreshKey;

    public Long getUserId(String accessToken) {
        return Long.valueOf(getClaims(accessKey, accessToken, false).getSubject());
    }

    public Long getRefreshUserId(String refreshToken) {
        return Long.valueOf(getClaims(refreshKey, refreshToken, true).getSubject());
    }

    public void validateRefreshToken(String refreshToken) {
        getClaims(refreshKey, refreshToken, true);
    }

    private Claims getClaims(String key, String token, boolean isRefresh) {
        try {
            return Jwts.parser()
                    .setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | MalformedJwtException | MissingClaimException | IllegalArgumentException ex) {
            if (isRefresh) {
                throw new CustomException(MODULATION_REFRESH);
            }
            throw new CustomException(MODULATION_ACCESS);
        } catch (ExpiredJwtException ex) {
            if (isRefresh) {
                throw new CustomException(EXPIRATION_REFRESH);
            }
            throw new CustomException(EXPIRATION_ACCESS);
        }
    }
}
