# Refactor Notes

이 문서는 주전부리(Pubburi) 리팩토링 방향과 현재 반영된 변경 사항을 기록한다.

## 목표

- Spring Boot backend와 Vue frontend를 로컬에서 바로 연결 가능한 구조로 정리한다.
- 외부 AI와 푸시 공급자 기능은 대체 없이 제거한 상태를 유지한다.
- 모든 API는 `/api` prefix와 `{ data, message, error }` 응답 wrapper를 사용한다.
- public repo 기준으로 실제 로컬 설정값과 개인성 seed를 제거한다.
- 변경 후 README, layer 문서, 성능 기록, agent guide를 함께 갱신한다.

## Backend 변경

- Spring Boot 3, MyBatis, MySQL 기반 jar 애플리케이션으로 정리했다.
- `spring-security-crypto`를 추가하고 신규 비밀번호 저장을 BCrypt로 교체했다.
- 기존 SHA-256 hash는 로그인 성공 시 BCrypt로 자동 전환되도록 호환 로직을 둔다.
- `spring-boot-starter-validation`과 request DTO를 추가해 로그인, 회원가입, 상품/매장 CRUD, 댓글, 주문 생성, 관리자 사용자 수정에 검증을 적용했다.
- `ApiResponse`, `ApiError`, `PageResponse`, `GlobalExceptionHandler`로 성공/실패 응답을 통일했다.
- session에는 비밀번호 없는 `SessionUser`만 저장하고, 권한 확인은 `AccessGuard`가 담당한다.
- 상품, 댓글, 주문, 매장, 관리자 사용자 목록에 `page/size` 기반 pagination과 count query를 추가했다.

## Frontend 변경

- Vue Router를 도입해 홈, 상품, 상세, 장바구니, 계정, 마이페이지, 관리자 화면을 route 단위로 분리했다.
- Pinia store를 도입해 인증, 장바구니, 카탈로그, 관리자, UI 알림 상태를 분리했다.
- `App.vue`는 shell 진입점으로 축소하고, 화면은 `views`, 공통 UI는 `components`, API 호출은 `services`로 나눴다.
- API client는 공통 wrapper를 unwrap하고 validation/error 정보를 한 가지 경로로 처리한다.
- `<picture>` 기반 이미지 fallback 컴포넌트를 추가하고 WebP가 있으면 우선 사용한다.

## Data 변경

- schema와 seed는 `Server/data.sql`에 유지한다.
- `t_user.password_hash`를 BCrypt 길이에 맞춰 `VARCHAR(100)`으로 변경했다.
- seed 계정 hash는 BCrypt 값으로 갱신했다.
- DB 이름 생성/사용 구문은 SQL에서 제거하고 로컬 환경변수에 맡긴다.
- 조회 성능을 위해 상품 타입, 상품 주문 수, 주문 사용자/시간, 주문 상세, 댓글 상품 index를 유지한다.

## 문서화 규칙

- layer 책임 변경은 `docs/LAYERS.md`에 반영한다.
- API 변경은 `docs/API-MAP.md`에 반영한다.
- 실행 방식 변경은 `docs/LOCAL_RUNBOOK.md`에 반영한다.
- 성능 관련 변경은 `docs/PERFORMANCE-OPTIMIZATION.md`에 근거와 함께 기록한다.
- 작업 규칙과 subagent 책임은 `AGENT.md`에 반영한다.
