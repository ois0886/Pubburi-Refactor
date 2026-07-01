# Local Runbook

이 프로젝트는 배포 대상이 아니라 로컬 실행 기준으로 관리한다. 로컬 값은 `.env`에만 작성하고 Git에는 올리지 않는다.

## Required Environment

`.env.example`을 복사한 뒤 빈 값을 로컬에서 채운다.

- `PUBBURI_DB_NAME`
- `PUBBURI_DB_USER`
- `PUBBURI_DB_PASSWORD`
- `PUBBURI_DB_ROOT_PASSWORD`
- `PUBBURI_DB_PORT`
- `PUBBURI_DB_URL`
- `PUBBURI_SERVER_PORT`
- `PUBBURI_LOG_LEVEL`
- `VITE_API_BASE_URL`

`PUBBURI_DB_URL`은 로컬 MySQL에 접속할 JDBC URL이다. 실제 DB 이름과 계정 정보는 문서나 코드에 기록하지 않는다.

## Start Database

```bash
cp .env.example .env
docker compose up -d db
```

## Start Backend

```bash
scripts/dev-backend.sh
```

backend는 `.env`를 읽어 `PUBBURI_SERVER_PORT` 기본값인 `9987`에 Spring Boot를 실행한다.

## Start Frontend

```bash
cd pubburi-vue
npm ci
cd ..
scripts/dev-frontend.sh
```

frontend는 `127.0.0.1:5173`에서 실행되며, Vite proxy가 `/api` 요청을 `localhost:9987`로 전달한다.

## Stop Local Runtime

```bash
docker compose down
```

로컬 DB 볼륨까지 지워야 할 때만 `docker compose down -v`를 사용한다.

## Validation

```bash
scripts/check.sh
```

개별 확인이 필요하면 `cd Server && ./mvnw test`, `cd pubburi-vue && npm run test && npm run build`를 실행한다.

현재 검증 기준:

- Backend: `./mvnw test`, 9 tests.
- Frontend: `npm run test`, 10 tests.
- Frontend build: `npm run build`.
- 로컬 URL: Web `http://localhost:5173`, API `http://localhost:9987/api`, Swagger UI `http://localhost:9987/docs`.

## Asset Notes

- `pubburi-vue/public/images`는 WebP asset만 배포 대상으로 둔다.
- 대응 WebP가 있는 PNG/JPG 원본은 제거된 상태다.
- 새 이미지를 추가할 때는 `cd pubburi-vue && npm run optimize:images`로 WebP를 생성한 뒤, public 배포 경로가 WebP를 가리키는지 확인한다.
