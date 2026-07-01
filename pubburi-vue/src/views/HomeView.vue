<template>
  <section class="view">
    <section class="hero-carousel">
      <div v-for="(image, index) in carouselImages" :key="image" class="hero-slide" :class="{ active: index === activeIndex }">
        <ImageWithFallback :src="image" alt="" :loading="index === 0 ? 'eager' : 'lazy'" />
      </div>
      <div class="hero-copy">
        <p>오늘의 전통주 셀렉션</p>
        <h1>향과 계절에 맞춰 고르는 주점부리</h1>
        <RouterLink class="btn btn-light" to="/products">상품 둘러보기</RouterLink>
      </div>
      <button class="carousel-control prev" type="button" aria-label="previous banner" @click="previousSlide">
        <span aria-hidden="true">‹</span>
      </button>
      <button class="carousel-control next" type="button" aria-label="next banner" @click="nextSlide">
        <span aria-hidden="true">›</span>
      </button>
    </section>

    <section class="section-band">
      <div class="section-heading">
        <div>
          <p class="section-kicker">Category</p>
          <h2>취향으로 빠르게 찾기</h2>
        </div>
        <RouterLink class="btn btn-outline-dark btn-sm" to="/products">전체 보기</RouterLink>
      </div>
      <div class="category-strip">
        <button v-for="category in categories" :key="category.label" type="button" @click="filterCategory(category.label)">
          <ImageWithFallback :src="category.image" :alt="category.label" />
          <span>{{ category.label }}</span>
        </button>
      </div>
    </section>

    <section class="section-band">
      <div class="section-heading">
        <div>
          <p class="section-kicker">Popular</p>
          <h2>많이 찾는 술</h2>
        </div>
      </div>
      <div class="product-grid">
        <ProductCard
          v-for="product in catalog.popularProducts"
          :key="product.id"
          :product="product"
          :show-detail="false"
          @open="openProduct"
          @add="addToCart"
        />
      </div>
    </section>

    <section class="section-band">
      <div class="section-heading">
        <div>
          <p class="section-kicker">Stores</p>
          <h2>가까운 매장</h2>
        </div>
      </div>
      <div class="market-grid">
        <article v-for="market in catalog.markets" :key="market.id" class="market-row">
          <strong>{{ market.name }}</strong>
          <span>{{ Number(market.latitude).toFixed(5) }}, {{ Number(market.longitude).toFixed(5) }}</span>
        </article>
      </div>
    </section>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '../components/product/ProductCard.vue'
import ImageWithFallback from '../components/ui/ImageWithFallback.vue'
import { carouselImages, categories, useCatalogStore } from '../stores/catalog'
import { useCartStore } from '../stores/cart'
import { useUiStore } from '../stores/ui'

const catalog = useCatalogStore()
const cart = useCartStore()
const ui = useUiStore()
const router = useRouter()
const activeIndex = ref(0)

onMounted(() => {
  ui.run(async () => {
    await Promise.all([catalog.loadPopularProducts(), catalog.loadMarkets()])
  })
})

function filterCategory(type) {
  catalog.filters = { ...catalog.filters, type, q: '', page: 1 }
  router.push('/products')
}

function previousSlide() {
  activeIndex.value = (activeIndex.value + carouselImages.length - 1) % carouselImages.length
}

function nextSlide() {
  activeIndex.value = (activeIndex.value + 1) % carouselImages.length
}

function openProduct(product) {
  router.push(`/products/${product.id}`)
}

function addToCart(product) {
  cart.add(product)
  ui.setStatus('장바구니에 담았습니다.')
}
</script>
