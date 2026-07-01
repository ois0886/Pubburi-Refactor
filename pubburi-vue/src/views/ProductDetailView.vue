<template>
  <section v-if="catalog.selectedProduct" class="view detail-layout">
    <div class="detail-media">
      <ImageWithFallback :src="productImage(catalog.selectedProduct)" :alt="catalog.selectedProduct.name" />
    </div>
    <div class="detail-panel">
      <p class="product-type">{{ catalog.selectedProduct.type }}</p>
      <h1>{{ catalog.selectedProduct.name }}</h1>
      <p>
        {{ money(catalog.selectedProduct.price) }}원 · 도수 {{ catalog.selectedProduct.abv }}% · 주문
        {{ catalog.selectedProduct.orderCount }}회
      </p>
      <button type="button" class="btn btn-warning" @click="addToCart(catalog.selectedProduct)">장바구니 담기</button>

      <div class="comment-box">
        <h2>댓글</h2>
        <form v-if="auth.user" class="comment-form" @submit.prevent="submitComment">
          <select v-model.number="commentForm.rating" class="form-select">
            <option :value="5">5점</option>
            <option :value="4">4점</option>
            <option :value="3">3점</option>
            <option :value="2">2점</option>
            <option :value="1">1점</option>
          </select>
          <input v-model="commentForm.comment" class="form-control" placeholder="댓글" required />
          <button type="submit" class="btn btn-dark">등록</button>
        </form>
        <p v-else class="muted">로그인 후 댓글을 남길 수 있습니다.</p>
        <div v-for="comment in catalog.comments" :key="comment.id" class="comment-row">
          <strong>{{ comment.userId }}</strong>
          <span>{{ Number(comment.rating).toFixed(1) }}점</span>
          <p>{{ comment.comment }}</p>
          <button v-if="canEditComment(comment)" type="button" class="btn btn-sm btn-outline-danger" @click="removeComment(comment)">
            삭제
          </button>
        </div>
        <PaginationBar :page="catalog.commentsPage" @change="loadComments" />
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import ImageWithFallback from '../components/ui/ImageWithFallback.vue'
import PaginationBar from '../components/ui/PaginationBar.vue'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { useCatalogStore } from '../stores/catalog'
import { useUiStore } from '../stores/ui'
import { money, productImage } from '../utils/format'

const route = useRoute()
const auth = useAuthStore()
const cart = useCartStore()
const catalog = useCatalogStore()
const ui = useUiStore()
const commentForm = reactive({ rating: 5, comment: '' })

onMounted(loadProduct)
watch(() => route.params.id, loadProduct)

async function loadProduct() {
  await ui.run(async () => {
    await catalog.loadProduct(route.params.id)
    await catalog.loadComments(route.params.id)
  })
}

function loadComments(page) {
  return ui.run(() => catalog.loadComments(route.params.id, { page }))
}

function addToCart(product) {
  cart.add(product)
  ui.setStatus('장바구니에 담았습니다.')
}

async function submitComment() {
  await ui.run(async () => {
    await catalog.createComment({
      productId: catalog.selectedProduct.id,
      rating: commentForm.rating,
      comment: commentForm.comment,
    })
    commentForm.comment = ''
  }, '댓글이 등록되었습니다.')
}

function canEditComment(comment) {
  return auth.isAdmin || auth.user?.id === comment.userId
}

function removeComment(comment) {
  return ui.run(() => catalog.deleteComment(comment.id), '댓글이 삭제되었습니다.')
}
</script>
