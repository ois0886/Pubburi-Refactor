# Layer Architecture

주전부리(Pubburi)는 로컬 실행을 기준으로 frontend, backend, data, local runtime layer를 분리한다.

## 1. Client Layer

- 위치: `pubburi-vue/src/views`, `pubburi-vue/src/components`, `pubburi-vue/src/router`, `pubburi-vue/src/style.css`
- 역할: 고객 화면, 관리자 화면, 장바구니, 주문, 댓글, 마이페이지 UI를 제공한다.
- 원칙: 화면은 route/view 단위로 나누고, 재사용 UI는 components에 둔다.

## 2. Frontend State Layer

- 위치: `pubburi-vue/src/stores`
- 역할: 인증, 장바구니, 카탈로그, 관리자 데이터, UI 알림 상태를 관리한다.
- 원칙: 장바구니처럼 로컬 지속성이 필요한 상태는 store action에서 한 곳으로 처리한다.

## 3. API Client Layer

- 위치: `pubburi-vue/src/services/api.js`
- 역할: `/api` endpoint 호출, `{ data, message, error }` unwrap, page query 생성을 관리한다.
- 원칙: `credentials: include`를 유지하고, 화면은 raw fetch를 직접 호출하지 않는다.

## 4. Backend API Layer

- 위치: `Server/src/main/java/com/pubburi/pub/controller/rest`, `Server/src/main/java/com/pubburi/pub/controller/request`, `Server/src/main/java/com/pubburi/pub/controller/response`, `Server/src/main/java/com/pubburi/pub/controller/api`
- 역할: HTTP 요청, request validation, session 사용자 확인, 요청/응답 변환을 담당한다.
- 주요 controller: user, product, order, comment, market.
- 원칙: 모든 endpoint는 `/api` prefix 아래에 두고 `ApiResponse`로 응답한다.

## 5. Auth Layer

- 위치: `Server/src/main/java/com/pubburi/pub/controller/auth`
- 역할: session 사용자 모델과 권한 확인을 담당한다.
- 원칙: session에는 비밀번호 없는 `SessionUser`만 저장한다.

## 6. Service Layer

- 위치: `Server/src/main/java/com/pubburi/pub/model/service`
- 역할: 주문 생성, 회원 처리, 댓글 권한, 스탬프 적립, 관리자 작업의 핵심 규칙을 처리한다.
- 원칙: 여러 테이블을 함께 바꾸는 작업은 service에서 트랜잭션 경계를 갖고, 비밀번호 저장은 `PasswordHasher`를 사용한다.

## 7. Persistence Layer

- 위치: `Server/src/main/java/com/pubburi/pub/model/dao`, `Server/src/main/resources/mappers`
- 역할: MyBatis mapper를 통해 SQL과 Java DTO를 연결한다.
- 원칙: 목록 조회는 필요한 컬럼, 정렬, 필터, count, limit/offset을 SQL에서 처리한다.

## 8. Data Layer

- 위치: `Server/data.sql`
- 역할: 로컬 개발용 schema, seed data, index를 제공한다.
- 원칙: 실제 로컬 DB 식별값과 개인 위치성 seed는 SQL에 남기지 않는다.

## 9. Local Runtime Layer

- 위치: `docker-compose.yml`, `.env.example`, `scripts/dev-backend.sh`, `scripts/dev-frontend.sh`, `scripts/check.sh`
- 역할: 로컬 MySQL과 Spring Boot 실행에 필요한 환경변수 연결을 제공한다.
- 원칙: `.env.example`에는 빈 항목만 두고 실제 값은 `.env`에만 둔다.

## 10. Documentation And Git Layer

- 위치: `README.md`, `docs/*.md`, `AGENT.md`
- 역할: 리팩토링 의사결정, 검증 방법, layer 책임, subagent 역할, 성능 기록을 남긴다.
- 원칙: 변경 후 문서를 갱신하고 Conventional Commits 형식으로 커밋한다.
