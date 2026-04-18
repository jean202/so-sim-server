package com.sosim.server.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class CookieUtil {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFRESH_COOKIE = "RefreshToken";
    private static final String DEVICE_ID_COOKIE = "deviceId";

    private static Long refreshExpiration;
    private static String domain;

    @Value("${jwt.refresh.expiration}")
    public void setRefreshExpiration(Long refreshExpiration) {
        CookieUtil.refreshExpiration = refreshExpiration;
    }

    @Value("${jwt.cookie.domain}")
    public void setDomain(String domain) {
        CookieUtil.domain = domain;
    }

    public static void setTokenCookies(HttpServletResponse response, String refreshToken, String deviceId) {
        setCookie(REFRESH_COOKIE, refreshToken, refreshExpiration / 1000, response);
        setCookie(DEVICE_ID_COOKIE, deviceId, refreshExpiration / 1000, response);
    }

    public static void setCookieRefreshToken(HttpServletResponse response, String refreshToken) {
        setCookie(REFRESH_COOKIE, refreshToken, refreshExpiration / 1000, response);
    }

    public static void deleteRefreshToken(HttpServletResponse response) {
        deleteTokenCookies(response);
    }

    public static void deleteTokenCookies(HttpServletResponse response) {
        setCookie(REFRESH_COOKIE, "", 0, response);
        setCookie(DEVICE_ID_COOKIE, "", 0, response);
    }

    public static String getRefreshToken(HttpServletRequest request) {
        Optional<Cookie[]> cookiesOp = Optional.ofNullable(request.getCookies());
        return cookiesOp
                .map(cookies -> findCookieValue(cookies, REFRESH_COOKIE))
                .orElse(null);
    }

    public static String getDeviceId(HttpServletRequest request) {
        Optional<Cookie[]> cookiesOp = Optional.ofNullable(request.getCookies());
        return cookiesOp
                .map(cookies -> findCookieValue(cookies, DEVICE_ID_COOKIE))
                .orElse(null);
    }

    private static String findCookieValue(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie != null && name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static void setCookie(String name, String value, long maxAge, HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(maxAge)
                .path("/");

        if (domain != null && !domain.isBlank()) {
            cookieBuilder.domain(domain);
        }

        response.addHeader(SET_COOKIE, cookieBuilder.build().toString());
    }
}
