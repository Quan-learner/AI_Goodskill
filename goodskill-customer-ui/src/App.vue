<template>
  <router-view />
  <AiAssistant v-if="showAi" />
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from './stores'
import AiAssistant from './components/AiAssistant.vue'

const userStore = useUserStore()
const route = useRoute()

const showAi = computed(() => {
  return route.path !== '/login' && route.path !== '/register'
})

onMounted(() => {
  userStore.checkLoginStatus()
})
</script>

<style scoped>
/* 全局样式 */
</style>