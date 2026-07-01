<template>
  <div class="app-shell">
    <header class="topbar">
      <button class="brand" type="button" @click="go('home')">
        <img src="/images/icon/mainIcon.png" alt="" />
        <span>주점부리</span>
      </button>

      <nav class="nav-actions" aria-label="main navigation">
        <button :class="{ active: route === 'home' }" type="button" @click="go('home')">홈</button>
        <button :class="{ active: route === 'products' }" type="button" @click="go('products')">상품</button>
        <button :class="{ active: route === 'cart' }" type="button" @click="go('cart')">
          장바구니 <span v-if="cartCount">({{ cartCount }})</span>
        </button>
        <button :class="{ active: route === 'account' }" type="button" @click="go('account')">마이</button>
        <button v-if="isAdmin" :class="{ active: route === 'admin' }" type="button" @click="openAdmin">관리</button>
      </nav>

      <div class="user-pill">
        <span v-if="user">{{ user.name }}</span>
        <button v-if="user" type="button" class="btn btn-sm btn-outline-dark" @click="logout">로그아웃</button>
        <button v-else type="button" class="btn btn-sm btn-dark" @click="go('account')">로그인</button>
      </div>
    </header>

    <main>
      <div v-if="status" class="notice success">{{ status }}</div>
      <div v-if="error" class="notice error">{{ error }}</div>

      <section v-if="route === 'home'" class="view">
        <div id="mainCarousel" class="carousel slide hero-carousel" data-bs-ride="carousel">
          <div class="carousel-inner">
            <div v-for="(image, index) in carouselImages" :key="image" class="carousel-item" :class="{ active: index === 0 }">
              <img :src="image" alt="" loading="lazy" decoding="async" />
            </div>
          </div>
          <button class="carousel-control-prev" type="button" data-bs-target="#mainCarousel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          </button>
          <button class="carousel-control-next" type="button" data-bs-target="#mainCarousel" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
          </button>
        </div>

        <section class="section-band">
          <div class="section-heading">
            <h1>오늘의 주점부리</h1>
            <button type="button" class="btn btn-outline-dark btn-sm" @click="go('products')">전체 보기</button>
          </div>
          <div class="category-strip">
            <button v-for="category in categories" :key="category.label" type="button" @click="filterCategory(category.label)">
              <img :src="category.image" :alt="category.label" loading="lazy" decoding="async" />
              <span>{{ category.label }}</span>
            </button>
          </div>
          <div class="product-grid">
            <article v-for="product in popularProducts" :key="product.id" class="product-card">
              <button type="button" class="product-image" @click="openProduct(product)">
                <img :src="productImage(product)" :alt="product.name" loading="lazy" decoding="async" />
              </button>
              <div class="product-body">
                <p class="product-type">{{ product.type }}</p>
                <h2>{{ product.name }}</h2>
                <p>{{ money(product.price) }}원 · {{ product.abv }}%</p>
                <button type="button" class="btn btn-warning btn-sm" @click="addToCart(product)">담기</button>
              </div>
            </article>
          </div>
        </section>

        <section class="section-band">
          <div class="section-heading">
            <h2>매장</h2>
          </div>
          <div class="market-grid">
            <article v-for="market in markets" :key="market.id" class="market-row">
              <strong>{{ market.name }}</strong>
              <span>{{ market.latitude.toFixed(5) }}, {{ market.longitude.toFixed(5) }}</span>
            </article>
          </div>
        </section>
      </section>

      <section v-else-if="route === 'products'" class="view">
        <div class="toolbar">
          <select v-model="filters.type" class="form-select" @change="loadProducts">
            <option value="">전체 주종</option>
            <option v-for="category in categories" :key="category.label" :value="category.label">{{ category.label }}</option>
          </select>
          <input v-model="filters.q" class="form-control" placeholder="상품명 또는 주종 검색" @keyup.enter="loadProducts" />
          <select v-model="filters.sort" class="form-select" @change="loadProducts">
            <option value="popular">인기순</option>
            <option value="priceAsc">낮은 가격순</option>
            <option value="priceDesc">높은 가격순</option>
            <option value="name">이름순</option>
          </select>
          <button type="button" class="btn btn-dark" @click="loadProducts">검색</button>
        </div>
        <div class="product-grid">
          <article v-for="product in products" :key="product.id" class="product-card">
            <button type="button" class="product-image" @click="openProduct(product)">
              <img :src="productImage(product)" :alt="product.name" loading="lazy" decoding="async" />
            </button>
            <div class="product-body">
              <p class="product-type">{{ product.type }}</p>
              <h2>{{ product.name }}</h2>
              <p>{{ money(product.price) }}원 · {{ product.abv }}%</p>
              <div class="button-row">
                <button type="button" class="btn btn-outline-dark btn-sm" @click="openProduct(product)">상세</button>
                <button type="button" class="btn btn-warning btn-sm" @click="addToCart(product)">담기</button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section v-else-if="route === 'detail' && selectedProduct" class="view detail-layout">
        <div class="detail-media">
          <img :src="productImage(selectedProduct)" :alt="selectedProduct.name" loading="lazy" decoding="async" />
        </div>
        <div class="detail-panel">
          <p class="product-type">{{ selectedProduct.type }}</p>
          <h1>{{ selectedProduct.name }}</h1>
          <p>{{ money(selectedProduct.price) }}원 · 도수 {{ selectedProduct.abv }}% · 주문 {{ selectedProduct.orderCount }}회</p>
          <button type="button" class="btn btn-warning" @click="addToCart(selectedProduct)">장바구니 담기</button>

          <div class="comment-box">
            <h2>댓글</h2>
            <form v-if="user" class="comment-form" @submit.prevent="submitComment">
              <select v-model.number="commentForm.rating" class="form-select">
                <option :value="5">5점</option>
                <option :value="4">4점</option>
                <option :value="3">3점</option>
                <option :value="2">2점</option>
                <option :value="1">1점</option>
              </select>
              <input v-model="commentForm.comment" class="form-control" placeholder="댓글" required />
              <button type="submit" class="btn btn-dark">등록</button>
            </form>
            <p v-else class="muted">로그인 후 댓글을 남길 수 있습니다.</p>
            <div v-for="comment in comments" :key="comment.id" class="comment-row">
              <strong>{{ comment.userId }}</strong>
              <span>{{ comment.rating.toFixed(1) }}점</span>
              <p>{{ comment.comment }}</p>
              <button v-if="canEditComment(comment)" type="button" class="btn btn-sm btn-outline-danger" @click="removeComment(comment)">삭제</button>
            </div>
          </div>
        </div>
      </section>

      <section v-else-if="route === 'cart'" class="view">
        <div class="section-heading">
          <h1>장바구니</h1>
          <strong>{{ money(cartTotal) }}원</strong>
        </div>
        <div v-if="cart.length" class="cart-list">
          <article v-for="item in cart" :key="item.product.id" class="cart-row">
            <img :src="productImage(item.product)" :alt="item.product.name" loading="lazy" decoding="async" />
            <div>
              <strong>{{ item.product.name }}</strong>
              <p>{{ money(item.product.price) }}원</p>
            </div>
            <input v-model.number="item.quantity" type="number" min="1" class="form-control quantity-input" @change="persistCart" />
            <button type="button" class="btn btn-outline-danger btn-sm" @click="removeFromCart(item.product.id)">삭제</button>
          </article>
        </div>
        <p v-else class="empty-state">장바구니가 비어 있습니다.</p>
        <form v-if="cart.length" class="checkout-box" @submit.prevent="checkout">
          <select v-model="orderForm.orderType" class="form-select">
            <option value="TAKEOUT">포장</option>
            <option value="DELIVERY">배달</option>
            <option value="OFFLINE">매장</option>
          </select>
          <input v-model="orderForm.orderTable" class="form-control" placeholder="테이블 또는 요청 메모" />
          <button type="submit" class="btn btn-dark">주문하기</button>
        </form>
      </section>

      <section v-else-if="route === 'account'" class="view account-layout">
        <div v-if="!user" class="auth-panel">
          <div class="segmented">
            <button type="button" :class="{ active: authMode === 'login' }" @click="authMode = 'login'">로그인</button>
            <button type="button" :class="{ active: authMode === 'join' }" @click="authMode = 'join'">회원가입</button>
          </div>
          <form v-if="authMode === 'login'" @submit.prevent="login">
            <input v-model="loginForm.id" class="form-control" placeholder="아이디" autocomplete="username" required />
            <input v-model="loginForm.pass" type="password" class="form-control" placeholder="비밀번호" autocomplete="current-password" required />
            <button type="submit" class="btn btn-dark w-100">로그인</button>
          </form>
          <form v-else @submit.prevent="join">
            <input v-model="joinForm.id" class="form-control" placeholder="아이디" required />
            <input v-model="joinForm.name" class="form-control" placeholder="이름" required />
            <input v-model="joinForm.pass" type="password" class="form-control" placeholder="비밀번호" required />
            <button type="submit" class="btn btn-dark w-100">가입</button>
          </form>
        </div>

        <div v-else class="profile-panel">
          <div class="section-heading">
            <div>
              <h1>{{ profile?.user?.name || user.name }}</h1>
              <p>{{ profile?.grade?.title || '회원' }} · 스탬프 {{ profile?.user?.stamps ?? user.stamps }}</p>
            </div>
            <button type="button" class="btn btn-outline-dark" @click="loadProfile">새로고침</button>
          </div>
          <div class="order-list">
            <article v-for="order in profileOrders" :key="order.oId" class="order-row">
              <div>
                <strong>#{{ order.oId }} · {{ order.orderType }}</strong>
                <p>{{ order.completed === 'Y' ? '완료' : '진행중' }} · {{ date(order.orderTime) }}</p>
              </div>
              <div class="order-items">
                <span v-for="detail in order.details" :key="detail.id">{{ detail.name }} x {{ detail.quantity }}</span>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section v-else-if="route === 'admin' && isAdmin" class="view admin-layout">
        <div class="section-heading">
          <h1>관리자</h1>
          <button type="button" class="btn btn-outline-dark btn-sm" @click="loadAdmin">새로고침</button>
        </div>
        <div class="admin-tabs">
          <button v-for="tab in adminTabs" :key="tab.key" type="button" :class="{ active: adminTab === tab.key }" @click="adminTab = tab.key">
            {{ tab.label }}
          </button>
        </div>

        <div v-if="adminTab === 'products'" class="admin-panel">
          <form class="admin-form" @submit.prevent="saveProduct">
            <input v-model="productForm.name" class="form-control" placeholder="상품명" required />
            <input v-model="productForm.type" class="form-control" placeholder="주종" required />
            <input v-model.number="productForm.price" type="number" min="0" class="form-control" placeholder="가격" required />
            <input v-model="productForm.img" class="form-control" placeholder="이미지 파일명" required />
            <input v-model.number="productForm.abv" type="number" step="0.1" min="0" class="form-control" placeholder="도수" required />
            <button type="submit" class="btn btn-dark">{{ productForm.id ? '수정' : '등록' }}</button>
            <button type="button" class="btn btn-outline-secondary" @click="resetProductForm">초기화</button>
          </form>
          <div class="admin-table">
            <div v-for="product in products" :key="product.id" class="table-row">
              <span>{{ product.id }}</span>
              <strong>{{ product.name }}</strong>
              <span>{{ product.type }}</span>
              <span>{{ money(product.price) }}원</span>
              <button type="button" class="btn btn-sm btn-outline-dark" @click="editProduct(product)">편집</button>
              <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteProduct(product.id)">삭제</button>
            </div>
          </div>
        </div>

        <div v-else-if="adminTab === 'markets'" class="admin-panel">
          <form class="admin-form" @submit.prevent="saveMarket">
            <input v-model="marketForm.name" class="form-control" placeholder="매장명" required />
            <input v-model.number="marketForm.latitude" type="number" step="0.000001" class="form-control" placeholder="위도" required />
            <input v-model.number="marketForm.longitude" type="number" step="0.000001" class="form-control" placeholder="경도" required />
            <input v-model="marketForm.image" class="form-control" placeholder="이미지" />
            <button type="submit" class="btn btn-dark">{{ marketForm.id ? '수정' : '등록' }}</button>
            <button type="button" class="btn btn-outline-secondary" @click="resetMarketForm">초기화</button>
          </form>
          <div class="admin-table">
            <div v-for="market in markets" :key="market.id" class="table-row">
              <span>{{ market.id }}</span>
              <strong>{{ market.name }}</strong>
              <span>{{ market.latitude.toFixed(5) }}, {{ market.longitude.toFixed(5) }}</span>
              <button type="button" class="btn btn-sm btn-outline-dark" @click="editMarket(market)">편집</button>
              <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteMarket(market.id)">삭제</button>
            </div>
          </div>
        </div>

        <div v-else-if="adminTab === 'orders'" class="admin-panel">
          <div class="admin-table">
            <div v-for="order in adminOrders" :key="order.oId" class="table-row wide">
              <span>#{{ order.oId }}</span>
              <strong>{{ order.userId }}</strong>
              <span>{{ order.orderType }}</span>
              <span>{{ order.completed === 'Y' ? '완료' : '진행중' }}</span>
              <button type="button" class="btn btn-sm btn-outline-dark" @click="completeOrder(order.oId)">완료</button>
              <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteOrder(order.oId)">삭제</button>
            </div>
          </div>
        </div>

        <div v-else class="admin-panel">
          <div class="admin-table">
            <div v-for="comment in adminComments" :key="comment.id" class="table-row wide">
              <span>{{ comment.id }}</span>
              <strong>{{ comment.userId }}</strong>
              <span>상품 {{ comment.productId }}</span>
              <span>{{ comment.rating.toFixed(1) }}점</span>
              <p>{{ comment.comment }}</p>
              <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteAdminComment(comment.id)">삭제</button>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { api } from './services/api'

