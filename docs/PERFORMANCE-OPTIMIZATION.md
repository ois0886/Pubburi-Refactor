# Performance Optimization

주전부리(Pubburi) 리팩토링 중 수행한 성능 관련 변경과 확인 결과를 기록한다.

## 2026-07-01 리팩토링 기준

### Dependency Pruning

- Backend에서 사용하지 않는 외부 AI, 푸시, WAR 배포 관련 의존성을 제거했다.
- Frontend에서 사용하지 않는 패키지를 제거하고 Vue, Vite, Bootstrap 중심으로 축소했다.
- `npm ci` 기준 37개 패키지 audit 결과 취약점 0건을 확인했다.

### Database Query And Index

- 상품 목록은 SQL에서 카테고리, 검색어, 정렬을 처리하도록 정리했다.
- 인기 상품은 주문 수 기준 제한 목록으로 조회한다.
- 주문 상세는 주문과 상품 정보를 join해 화면에 필요한 값을 한 번에 가져온다.
- 추가 index:
  - `idx_product_type`
  - `idx_product_order_count`
  - `idx_order_user_time`
  - `idx_order_detail_order`
  - `idx_order_detail_product`
  - `idx_comment_product`

### API Surface

- 모든 endpoint를 `/api` 아래로 통일해 frontend proxy와 호출 경로를 단순화했다.
- API client를 `src/services/api.js`로 통합해 중복 fetch 옵션과 error handling을 줄였다.
- 사용하지 않는 채팅, 추천, 푸시 화면과 호출 흐름을 제거해 불필요한 네트워크 경로를 없앴다.

### Frontend Build

- Vite production build 확인 결과:
  - CSS: 약 236 KB
  - JS: 약 168 KB
  - build time: 약 0.6초
- 장바구니는 localStorage에 저장해 새로고침 후에도 로컬 상태를 유지한다.

### Backend Test

- `./mvnw test` 기준 context 없는 기본 애플리케이션 로딩 테스트를 통과했다.
- MyBatis mapper와 실제 DB 연동 검증은 Docker MySQL을 띄운 수동 smoke test로 확인했다.

## 다음 최적화 후보

- 비밀번호 해시는 운영 수준 알고리즘으로 교체한다.
- 관리자 목록 API에 pagination을 추가한다.
- 상품 이미지는 용량이 큰 배너부터 WebP 또는 리사이즈 자산으로 교체한다.
- 댓글과 주문 목록은 페이지 단위 조회로 확장한다.
- frontend 화면을 route 단위로 나누어 code splitting을 적용한다.
