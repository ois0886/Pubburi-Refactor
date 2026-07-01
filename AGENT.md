# Pubburi Agent Guide

이 문서는 주전부리(Pubburi) 리팩토링 저장소에서 작업하는 에이전트와 개발자가 지켜야 할 운영 규칙입니다. 프로젝트는 SSAFY 13기 관통 프로젝트 기반이며, 현재 목표는 로컬 실행 가능한 구조로 안정화하는 것입니다.

## 기본 원칙

- 배포 설정은 만들지 않고 로컬 실행 기준으로만 문서화한다.
- 실제 DB 이름, DB 계정, 비밀번호, JDBC URL, 인증 키, 인증서, 로컬 override 파일은 커밋하지 않는다.
- 코드 변경 후 관련 문서를 함께 갱신한다.
- 커밋 메시지는 `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:` 중 작업 성격에 맞는 접두사를 사용한다.
- public repo 기준으로 개인 위치, 개인 계정, 실제 운영 설정처럼 보이는 값은 seed와 문서에서도 제거한다.

## 작업 순서

1. `git status --short --branch`로 시작 상태를 확인한다.
2. 관련 layer와 문서를 먼저 읽고 변경 범위를 정한다.
3. 코드는 기존 Spring Boot, MyBatis, Vue 패턴을 우선한다.
4. 검증은 최소 `./mvnw test`, `npm ci`, `npm run build`, 민감정보 패턴 스캔을 수행한다.
5. 문서를 갱신한다.
6. Conventional Commit 형식으로 커밋하고 푸시한다.
7. 민감정보가 Git 기록에 들어간 경우 새 root commit으로 히스토리를 정리한 뒤 force-with-lease로 푸시한다.

## Subagents

### backend-agent

- 담당: Spring Boot controller, service, DAO, MyBatis mapper, session 기반 인증 흐름.
- 확인: `/api` prefix 유지, controller는 thin하게 유지, service에서 트랜잭션과 핵심 규칙 처리.
- 검증: `cd Server && ./mvnw test`.

### frontend-agent

- 담당: Vue 화면, API client, 장바구니, 관리자 화면, 반응형 UI.
- 확인: `src/services/api.js`를 통해 백엔드와 연결하고, 화면별 상태와 에러 메시지를 정리한다.
- 검증: `cd pubburi-vue && npm ci && npm run build`.

### data-agent

- 담당: `Server/data.sql`, MyBatis query, 인덱스, 로컬 DB 초기화.
- 확인: SQL에는 실제 DB 이름과 개인 위치성 seed를 남기지 않는다.
- 검증: Docker Compose 설정을 로컬 환경변수로 렌더링하고 schema 초기화를 확인한다.

### security-agent

- 담당: `.gitignore`, 민감정보 스캔, 공개 저장소 기준 점검, 히스토리 정리.
- 확인: `.env`, 인증서, key, local override, build artifact, DB dump가 tracked 상태가 아닌지 확인한다.
- 검증: 공급자 키 패턴, private key 패턴, service account 패턴을 검색한다.

### performance-agent

- 담당: DB index, query 범위, dependency pruning, build size, API round-trip 감소 기록.
- 확인: 변경 전후의 근거를 `docs/PERFORMANCE-OPTIMIZATION.md`에 남긴다.
- 검증: backend test 시간, frontend build 결과, 주요 번들 크기 또는 쿼리 개선 근거를 기록한다.

### docs-agent

- 담당: README, layer 문서, runbook, refactor notes, agent guide.
- 확인: 코드와 문서가 서로 다르게 말하지 않도록 변경마다 갱신한다.
- 검증: 문서에 실제 로컬 값이나 민감 값이 들어가지 않았는지 확인한다.