const carouselImages = [
  '/images/carousel/banner1.png',
  '/images/carousel/banner2.png',
  '/images/carousel/banner3.png',
  '/images/carousel/banner4.png',
  '/images/carousel/banner5.png',
  '/images/carousel/banner6.png',
]

const categories = [
  { image: '/images/category/탁주.png', label: '탁주' },
  { image: '/images/category/청주.png', label: '약 · 청주' },
  { image: '/images/category/과실주.png', label: '과실주' },
  { image: '/images/category/증류주.png', label: '증류주' },
  { image: '/images/category/맥주.png', label: '맥주' },
  { image: '/images/category/양주.png', label: '양주' },
  { image: '/images/category/위스키.png', label: '위스키' },
  { image: '/images/category/사케.png', label: '사케' },
]

const adminTabs = [
  { key: 'products', label: '상품' },
  { key: 'markets', label: '매장' },
  { key: 'orders', label: '주문' },
  { key: 'comments', label: '댓글' },
]

const route = ref(window.location.hash.replace('#', '') || 'home')
const user = ref(null)
const status = ref('')
const error = ref('')
const products = ref([])
const popularProducts = ref([])
const markets = ref([])
const comments = ref([])
const adminOrders = ref([])
const adminComments = ref([])
const adminTab = ref('products')
const selectedProduct = ref(null)
const profile = ref(null)
const authMode = ref('login')

