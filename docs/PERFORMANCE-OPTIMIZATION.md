# Performance Optimization

주전부리(Pubburi) 리팩토링 중 수행한 성능 관련 변경과 확인 결과를 기록한다.

## 2026-07-01 Optimization Pass

### Backend

- BCrypt 기반 `PasswordHasher`를 적용하고 legacy SHA-256 검증/자동 전환 로직을 추가했다.
- request DTO와 Bean Validation을 적용해 controller 진입점에서 잘못된 입력을 빠르게 차단한다.
- 모든 API 응답을 `ApiResponse`로 통일해 frontend error handling 분기를 줄였다.
- 상품, 댓글, 주문, 매장, 관리자 사용자 목록에 `page/size` pagination과 count query를 추가했다.
- session에는 `SessionUser`만 저장해 내부 비밀번호 필드가 view/API 응답으로 새는 위험을 줄였다.

### Frontend

- Vue Router로 화면을 route 단위로 나누고, Pinia store로 인증/장바구니/카탈로그/관리자 상태를 분리했다.
- API client가 공통 응답 wrapper를 unwrap하고 page query를 생성해 화면별 중복 fetch 코드를 줄였다.
- `ImageWithFallback` 컴포넌트는 WebP source와 원본 PNG fallback을 함께 사용한다.
- `App.vue`는 shell 중심으로 축소하고 실제 화면은 `views`와 `components`로 분리했다.

### Image Assets

- `scripts/optimize-images.mjs`로 PNG/JPG 96개를 WebP로 생성했다.
- 대상 원본 합계: 60,003,601 bytes.
- 생성 WebP 합계: 2,924,062 bytes.
- 산술상 약 95% 감소했고, 원본 PNG는 fallback으로 유지한다.

### Verification Snapshot

- Backend: `./mvnw test` 통과, 5 tests.
- Frontend: `npm run test` 통과, 6 tests.
- Frontend build: `npm run build` 통과.
- Build output:
  - CSS: 236.63 KB, gzip 32.65 KB.
  - JS: 207.00 KB, gzip 69.71 KB.
  - build time: 약 1초.

## 문서 갱신 체크리스트

- API page size, query, response wrapper가 바뀌면 `docs/API-MAP.md`와 이 문서를 같이 갱신한다.
- 이미지 최적화 기준이나 산출 용량이 바뀌면 이 문서의 Image Assets 섹션을 갱신한다.
- 테스트 수나 build size가 크게 바뀌면 Verification Snapshot을 갱신한다.

## 다음 최적화 후보

- 관리자 화면의 탭별 데이터 로딩을 현재 선택 탭 중심으로 더 줄인다.
- 상품 목록 쿼리에 검색어 빈도와 실제 사용 패턴을 기준으로 추가 index를 검토한다.
- 이미지 품질을 화면별로 비교해 WebP quality와 resize width를 세분화한다.
