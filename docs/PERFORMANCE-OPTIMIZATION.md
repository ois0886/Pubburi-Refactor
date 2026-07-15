# Performance Optimization

주점부리(Pubburi) 리팩토링 중 수행한 성능 관련 변경과 확인 결과를 기록한다.

## 2026-07-01 Optimization Pass

### Backend

- BCrypt 기반 `PasswordHasher`를 적용하고 legacy SHA-256 검증/자동 전환 로직을 추가했다.
- request DTO와 Bean Validation을 적용해 controller 진입점에서 잘못된 입력을 빠르게 차단한다.
- 모든 API 응답을 `ApiResponse`로 통일해 frontend error handling 분기를 줄였다.
- 상품, 댓글, 주문, 매장, 관리자 사용자 목록에 `page/size` pagination과 count query를 추가했다.
- session에는 `SessionUser`만 저장해 내부 비밀번호 필드가 view/API 응답으로 새는 위험을 줄였다.
- 주문 목록의 상세 항목 조회는 nested select를 제거하고 `order_id IN (...)` batch 조회로 전환했다.
- 주문 생성은 중복 상품 수량을 합산하고 상세 항목을 batch insert한다.
- 회원 스탬프 적립은 `stamps = stamps + quantity` update로 처리해 동시성 손실 가능성을 줄였다.
- 등급 계산은 `GradeService`로 분리하고 controller 책임을 축소했다.
- controller별 `@CrossOrigin` 반복은 전역 CORS 설정으로 통합했다.

### Frontend

- Vue Router로 화면을 route 단위로 나누고, Pinia store로 인증/장바구니/카탈로그/관리자 상태를 분리했다.
- API client가 공통 응답 wrapper를 unwrap하고 page query를 생성해 화면별 중복 fetch 코드를 줄였다.
- `ImageWithFallback` 컴포넌트와 image path helper는 WebP를 기본 이미지로 사용한다.
- `App.vue`는 shell 중심으로 축소하고 실제 화면은 `views`와 `components`로 분리했다.
- 관리자 store는 active tab 기준 lazy load로 변경해 관리자 화면 진입/갱신 요청 수를 줄였다.
- Bootstrap CSS/JS와 package dependency를 제거하고 로컬 CSS primitive로 대체했다.
- Bootstrap carousel은 Vue 상태 기반 carousel로 대체해 JS bundle을 줄였다.
- 여러 주종을 동등하게 다루는 종합 주류 큐레이션 톤으로 홈/상품/상세/장바구니/프로필/관리자 UI를 정리했다.

### Image Assets

- `scripts/optimize-images.mjs`로 PNG/JPG 96개를 WebP로 생성했다.
- 대상 원본 합계: 60,003,601 bytes.
- 생성 WebP 합계: 2,924,062 bytes.
- 대응 WebP가 있는 PNG/JPG 원본 96개를 제거했다.
- `pubburi-vue/public/images`: 약 3.0 MB.
- `pubburi-vue/dist/images`: 약 3.4 MB.
- `pubburi-vue/dist`: 약 3.5 MB.

### Verification Snapshot

- Backend: `./mvnw test` 통과, 9 tests.
- Frontend: `npm run test` 통과, 10 tests.
- Frontend build: `npm run build` 통과.
- Build output:
  - CSS: 8.86 KB, gzip 2.63 KB.
  - JS: 127.53 KB, gzip 45.66 KB.
  - build time: 약 0.7초.

## 2026-07-15 Commerce UX And Safety Pass

### Frontend

- 고객 catalog와 관리자 product/market page state를 분리해 화면 이동 시 검색 조건이 덮어써지는 문제를 제거했다.
- 상품 필터를 route query에서 복원하고 관리자 탭도 active tab만 lazy load하는 구조를 유지했다.
- 작업 key별 pending state로 중복 로그인, 저장, 삭제, 주문 요청을 차단한다.
- loading skeleton, empty/404, confirm dialog, quantity stepper를 로컬 공통 컴포넌트로 구현해 외부 UI dependency를 추가하지 않았다.
- API client가 network/non-JSON/status 오류를 한 경로에서 처리해 화면별 오류 분기를 줄였다.

### Backend

- mutation service에 transaction 경계를 명시하고 field injection을 constructor injection으로 교체했다.
- 주문 상품 update, 상세 batch insert, stamp insert, 회원 stamp 증가의 반영 건수를 검증해 부분 저장을 방지한다.
- 없는 리소스의 수정/삭제와 관리자 자기 계정 변경 규칙을 controller에서 빠르게 차단한다.

### Verification Snapshot

- Backend: `./mvnw test` 통과, 13 tests.
- Frontend: `npm run test` 통과, 20 tests.
- Frontend build: `npm run build` 통과.
- Build output:
  - CSS: 24.03 KB, gzip 5.82 KB.
  - JS: 164.90 KB, gzip 55.82 KB.
  - build time: 약 0.7초.

UI 상태와 접근성 표현 확대로 CSS/JS 크기는 증가했지만 외부 runtime dependency는 추가하지 않았으며, lazy admin fetch와 WebP asset 정책은 그대로 유지한다.

## 문서 갱신 체크리스트

- API page size, query, response wrapper가 바뀌면 `docs/API-MAP.md`와 이 문서를 같이 갱신한다.
- 이미지 최적화 기준이나 산출 용량이 바뀌면 이 문서의 Image Assets 섹션을 갱신한다.
- 테스트 수나 build size가 크게 바뀌면 Verification Snapshot을 갱신한다.

## 다음 최적화 후보

- 상품 목록 쿼리에 검색어 빈도와 실제 사용 패턴을 기준으로 추가 index를 검토한다.
- 이미지 품질을 화면별로 비교해 WebP quality와 resize width를 세분화한다.
- 관리자 화면의 row 수가 커지면 virtual list 또는 서버 필터를 검토한다.