const filters = reactive({ type: '', q: '', sort: 'popular' })
const loginForm = reactive({ id: '', pass: '' })
const joinForm = reactive({ id: '', name: '', pass: '' })
const orderForm = reactive({ orderType: 'TAKEOUT', orderTable: '' })
const commentForm = reactive({ rating: 5, comment: '' })
const productForm = reactive({ id: null, name: '', type: '', price: 0, img: '', abv: 0, orderCount: 0 })
const marketForm = reactive({ id: null, name: '', latitude: 0, longitude: 0, image: '' })
const cart = ref(JSON.parse(localStorage.getItem('pubburi-cart') || '[]'))

const isAdmin = computed(() => user.value?.role === 'ADMIN')
const cartCount = computed(() => cart.value.reduce((sum, item) => sum + item.quantity, 0))
const cartTotal = computed(() => cart.value.reduce((sum, item) => sum + item.product.price * item.quantity, 0))
const profileOrders = computed(() => profile.value?.orders || [])

window.addEventListener('hashchange', () => {
  route.value = window.location.hash.replace('#', '') || 'home'
})

watch(route, async (value) => {
  if (value === 'products') {
    await loadProducts()
  }
  if (value === 'account' && user.value) {
    await loadProfile()
  }
  if (value === 'admin' && isAdmin.value) {
    await loadAdmin()
  }
})

