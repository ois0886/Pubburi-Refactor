# SSAFY 13기 구미캠퍼스 5반 관통 프로젝트 Final

## 주전부리 Pubburi

주전부리(Pubburi)는 주류 상품 조회, 장바구니, 주문, 댓글, 마이페이지, 관리자 CRUD를 제공하는 웹 애플리케이션입니다.

## 팀원

- 오인성
- 박상윤

## 기술 스택

- Backend: Spring Boot 3, MyBatis, MySQL
- Frontend: Vue 3, Vite, Pinia, Vue Router
- Local DB: Docker Compose MySQL

## 로컬 실행

```bash
cp .env.example .env
docker compose up -d db
cd Server
set -a
source ../.env
set +a
./mvnw spring-boot:run
```

```bash
cd pubburi-vue
npm ci
npm run dev
```

- Web: `http://localhost:5173`
- API: `http://localhost:9987/api`
- Swagger UI: `http://localhost:9987/docs`

## 검증

```bash
cd Server
./mvnw test
cd ../pubburi-vue
npm ci
npm run build
```

현재 기준 검증 결과는 backend 9 tests, frontend 10 tests, Vite build 통과입니다.

## 문서

- `docs/LOCAL_RUNBOOK.md`: 로컬 실행과 검증 절차
- `docs/API-MAP.md`: `/api` endpoint와 응답 계약
- `docs/LAYERS.md`: frontend/backend/data/runtime layer 책임
- `docs/REFACTORING.md`: 리팩토링 변경 기록
- `docs/PERFORMANCE-OPTIMIZATION.md`: 성능 최적화 근거와 최신 build size
- `AGENT.md`: 작업 규칙과 유지보수 가이드
