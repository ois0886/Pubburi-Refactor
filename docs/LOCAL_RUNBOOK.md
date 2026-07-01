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
cd Server
set -a
source ../.env
set +a
./mvnw spring-boot:run
```

## Start Frontend

```bash
cd pubburi-vue
npm ci
npm run dev
```

## Stop Local Runtime

```bash
docker compose down
```

로컬 DB 볼륨까지 지워야 할 때만 `docker compose down -v`를 사용한다.

## Validation

```bash
cd Server
./mvnw test
cd ../pubburi-vue
npm ci
npm run build
```
