<template>
  <div class="admin-panel">
    <div class="admin-table">
      <div v-for="order in admin.orders" :key="order.oId" class="table-row wide">
        <span>#{{ order.oId }}</span>
        <strong>{{ order.userId }}</strong>
        <span>{{ order.orderType }}</span>
        <span>{{ order.completed === 'Y' ? '완료' : '진행중' }}</span>
        <button type="button" class="btn btn-sm btn-outline-dark" @click="completeOrder(order.oId)">완료</button>
        <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteOrder(order.oId)">삭제</button>
      </div>
    </div>
    <PaginationBar :page="admin.ordersPage" @change="loadPage" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import PaginationBar from '../ui/PaginationBar.vue'
import { useAdminStore } from '../../stores/admin'
import { useUiStore } from '../../stores/ui'

const admin = useAdminStore()
const ui = useUiStore()

onMounted(() => loadPage(1))

function loadPage(page) {
  return ui.run(() => admin.load({ page }))
}

function completeOrder(id) {
  return ui.run(() => admin.completeOrder(id), '주문을 완료 처리했습니다.')
}

function deleteOrder(id) {
  return ui.run(() => admin.deleteOrder(id), '주문이 삭제되었습니다.')
}
</script>