onMounted(async () => {
  await refreshMe()
  await Promise.all([loadProducts(), loadPopularProducts(), loadMarkets()])
})

function go(value) {
  window.location.hash = value
  route.value = value
  clearMessages()
}

function setStatus(message) {
  status.value = message
  error.value = ''
}

function setError(err) {
  error.value = err?.message || String(err)
  status.value = ''
}

function clearMessages() {
  status.value = ''
  error.value = ''
}

async function run(action, successMessage) {
  try {
    clearMessages()
    const result = await action()
    if (successMessage) {
      setStatus(successMessage)
    }
    return result
  } catch (err) {
    setError(err)
    return null
  }
}

async function refreshMe() {
  user.value = await api.me().catch(() => null)
}

async function loadProducts() {
  products.value = await api.products(filters).catch((err) => {
    setError(err)
    return []
  })
}

async function loadPopularProducts() {
  popularProducts.value = await api.popularProducts().catch(() => [])
}

async function loadMarkets() {
  markets.value = await api.markets().catch(() => [])
}

async function openProduct(product) {
  selectedProduct.value = product
  comments.value = await api.comments(product.id).catch(() => [])
  go('detail')
}

function filterCategory(type) {
  filters.type = type
  filters.q = ''
  go('products')
}

function addToCart(product) {
  const existing = cart.value.find((item) => item.product.id === product.id)
  if (existing) {
    existing.quantity += 1
  } else {
    cart.value.push({ product, quantity: 1 })
  }
  persistCart()
  setStatus('장바구니에 담았습니다.')
}

