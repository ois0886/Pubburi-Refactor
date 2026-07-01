<template>
  <div class="admin-panel">
    <div class="admin-table">
      <div v-for="comment in admin.comments" :key="comment.id" class="table-row wide">
        <span>{{ comment.id }}</span>
        <strong>{{ comment.userId }}</strong>
        <span>상품 {{ comment.productId }}</span>
        <span>{{ Number(comment.rating).toFixed(1) }}점</span>
        <p>{{ comment.comment }}</p>
        <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteComment(comment.id)">삭제</button>
      </div>
    </div>
    <PaginationBar :page="admin.commentsPage" @change="loadPage" />
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

function deleteComment(id) {
  return ui.run(() => admin.deleteComment(id), '댓글이 삭제되었습니다.')
}
</script>
