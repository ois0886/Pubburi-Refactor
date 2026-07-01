<template>
  <section class="view">
    <div class="section-heading">
      <div>
        <p class="section-kicker">Cart</p>
        <h1>장바구니</h1>
      </div>
      <strong class="total-price">{{ money(cart.total) }}원</strong>
    </div>
    <div v-if="cart.items.length" class="cart-list">
      <article v-for="item in cart.items" :key="item.product.id" class="cart-row">
        <ImageWithFallback :src="productImage(item.product)" :alt="item.product.name" />
        <div>
          <strong>{{ item.product.name }}</strong>
          <p class="muted">{{ money(item.product.price) }}원</p>
        </div>
        <input v-model.number="item.quantity" type="number" min="1" class="form-control quantity-input" @change="cart.persist" />
        <button type="button" class="btn btn-outline-danger btn-sm" @click="cart.remove(item.product.id)">삭제</button>
      </article>
    </div>
    <p v-else class="empty-state">장바구니가 비어 있습니다.</p>
    <form v-if="cart.items.length" class="checkout-box" @submit.prevent="checkout">
      <select v-model="cart.orderForm.orderType" class="form-select">
        <option value="TAKEOUT">포장</option>
        <option value="ONLINE">온라인</option>
        <option value="OFFLINE">매장</option>
      </select>
      <input v-model="cart.orderForm.orderTable" class="form-control" placeholder="테이블 또는 요청 메모" />
      <button type="submit" class="btn btn-dark">주문하기</button>
    </form>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'
import ImageWithFallback from '../components/ui/ImageWithFallback.vue'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { useCatalogStore } from '../stores/catalog'
import { useUiStore } from '../stores/ui'
import { money, productImage } from '../utils/format'

const auth = useAuthStore()
const cart = useCartStore()
const catalog = useCatalogStore()
const ui = useUiStore()
const router = useRouter()

async function checkout() {
  if (!auth.user) {
    ui.setError(new Error('로그인이 필요합니다.'))
    await router.push('/account')
    return
  }
  await ui.run(async () => {
    await cart.checkout(auth.user.id)
    await Promise.all([catalog.loadProducts(), catalog.loadPopularProducts(), auth.loadProfile()])
  }, '주문이 접수되었습니다.')
}
</script>
