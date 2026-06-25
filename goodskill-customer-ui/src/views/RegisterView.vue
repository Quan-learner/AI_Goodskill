<template>
  <div class="min-h-screen flex items-center justify-center bg-[var(--bg-primary)] transition-colors duration-300 relative overflow-hidden px-4">
    <!-- Background Ambient Glows -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-40 -left-40 w-96 h-96 bg-primary/20 rounded-full blur-[128px] dark:opacity-100 opacity-30"></div>
      <div class="absolute -bottom-40 -right-40 w-96 h-96 bg-secondary/20 rounded-full blur-[128px] dark:opacity-100 opacity-30"></div>
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-accent/10 rounded-full blur-[150px] dark:opacity-100 opacity-20"></div>
    </div>

    <!-- Theme Toggle (Floating Top Right) -->
    <div class="absolute top-6 right-6 z-50">
      <button @click="toggleTheme" class="theme-toggle" title="切换主题">
        <svg class="sun-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
        </svg>
        <svg class="moon-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
        </svg>
      </button>
    </div>

    <!-- Register Container -->
    <div class="glass-card p-8 w-full max-w-md shadow-glow relative z-10 border border-[var(--border-color)]">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-gradient-to-br from-primary to-secondary rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-glow animate-float">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
          </svg>
        </div>
        <h2 class="text-3xl font-bold font-display gradient-text">注册新账户</h2>
        <p class="text-[var(--text-secondary)] mt-2">加入秒杀商城，抢购海量好物</p>
      </div>

      <form @submit.prevent="handleRegister" class="space-y-5">
        <!-- Username Field -->
        <div>
          <label for="username" class="block text-sm font-medium text-[var(--text-secondary)] mb-2">用户名</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <input
              type="text"
              id="username"
              v-model="form.username"
              class="w-full input-dark pr-4 py-3 placeholder-[var(--text-muted)] text-[var(--text-primary)]"
              style="padding-left: 2.75rem;"
              placeholder="请输入用户名"
              required
            />
          </div>
        </div>

        <!-- Password Field -->
        <div>
          <label for="password" class="block text-sm font-medium text-[var(--text-secondary)] mb-2">密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <input
              type="password"
              id="password"
              v-model="form.password"
              class="w-full input-dark pr-4 py-3 placeholder-[var(--text-muted)] text-[var(--text-primary)]"
              style="padding-left: 2.75rem;"
              placeholder="请输入密码 (至少6位)"
              required
            />
          </div>
        </div>

        <!-- Confirm Password Field -->
        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-[var(--text-secondary)] mb-2">确认密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <input
              type="password"
              id="confirmPassword"
              v-model="form.confirmPassword"
              class="w-full input-dark pr-4 py-3 placeholder-[var(--text-muted)] text-[var(--text-primary)]"
              style="padding-left: 2.75rem;"
              placeholder="请再次输入密码"
              required
            />
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="bg-red-500/10 border border-red-500/30 rounded-2xl p-4 text-danger text-sm flex items-start gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span>{{ error }}</span>
        </div>

        <!-- Success Message -->
        <div v-if="success" class="bg-emerald-500/10 border border-emerald-500/30 rounded-2xl p-4 text-success text-sm flex items-start gap-3">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>{{ success }}</span>
        </div>

        <!-- Register Button -->
        <button
          type="submit"
          :disabled="loading || !!success"
          class="w-full btn-primary py-3.5 flex items-center justify-center font-medium disabled:opacity-50 disabled:cursor-not-allowed group"
        >
          <svg v-if="loading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span class="group-hover:translate-x-0.5 transition-transform">{{ loading ? '注册中...' : '立即注册' }}</span>
        </button>
      </form>

      <!-- Navigation to Login -->
      <div class="mt-8 text-center">
        <p class="text-sm text-[var(--text-secondary)]">
          已有账户？
          <router-link to="/login" class="text-primary hover:text-secondary font-semibold transition-colors duration-200 ml-1">
            返回登录
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useThemeStore } from '../stores'

const router = useRouter()
const { toggleTheme, initTheme } = useThemeStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')
const success = ref('')

const handleRegister = async () => {
  error.value = ''
  success.value = ''

  // Validations
  if (form.value.password.length < 6) {
    error.value = '密码长度不能少于6位'
    return
  }

  if (form.value.password !== form.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    const payload = {
      username: form.value.username,
      password: form.value.password
    }
    const response = await api.register(payload)
    if (response.code === 0 || response.code === 200) {
      success.value = '注册成功！正在跳转至登录页面...'
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      error.value = response.message || response.msg || '注册失败'
    }
  } catch (err) {
    error.value = '注册失败，请稍后重试'
    console.error('注册失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  initTheme()
})
</script>

<style scoped>
/* No additional styles needed as we use variables and global styles */
</style>
