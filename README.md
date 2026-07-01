# SSAFY 13기 구미캠퍼스 5반 관통 프로젝트 Final

## 주전부리 Pubburi

주전부리(Pubburi)는 주류 상품 조회, 장바구니, 주문, 댓글, 마이페이지, 관리자 CRUD를 제공하는 웹 애플리케이션입니다. 현재 저장소는 Spring Boot와 Vue 기반으로 리팩토링 중이며 로컬 실행을 기준으로 관리합니다.

## 팀원

- 오인성
- 박상윤

## 기술 스택

- Backend: Spring Boot 3, MyBatis, MySQL
- Frontend: Vue 3, Vite, Bootstrap 5
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
