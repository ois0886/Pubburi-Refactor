<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/" @click="ui.clear">
        <ImageWithFallback src="/images/icon/mainIcon.webp" alt="" />
        <span>주점부리</span>
      </RouterLink>

      <nav class="nav-actions" aria-label="main navigation">
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/products">상품</RouterLink>
        <RouterLink to="/cart">장바구니 <span v-if="cart.count">({{ cart.count }})</span></RouterLink>
        <RouterLink :to="auth.user ? '/profile' : '/account'">마이</RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin">관리</RouterLink>
      </nav>

      <div class="user-pill">
        <span v-if="auth.user">{{ auth.user.name }}</span>
        <button v-if="auth.user" type="button" class="btn btn-sm btn-outline-dark" @click="logout">로그아웃</button>
        <RouterLink v-else class="btn btn-sm btn-dark" to="/account">로그인</RouterLink>
      </div>
    </header>

    <main>
      <AlertMessage />
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AlertMessage from '../ui/AlertMessage.vue'
import ImageWithFallback from '../ui/ImageWithFallback.vue'
import { useAuthStore } from '../../stores/auth'
import { useCartStore } from '../../stores/cart'
import { useUiStore } from '../../stores/ui'

const auth = useAuthStore()
const cart = useCartStore()
const ui = useUiStore()
const router = useRouter()

onMounted(() => {
  auth.refreshMe()
})

async function logout() {
  await ui.run(async () => {
    await auth.logout()
    await router.push('/')
  }, '로그아웃되었습니다.')
}
</script>
