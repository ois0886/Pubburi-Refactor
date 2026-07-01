<template>
  <section class="view">
    <div class="toolbar">
      <select v-model="catalog.filters.type" class="form-select" @change="loadPage(1)">
        <option value="">전체 주종</option>
        <option v-for="category in categories" :key="category.label" :value="category.label">{{ category.label }}</option>
      </select>
      <input v-model="catalog.filters.q" class="form-control" placeholder="상품명 또는 주종 검색" @keyup.enter="loadPage(1)" />
      <select v-model="catalog.filters.sort" class="form-select" @change="loadPage(1)">
        <option value="popular">인기순</option>
        <option value="priceAsc">낮은 가격순</option>
        <option value="priceDesc">높은 가격순</option>
        <option value="name">이름순</option>
      </select>
      <button type="button" class="btn btn-dark" @click="loadPage(1)">검색</button>
    </div>
    <div class="product-grid">
      <ProductCard v-for="product in catalog.products" :key="product.id" :product="product" @open="openProduct" @add="addToCart" />
    </div>
    <PaginationBar :page="catalog.productsPage" @change="loadPage" />
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '../components/product/ProductCard.vue'
import PaginationBar from '../components/ui/PaginationBar.vue'
import { categories, useCatalogStore } from '../stores/catalog'
import { useCartStore } from '../stores/cart'
import { useUiStore } from '../stores/ui'

const catalog = useCatalogStore()
const cart = useCartStore()
const ui = useUiStore()
const router = useRouter()

onMounted(() => loadPage(catalog.filters.page || 1))

function loadPage(page) {
  return ui.run(() => catalog.loadProducts({ page }))
}

function openProduct(product) {
  router.push(`/products/${product.id}`)
}

function addToCart(product) {
  cart.add(product)
  ui.setStatus('장바구니에 담았습니다.')
}
</script>
