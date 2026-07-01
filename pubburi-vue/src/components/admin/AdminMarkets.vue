<template>
  <div class="admin-panel">
    <form class="admin-form" @submit.prevent="saveMarket">
      <input v-model="form.name" class="form-control" placeholder="매장명" required />
      <input v-model.number="form.latitude" type="number" step="0.000001" class="form-control" placeholder="위도" required />
      <input v-model.number="form.longitude" type="number" step="0.000001" class="form-control" placeholder="경도" required />
      <input v-model="form.image" class="form-control" placeholder="이미지" />
      <button type="submit" class="btn btn-dark">{{ form.id ? '수정' : '등록' }}</button>
      <button type="button" class="btn btn-outline-secondary" @click="resetForm">초기화</button>
    </form>
    <div class="admin-table">
      <div v-for="market in catalog.markets" :key="market.id" class="table-row">
        <span>{{ market.id }}</span>
        <strong>{{ market.name }}</strong>
        <span>{{ Number(market.latitude).toFixed(5) }}, {{ Number(market.longitude).toFixed(5) }}</span>
        <button type="button" class="btn btn-sm btn-outline-dark" @click="editMarket(market)">편집</button>
        <button type="button" class="btn btn-sm btn-outline-danger" @click="deleteMarket(market.id)">삭제</button>
      </div>
    </div>
    <PaginationBar :page="catalog.marketsPage" @change="loadPage" />
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import PaginationBar from '../ui/PaginationBar.vue'
import { api } from '../../services/api'
import { useCatalogStore } from '../../stores/catalog'
import { useUiStore } from '../../stores/ui'

const catalog = useCatalogStore()
const ui = useUiStore()
const form = reactive({ id: null, name: '', latitude: 0, longitude: 0, image: '' })

onMounted(() => loadPage(1))

function loadPage(page) {
  return ui.run(() => catalog.loadMarkets({ page }))
}

function editMarket(market) {
  Object.assign(form, { ...market })
}

function resetForm() {
  Object.assign(form, { id: null, name: '', latitude: 0, longitude: 0, image: '' })
}

async function saveMarket() {
  await ui.run(async () => {
    if (form.id) {
      await api.updateMarket(form.id, form)
    } else {
      await api.createMarket(form)
    }
    resetForm()
    await catalog.loadMarkets({ page: catalog.marketsPage.page })
  }, '매장이 저장되었습니다.')
}

async function deleteMarket(id) {
  await ui.run(async () => {
    await api.deleteMarket(id)
    await catalog.loadMarkets({ page: catalog.marketsPage.page })
  }, '매장이 삭제되었습니다.')
}
</script>
