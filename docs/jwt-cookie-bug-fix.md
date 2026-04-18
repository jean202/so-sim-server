# JWT/Redis Refresh 개선 기록

## 요약

`active-develop`에는 기존 active 서버 구조를 유지하면서 다음 변경을 포팅했다.

- refresh token Redis 저장 구조를 유저별 hash 방식으로 변경
- 디바이스별 refresh token 관리 추가
- 로그인/재발급 시 `deviceId` 쿠키 동시 발급
- 로그아웃 시 Redis refresh token 무효화 추가
- 쿠키 조회를 null-safe 하게 수정

## 저장 구조 변경

기존 active 브랜치는 유저당 refresh token 하나만 저장했다.

```text
KEY = userId
VALUE = refreshToken
```

포팅 후 구조는 다음과 같다.

```text
KEY = refresh:{userId}
FIELD = deviceId
VALUE = refreshToken
TTL = 14일
```

이 구조로 바꾸면서 얻는 점은 다음과 같다.

- 여러 디바이스에서 동시에 로그인 가능
- 특정 디바이스만 로그아웃 가능
- 회원 탈퇴 시 유저 전체 refresh token 삭제 가능
- 서버가 refresh token과 device context를 같이 검증 가능

## 포팅 범위

### 코어 변경

- `RedisConfig`
- `JwtRepository`
- `JwtFactory`
- `JwtProvider`
- `JwtService`
- `JwtResponse`
- `LoginResponse`

### 컨트롤러/쿠키 변경

- `OAuthController`
- `JwtController`
- `CookieUtil`
- `UserController`

## 버그 성격의 보정

active 브랜치에는 이미 `/auth/refresh` 경로와 refresh cookie `maxAge` 반영이 들어가 있었기 때문에,
이번 포팅에서는 다음 문제를 중심으로 보정했다.

- `request.getCookies()` null-safe 처리
- refresh 재발급 시 `deviceId` 쿠키 누락 문제
- 로그아웃이 쿠키만 지우고 Redis 토큰은 남기던 문제

## 개발 검증용 엔드포인트

`dev` 프로필에서만 아래 엔드포인트를 추가했다.

- `POST /auth/test/token?userId={id}`
- `GET /auth/test/devices?userId={id}`

이 엔드포인트는 수동으로 refresh/device 동작을 확인할 때만 사용한다.
