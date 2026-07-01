<template>
  <section class="view">
    <div id="mainCarousel" class="carousel slide hero-carousel" data-bs-ride="carousel">
      <div class="carousel-inner">
        <div v-for="(image, index) in carouselImages" :key="image" class="carousel-item" :class="{ active: index === 0 }">
          <ImageWithFallback :src="image" alt="" />
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
        <RouterLink class="btn btn-outline-dark btn-sm" to="/products">전체 보기</RouterLink>
      </div>
      <div class="category-strip">
        <button v-for="category in categories" :key="category.label" type="button" @click="filterCategory(category.label)">
          <ImageWithFallback :src="category.image" :alt="category.label" />
          <span>{{ category.label }}</span>
        </button>
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
        <h2>매장</h2>
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
import { onMounted } from 'vue'
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

onMounted(() => {
  ui.run(async () => {
    await Promise.all([catalog.loadPopularProducts(), catalog.loadMarkets()])
  })
})

function filterCategory(type) {
  catalog.filters = { ...catalog.filters, type, q: '', page: 1 }
  router.push('/products')
}

function openProduct(product) {
  router.push(`/products/${product.id}`)
}

function addToCart(product) {
  cart.add(product)
  ui.setStatus('장바구니에 담았습니다.')
}
</script>