function removeFromCart(productId) {
  cart.value = cart.value.filter((item) => item.product.id !== productId)
  persistCart()
}

function persistCart() {
  cart.value = cart.value.filter((item) => item.quantity > 0)
  localStorage.setItem('pubburi-cart', JSON.stringify(cart.value))
}

async function checkout() {
  if (!user.value) {
    go('account')
    setError(new Error('로그인이 필요합니다.'))
    return
  }
  await run(async () => {
    await api.createOrder({
      userId: user.value.id,
      orderType: orderForm.orderType,
      orderTable: orderForm.orderTable,
      details: cart.value.map((item) => ({ productId: item.product.id, quantity: item.quantity })),
    })
    cart.value = []
    persistCart()
    await Promise.all([loadProducts(), loadPopularProducts(), loadProfile()])
  }, '주문이 접수되었습니다.')
}

async function login() {
  await run(async () => {
    user.value = await api.login(loginForm)
    loginForm.pass = ''
    await loadProfile()
    go('account')
  }, '로그인되었습니다.')
}

async function join() {
  await run(async () => {
    await api.join(joinForm)
    loginForm.id = joinForm.id
    loginForm.pass = joinForm.pass
    joinForm.id = ''
    joinForm.name = ''
    joinForm.pass = ''
    authMode.value = 'login'
    await login()
  }, '가입되었습니다.')
}

