<template>
  <section v-if="auth.isAdmin" class="view admin-layout">
    <div class="section-heading">
      <h1>관리자</h1>
      <button type="button" class="btn btn-outline-dark btn-sm" @click="refresh">새로고침</button>
    </div>
    <div class="admin-tabs">
      <button v-for="tab in adminTabs" :key="tab.key" type="button" :class="{ active: admin.tab === tab.key }" @click="admin.tab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <AdminProducts v-if="admin.tab === 'products'" />
    <AdminMarkets v-else-if="admin.tab === 'markets'" />
    <AdminOrders v-else-if="admin.tab === 'orders'" />
    <AdminComments v-else-if="admin.tab === 'comments'" />
    <AdminUsers v-else />
  </section>
  <p v-else class="empty-state">관리자 권한이 필요합니다.</p>
</template>

<script setup>
import AdminComments from '../components/admin/AdminComments.vue'
import AdminMarkets from '../components/admin/AdminMarkets.vue'
import AdminOrders from '../components/admin/AdminOrders.vue'
import AdminProducts from '../components/admin/AdminProducts.vue'
import AdminUsers from '../components/admin/AdminUsers.vue'
import { adminTabs, useAdminStore } from '../stores/admin'
import { useAuthStore } from '../stores/auth'
import { useCatalogStore } from '../stores/catalog'
import { useUiStore } from '../stores/ui'

const admin = useAdminStore()
const auth = useAuthStore()
const catalog = useCatalogStore()
const ui = useUiStore()

function refresh() {
  return ui.run(async () => {
    await Promise.all([admin.load(), catalog.loadProducts({ page: 1, size: 20 }), catalog.loadMarkets()])
  })
}
</script>
