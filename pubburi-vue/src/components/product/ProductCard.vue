<template>
  <article class="product-card">
    <button type="button" class="product-image" @click="$emit('open', product)">
      <ImageWithFallback :src="productImage(product)" :alt="product.name" />
    </button>
    <div class="product-body">
      <p class="product-type">{{ product.type }}</p>
      <h2>{{ product.name }}</h2>
      <p>{{ money(product.price) }}원 · {{ product.abv }}%</p>
      <div class="button-row">
        <button v-if="showDetail" type="button" class="btn btn-outline-dark btn-sm" @click="$emit('open', product)">상세</button>
        <button type="button" class="btn btn-warning btn-sm" @click="$emit('add', product)">담기</button>
      </div>
    </div>
  </article>
</template>

<script setup>
import ImageWithFallback from '../ui/ImageWithFallback.vue'
import { money, productImage } from '../../utils/format'

defineProps({
  product: {
    type: Object,
    required: true,
  },
  showDetail: {
    type: Boolean,
    default: true,
  },
})

defineEmits(['open', 'add'])
</script>
