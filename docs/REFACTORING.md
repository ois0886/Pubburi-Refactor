# Refactor Notes

이 문서는 주전부리(Pubburi) 리팩토링 방향과 현재 반영된 변경 사항을 기록한다.

## 목표

- Spring Boot backend와 Vue frontend를 로컬에서 바로 연결 가능한 구조로 정리한다.
- 외부 AI와 푸시 공급자 기능은 대체 없이 제거한다.
- 모든 API는 `/api` prefix를 사용한다.
- public repo 기준으로 실제 로컬 설정값과 개인성 seed를 제거한다.
- 변경 후 README, layer 문서, 성능 기록, agent guide를 함께 갱신한다.

## Backend 변경

- Spring Boot 3, MyBatis, MySQL 기반 jar 애플리케이션으로 정리했다.
- REST controller를 user, product, order, comment, market 영역으로 나누었다.
- session 기반 로그인과 관리자 권한 체크를 `SessionSupport`로 통일했다.
- 주문 생성은 order, order detail, product order count, stamp 갱신을 하나의 service 흐름으로 처리한다.
- 댓글 수정/삭제는 존재하지 않는 댓글에 대해 404로 응답한다.

## Frontend 변경

- Vue 3 단일 앱에서 고객 화면과 관리자 화면을 함께 제공한다.
- API 호출은 `src/services/api.js`로 모았다.
- 상품, 카테고리, 상세, 댓글, 장바구니, 주문, 로그인, 회원가입, 마이페이지, 매장, 관리자 CRUD를 backend API와 연결했다.
- 빌드 산출물은 Git에 포함하지 않는다.

## Data 변경

- schema와 seed는 `Server/data.sql`에 유지한다.
- DB 이름 생성/사용 구문은 SQL에서 제거하고 로컬 환경변수에 맡긴다.
- 테스트 매장은 개인 위치성 표현을 제거한 익명 seed로 정리했다.
- 조회 성능을 위해 상품 타입, 상품 주문 수, 주문 사용자/시간, 주문 상세, 댓글 상품 index를 추가했다.

## 문서화 규칙

- layer 책임 변경은 `docs/LAYERS.md`에 반영한다.
- API 변경은 `docs/API-MAP.md`에 반영한다.
- 실행 방식 변경은 `docs/LOCAL_RUNBOOK.md`에 반영한다.
- 성능 관련 변경은 `docs/PERFORMANCE-OPTIMIZATION.md`에 근거와 함께 기록한다.
- 작업 규칙과 subagent 책임은 `AGENT.md`에 반영한다.
