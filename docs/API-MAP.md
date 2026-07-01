# API Map

모든 backend API는 `/api` prefix를 사용한다. Frontend는 `pubburi-vue/src/services/api.js`를 통해 이 endpoint에 연결한다.

## Auth And Users

- `POST /api/auth/login`: 로그인
- `POST /api/auth/logout`: 로그아웃
- `GET /api/auth/me`: 현재 session 사용자 조회
- `POST /api/users`: 회원가입
- `GET /api/users/id-available`: 아이디 중복 확인
- `GET /api/users/{id}/profile`: 사용자 프로필과 주문 요약
- `GET /api/admin/users`: 관리자 사용자 목록
- `PUT /api/admin/users/{id}`: 관리자 사용자 수정
- `DELETE /api/admin/users/{id}`: 관리자 사용자 삭제

## Products

- `GET /api/products`: 상품 목록, 검색, 카테고리, 정렬
- `GET /api/products/popular`: 인기 상품
- `GET /api/products/{id}`: 상품 상세
- `POST /api/admin/products`: 관리자 상품 등록
- `PUT /api/admin/products/{id}`: 관리자 상품 수정
- `DELETE /api/admin/products/{id}`: 관리자 상품 삭제

## Comments

- `GET /api/products/{productId}/comments`: 상품 댓글 목록
- `POST /api/comments`: 댓글 등록
- `PUT /api/comments/{id}`: 댓글 수정
- `DELETE /api/comments/{id}`: 댓글 삭제
- `GET /api/admin/comments`: 관리자 댓글 목록

## Orders

- `POST /api/orders`: 주문 생성
- `GET /api/users/{userId}/orders`: 사용자 주문 목록
- `GET /api/orders/{id}`: 주문 상세
- `GET /api/admin/orders`: 관리자 주문 목록
- `PUT /api/admin/orders/{id}/complete`: 주문 완료 처리
- `DELETE /api/admin/orders/{id}`: 주문 삭제

## Markets

- `GET /api/markets`: 매장 목록
- `GET /api/markets/{id}`: 매장 상세
- `POST /api/admin/markets`: 관리자 매장 등록
- `PUT /api/admin/markets/{id}`: 관리자 매장 수정
- `DELETE /api/admin/markets/{id}`: 관리자 매장 삭제

## Frontend 연결 화면

- 고객: 홈, 상품 목록, 상품 상세, 댓글, 장바구니, 주문, 로그인, 회원가입, 마이페이지, 매장
- 관리자: 상품 CRUD, 매장 CRUD, 주문 관리, 댓글 관리, 사용자 관리
