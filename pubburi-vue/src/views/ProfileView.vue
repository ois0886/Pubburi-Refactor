<template>
  <section class="view">
    <div v-if="auth.user" class="profile-panel">
      <div class="section-heading">
        <div>
          <h1>{{ auth.profile?.user?.name || auth.user.name }}</h1>
          <p>{{ auth.profile?.grade?.title || '회원' }} · 스탬프 {{ auth.profile?.user?.stamps ?? auth.user.stamps }}</p>
        </div>
        <button type="button" class="btn btn-outline-dark" @click="loadProfile">새로고침</button>
      </div>
      <div class="order-list">
        <article v-for="order in auth.profileOrders" :key="order.oId" class="order-row">
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
    <p v-else class="empty-state">로그인이 필요합니다.</p>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'
import { date } from '../utils/format'

const auth = useAuthStore()
const ui = useUiStore()

onMounted(loadProfile)

function loadProfile() {
  if (!auth.user) return null
  return ui.run(() => auth.loadProfile())
}
</script>
