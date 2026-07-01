<template>
  <div class="admin-panel">
    <form class="admin-form" @submit.prevent="saveProduct">
      <input v-model="form.name" class="form-control" placeholder="상품명" required />
      <input v-model="form.type" class="form-control" placeholder="주종" required />
      <input v-model.number="form.price" type="number" min="1" class="form-control" placeholder="가격" required />
      <input v-model="form.img" class="form-control" placeholder="이미지 파일명" required />
      <input v-model.number="form.abv" type="number" step="0.1" min="0" class="form-control" placeholder="도수" required />
      <button type="submit" class="btn btn-dark">{{ form.id ? '수정' : '등록' }}</button>
      <button type="button" class="btn btn-outline-secondary" @click="resetForm">초기화</button>
    </form>
    <div class="admin-table">
      <div v-for="product in catalog.products" :key="product.id" class="table-row">
        <span>{{ product.id }}</span>
        <strong>{{ product.name }}</strong>
        <span>{{ product.type }}</span>
        <span>{{ money(product.price) }}원</span>
        <button type="button" class="btn btn-sm btn-outline-dark" @click="editProduct(product)">편집</button>
        <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteProduct(product.id)">삭제</button>
      </div>
    </div>
    <PaginationBar :page="catalog.productsPage" @change="loadPage" />
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import PaginationBar from '../ui/PaginationBar.vue'
import { api } from '../../services/api'
import { useCatalogStore } from '../../stores/catalog'
import { useUiStore } from '../../stores/ui'
import { money } from '../../utils/format'

const catalog = useCatalogStore()
const ui = useUiStore()
const form = reactive({ id: null, name: '', type: '', price: 0, img: '', abv: 0, orderCount: 0 })

onMounted(() => loadPage(1))

function loadPage(page) {
  return ui.run(() => catalog.loadProducts({ page, size: 20, type: '', q: '' }))
}

function editProduct(product) {
  Object.assign(form, { ...product })
}

function resetForm() {
  Object.assign(form, { id: null, name: '', type: '', price: 0, img: '', abv: 0, orderCount: 0 })
}

async function saveProduct() {
  await ui.run(async () => {
    if (form.id) {
      await api.updateProduct(form.id, form)
    } else {
      await api.createProduct(form)
    }
    resetForm()
    await Promise.all([catalog.loadProducts({ page: catalog.productsPage.page, size: 20 }), catalog.loadPopularProducts()])
  }, '상품이 저장되었습니다.')
}

async function deleteProduct(id) {
  await ui.run(async () => {
    await api.deleteProduct(id)
    await Promise.all([catalog.loadProducts({ page: catalog.productsPage.page, size: 20 }), catalog.loadPopularProducts()])
  }, '상품이 삭제되었습니다.')
}
</script>
