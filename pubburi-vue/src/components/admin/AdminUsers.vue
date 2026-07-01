<template>
  <div class="admin-panel">
    <div class="admin-table">
      <div v-for="user in admin.users" :key="user.id" class="table-row wide">
        <span>{{ user.id }}</span>
        <strong>{{ user.name }}</strong>
        <span class="badge" :class="{ muted: user.role !== 'ADMIN' }">{{ user.role }}</span>
        <span>스탬프 {{ user.stamps }}</span>
        <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteUser(user.id)">삭제</button>
      </div>
    </div>
    <PaginationBar :page="admin.usersPage" @change="loadPage" />
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
  return ui.run(() => admin.loadUsers({ page }))
}

function deleteUser(id) {
  return ui.run(() => admin.deleteUser(id), '회원이 삭제되었습니다.')
}
</script>
