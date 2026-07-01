<template>
  <div v-if="ui.status" class="notice success">{{ ui.status }}</div>
  <div v-else-if="ui.error" class="notice error">{{ ui.error }}</div>
</template>

<script setup>
import { useUiStore } from '../../stores/ui'

const ui = useUiStore()
</script>
