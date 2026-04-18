package com.sosim.server.jwt.controller;

import static com.sosim.server.common.response.ResponseCode.SUCCESS_LOGIN;

import com.sosim.server.common.response.Response;
import com.sosim.server.common.util.CookieUtil;
import com.sosim.server.jwt.dto.response.JwtResponse;
import com.sosim.server.jwt.service.JwtService;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/test")
public class TestTokenController {

    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> issueToken(@RequestParam("userId") Long userId, HttpServletResponse response) {
        JwtResponse token = jwtService.createToken(userId);
        CookieUtil.setTokenCookies(response, token.getRefreshToken(), token.getDeviceId());
        return ResponseEntity.ok(Response.create(SUCCESS_LOGIN, token));
    }

    @GetMapping("/devices")
    public ResponseEntity<Map<Object, Object>> getDevices(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(jwtService.getAllDevices(userId));
    }
}