async function logout() {
  await run(async () => {
    await api.logout()
    user.value = null
    profile.value = null
    go('home')
  }, '로그아웃되었습니다.')
}

async function loadProfile() {
  if (!user.value) return
  profile.value = await api.profile(user.value.id).catch((err) => {
    setError(err)
    return null
  })
}

async function submitComment() {
  if (!selectedProduct.value) return
  await run(async () => {
    await api.createComment({
      userId: user.value.id,
      productId: selectedProduct.value.id,
      rating: commentForm.rating,
      comment: commentForm.comment,
    })
    commentForm.comment = ''
    comments.value = await api.comments(selectedProduct.value.id)
  }, '댓글이 등록되었습니다.')
}

function canEditComment(comment) {
  return isAdmin.value || user.value?.id === comment.userId
}

async function removeComment(comment) {
  await run(async () => {
    await api.deleteComment(comment.id)
    comments.value = comments.value.filter((item) => item.id !== comment.id)
  }, '댓글이 삭제되었습니다.')
}

async function openAdmin() {
  go('admin')
  await loadAdmin()
}

async function loadAdmin() {
  await Promise.all([
    loadProducts(),
    loadMarkets(),
    api.adminOrders().then((data) => (adminOrders.value = data)).catch((err) => setError(err)),
    api.adminComments().then((data) => (adminComments.value = data)).catch((err) => setError(err)),
  ])
}

function editProduct(product) {
  Object.assign(productForm, { ...product })
}

function resetProductForm() {
  Object.assign(productForm, { id: null, name: '', type: '', price: 0, img: '', abv: 0, orderCount: 0 })
}

async function saveProduct() {
  await run(async () => {
    if (productForm.id) {
      await api.updateProduct(productForm.id, productForm)
    } else {
      await api.createProduct(productForm)
    }
    resetProductForm()
    await Promise.all([loadProducts(), loadPopularProducts()])
  }, '상품이 저장되었습니다.')
}

async function deleteProduct(id) {
  await run(async () => {
    await api.deleteProduct(id)
    await Promise.all([loadProducts(), loadPopularProducts()])
  }, '상품이 삭제되었습니다.')
}

function editMarket(market) {
  Object.assign(marketForm, { ...market })
}

function resetMarketForm() {
  Object.assign(marketForm, { id: null, name: '', latitude: 0, longitude: 0, image: '' })
}

async function saveMarket() {
  await run(async () => {
    if (marketForm.id) {
      await api.updateMarket(marketForm.id, marketForm)
    } else {
      await api.createMarket(marketForm)
    }
    resetMarketForm()
    await loadMarkets()
  }, '매장이 저장되었습니다.')
}

async function deleteMarket(id) {
  await run(async () => {
    await api.deleteMarket(id)
    await loadMarkets()
  }, '매장이 삭제되었습니다.')
}

async function completeOrder(id) {
  await run(async () => {
    await api.completeOrder(id)
    adminOrders.value = await api.adminOrders()
  }, '주문을 완료 처리했습니다.')
}

async function deleteOrder(id) {
  await run(async () => {
    await api.deleteOrder(id)
    adminOrders.value = await api.adminOrders()
  }, '주문이 삭제되었습니다.')
}

async function deleteAdminComment(id) {
  await run(async () => {
    await api.deleteComment(id)
    adminComments.value = await api.adminComments()
  }, '댓글이 삭제되었습니다.')
}

function productImage(product) {
  if (!product?.img) return '/images/icon/mainIcon.png'
  return product.img.startsWith('/') ? product.img : `/images/pub/${product.img}`
}

function money(value) {
  return Number(value || 0).toLocaleString('ko-KR')
}

function date(value) {
  return value ? new Date(value).toLocaleString('ko-KR') : ''
}
</script>
