# Layer Architecture

주전부리(Pubburi)는 로컬 실행을 기준으로 frontend, backend, data, local runtime layer를 분리한다.

## 1. Client Layer

- 위치: `pubburi-vue/src/App.vue`, `pubburi-vue/src/style.css`
- 역할: 고객 화면, 관리자 화면, 장바구니, 주문, 댓글, 마이페이지 UI를 제공한다.
- 원칙: 화면은 API 응답을 표시하고, 영속 규칙은 backend service layer에 둔다.

## 2. API Client Layer

- 위치: `pubburi-vue/src/services/api.js`
- 역할: `/api` endpoint 호출을 한 곳에서 관리한다.
- 원칙: `credentials: include`를 유지해 session 기반 로그인 상태와 일관되게 동작한다.

## 3. Backend API Layer

- 위치: `Server/src/main/java/com/pubburi/pub/controller/rest`
- 역할: HTTP 요청, session 사용자 확인, 요청/응답 변환을 담당한다.
- 주요 controller: user, product, order, comment, market.
- 원칙: 모든 endpoint는 `/api` prefix 아래에 둔다.

## 4. Service Layer

- 위치: `Server/src/main/java/com/pubburi/pub/model/service`
- 역할: 주문 생성, 회원 처리, 댓글 권한, 스탬프 적립, 관리자 작업의 핵심 규칙을 처리한다.
- 원칙: 여러 테이블을 함께 바꾸는 작업은 service에서 트랜잭션 경계를 갖는다.

## 5. Persistence Layer

- 위치: `Server/src/main/java/com/pubburi/pub/model/dao`, `Server/src/main/resources/mappers`
- 역할: MyBatis mapper를 통해 SQL과 Java DTO를 연결한다.
- 원칙: 목록 조회는 필요한 정렬과 필터를 SQL에서 처리하고, controller에서 임의 가공하지 않는다.

## 6. Data Layer

- 위치: `Server/data.sql`
- 역할: 로컬 개발용 schema, seed data, index를 제공한다.
- 원칙: 실제 DB 이름, 실제 계정, 개인 위치성 seed는 SQL에 남기지 않는다.

## 7. Local Runtime Layer

- 위치: `docker-compose.yml`, `.env.example`
- 역할: 로컬 MySQL과 Spring Boot 실행에 필요한 환경변수 연결을 제공한다.
- 원칙: `.env.example`에는 빈 항목만 두고 실제 값은 `.env`에만 둔다.

## 8. Documentation And Git Layer

- 위치: `README.md`, `docs/*.md`, `AGENT.md`
- 역할: 리팩토링 의사결정, 검증 방법, layer 책임, subagent 역할, 성능 기록을 남긴다.
- 원칙: 변경 후 문서를 갱신하고 Conventional Commits 형식으로 커밋한다.
