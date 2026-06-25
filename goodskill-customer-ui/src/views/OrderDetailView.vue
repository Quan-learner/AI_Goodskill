<template>
  <div class="min-h-screen bg-[var(--bg-primary)] transition-colors duration-300">
    <!-- 确认弹窗 -->
    <ConfirmDialog
      :visible="showConfirmDialog"
      title="取消订单"
      message="确定要取消这个订单吗？"
      confirm-text="确定取消"
      cancel-text="再想想"
      @confirm="handleConfirmCancelOrder"
      @cancel="showConfirmDialog = false"
    />

    <!-- 导航栏 -->
    <nav class="fixed top-0 left-0 right-0 z-50 glass-card border-b border-[var(--border-color)]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-secondary flex items-center justify-center shadow-glow">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold gradient-text font-display">秒杀商城</span>
            </router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">首页</router-link>
            <router-link to="/orders" class="text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium bg-black/5 dark:bg-white/10">我的订单</router-link>
            <!-- 主题切换 -->
            <button @click="toggleTheme" class="theme-toggle" title="切换主题">
              <svg class="sun-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <svg class="moon-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
            </button>
            <button @click="handleLogout" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">退出登录</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 订单详情 -->
    <div class="pt-24 pb-12">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <div class="flex items-center gap-2 text-sm text-[var(--text-muted)] mb-8">
          <router-link to="/" class="hover:text-[var(--text-primary)] transition-colors">首页</router-link>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <router-link to="/orders" class="hover:text-[var(--text-primary)] transition-colors">我的订单</router-link>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <span class="text-[var(--text-secondary)]">订单详情</span>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="glass-card rounded-3xl p-8">
          <div class="space-y-6">
            <div class="h-8 shimmer rounded w-1/3"></div>
            <div class="h-4 shimmer rounded w-1/4"></div>
            <div class="border-t border-white/5 pt-6">
              <div class="flex gap-6">
                <div class="w-32 h-32 rounded-2xl shimmer flex-shrink-0"></div>
                <div class="flex-1 space-y-3">
                  <div class="h-6 shimmer rounded w-2/3"></div>
                  <div class="h-4 shimmer rounded w-1/2"></div>
                  <div class="h-8 shimmer rounded w-32"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-danger/10 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <p class="text-[var(--text-muted)] text-lg">{{ error }}</p>
          <button @click="fetchOrderDetail" class="mt-4 btn-primary px-6 py-3 rounded-xl">重新加载</button>
        </div>

        <!-- Order Detail Content -->
        <div v-else-if="currentOrder" class="space-y-6">
          <!-- Order Status Card -->
          <div class="glass-card rounded-3xl p-8">
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
              <div>
                <h1 class="text-2xl font-bold text-[var(--text-primary)] font-display mb-2">订单详情</h1>
                <div class="flex items-center gap-2 text-[var(--text-muted)]">
                  <span>订单号:</span>
                  <span class="font-mono text-[var(--text-primary)]">{{ currentOrder.id }}</span>
                  <button
                    @click="copyOrderId"
                    class="ml-2 p-1.5 rounded-lg hover:bg-black/10 dark:hover:bg-white/10 transition-colors"
                    title="复制订单号"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                    </svg>
                  </button>
                </div>
              </div>
              <div :class="[
                'flex items-center gap-2 px-5 py-2.5 rounded-full text-sm font-medium',
                getStatusStyle(currentOrder.state)
              ]">
                <span class="w-2 h-2 rounded-full" :class="getStatusDotColor(currentOrder.state)"></span>
                {{ currentOrder.stateDesc || '待支付' }}
              </div>
            </div>

            <!-- Progress Steps -->
            <div class="relative">
              <div class="absolute top-5 left-0 right-0 h-0.5 bg-[var(--border-color)]"></div>
              <div class="relative flex justify-between">
                <div
                  v-for="(step, index) in orderSteps"
                  :key="index"
                  class="flex flex-col items-center"
                  :class="[
                    step.completed ? 'text-primary' : 'text-[var(--text-muted)]'
                  ]"
                >
                  <div
                    class="w-10 h-10 rounded-full flex items-center justify-center mb-3 transition-all"
                    :class="[
                      step.completed
                        ? 'bg-gradient-to-br from-primary to-secondary shadow-glow'
                        : 'bg-[var(--bg-secondary)] border border-[var(--border-color)]'
                    ]"
                  >
                    <svg v-if="step.completed" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    <span v-else class="text-sm font-medium">{{ index + 1 }}</span>
                  </div>
                  <span class="text-sm font-medium">{{ step.label }}</span>
                  <span v-if="step.time" class="text-xs text-[var(--text-muted)] mt-1">{{ step.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Left Column: Product Info -->
            <div class="lg:col-span-2 space-y-6">
              <!-- Product Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                  商品信息
                </h3>
                <div class="flex gap-6">
                  <div class="w-32 h-32 rounded-2xl overflow-hidden bg-[var(--bg-secondary)] flex-shrink-0">
                    <img
                      :src="currentOrder.goodsImg || 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=200&h=200&fit=crop'"
                      :alt="currentOrder.goodsName"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <div class="flex-1 min-w-0">
                    <h4 class="text-lg font-semibold text-[var(--text-primary)] mb-2">{{ currentOrder.goodsName || '商品名称' }}</h4>
                    <p class="text-[var(--text-muted)] text-sm mb-4">{{ currentOrder.goodsTitle || '商品描述' }}</p>
                    <div class="flex items-center gap-4">
                      <span class="text-2xl font-bold text-[var(--text-primary)] font-display">¥{{ currentOrder.seckillPrice || 0 }}</span>
                      <span class="text-[var(--text-muted)] line-through">¥{{ (currentOrder.seckillPrice || 0) * 1.2 }}</span>
                      <span class="badge badge-danger">-20%</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Order Info -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  订单信息
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <div class="space-y-4">
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">订单编号</span>
                      <span class="text-[var(--text-primary)] font-mono">{{ currentOrder.id }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">创建时间</span>
                      <span class="text-[var(--text-primary)]">{{ formatTime(currentOrder.createTime) }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">用户手机</span>
                      <span class="text-[var(--text-primary)]">{{ maskPhone(currentOrder.userPhone) }}</span>
                    </div>
                  </div>
                  <div class="space-y-4">
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">支付方式</span>
                      <span class="text-[var(--text-primary)]">{{ currentOrder.alipayTradeNo ? '支付宝' : (Number(currentOrder.status) >= 2 || Number(currentOrder.state) >= 2 ? '余额支付' : '待支付') }}</span>
                    </div>
                    <div v-if="currentOrder.alipayTradeNo" class="flex justify-between">
                      <span class="text-[var(--text-muted)]">支付流水</span>
                      <span class="text-[var(--text-primary)] font-mono text-sm">{{ currentOrder.alipayTradeNo }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">订单状态</span>
                      <span :class="getStatusTextColor(currentOrder.state)">{{ currentOrder.stateDesc || '待支付' }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 物流跟踪卡片 -->
              <div v-if="shippingTracks.length > 0" id="logistics" class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h1M5 17a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
                  </svg>
                  物流信息 (顺丰速运)
                </h3>
                
                <div class="space-y-6 relative before:absolute before:inset-y-2 before:left-[15px] before:w-[2px] before:bg-[var(--border-color)]">
                  <div v-for="(track, idx) in shippingTracks" :key="idx" class="flex gap-4 relative">
                    <!-- 图标节点 -->
                    <div class="w-8 h-8 rounded-full flex items-center justify-center bg-[var(--bg-secondary)] border border-[var(--border-color)] shrink-0 z-10 text-xs sm:text-sm" :class="idx === 0 ? 'border-primary bg-primary/10 shadow-glow text-primary font-bold' : ''">
                      {{ track.icon }}
                    </div>
                    <!-- 内容 -->
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center justify-between gap-2 mb-1">
                        <span class="text-sm font-semibold" :class="idx === 0 ? 'text-primary' : 'text-[var(--text-primary)]'">{{ track.status }}</span>
                        <span class="text-xs text-[var(--text-muted)] font-mono">{{ track.time }}</span>
                      </div>
                      <p class="text-xs leading-relaxed" :class="idx === 0 ? 'text-[var(--text-secondary)] font-medium' : 'text-[var(--text-muted)]'">{{ track.desc }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column: Payment Summary -->
            <div class="space-y-6">
              <!-- Payment Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                  支付信息
                </h3>
                <div class="space-y-4 mb-6">
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">商品金额</span>
                    <span class="text-[var(--text-primary)]">¥{{ (currentOrder.seckillPrice || 0) * 1.2 }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">优惠金额</span>
                    <span class="text-success">-¥{{ ((currentOrder.seckillPrice || 0) * 0.2).toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">运费</span>
                    <span class="text-[var(--text-primary)]">¥0.00</span>
                  </div>
                  <div class="border-t border-[var(--border-color)] pt-4">
                    <div class="flex justify-between items-center">
                      <span class="text-[var(--text-primary)]">实付金额</span>
                      <span class="text-3xl font-bold text-[var(--text-primary)] font-display">¥{{ currentOrder.seckillPrice || 0 }}</span>
                    </div>
                  </div>

                  <!-- 选择支付方式 -->
                  <div v-if="currentOrder.state === 1" class="border-t border-[var(--border-color)] pt-4 mt-4">
                    <label class="block text-xs font-semibold text-[var(--text-muted)] uppercase tracking-wider mb-3">选择支付方式</label>
                    <div class="space-y-2.5">
                      <!-- 余额支付 -->
                      <label 
                        class="flex items-center justify-between p-3.5 rounded-2xl border border-[var(--border-color)] cursor-pointer hover:bg-black/5 dark:hover:bg-white/5 transition-all" 
                        :class="payMethod === 'balance' ? 'border-primary bg-primary/5' : ''"
                      >
                        <div class="flex items-center gap-3">
                          <input type="radio" v-model="payMethod" value="balance" class="w-4 h-4 text-primary border-[var(--border-color)] focus:ring-primary focus:ring-offset-0 bg-[var(--bg-secondary)]" />
                          <div class="flex flex-col">
                            <span class="text-sm font-semibold text-[var(--text-primary)] flex items-center gap-1.5">
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                              </svg>
                              余额支付
                            </span>
                            <span class="text-xs text-[var(--text-muted)] mt-0.5">账户余额: ¥{{ Number(user.balance).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }} 元</span>
                          </div>
                        </div>
                      </label>
                      <!-- 支付宝支付 -->
                      <label 
                        class="flex items-center justify-between p-3.5 rounded-2xl border border-[var(--border-color)] cursor-pointer hover:bg-black/5 dark:hover:bg-white/5 transition-all" 
                        :class="payMethod === 'alipay' ? 'border-primary bg-primary/5' : ''"
                      >
                        <div class="flex items-center gap-3">
                          <input type="radio" v-model="payMethod" value="alipay" class="w-4 h-4 text-primary border-[var(--border-color)] focus:ring-primary focus:ring-offset-0 bg-[var(--bg-secondary)]" />
                          <div class="flex flex-col">
                            <span class="text-sm font-semibold text-[var(--text-primary)] flex items-center gap-1.5">
                              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-info" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0 3.517-1.009 6.799-2.753 9.571m-3.44-2.04l.054-.09A13.916 13.916 0 009 11V7a4 4 0 00-8 0v4c0 3.823.847 7.45 2.385 10.727m6.21-1.01A13.92 13.92 0 0012 11V7a4 4 0 00-8 0" />
                              </svg>
                              支付宝支付
                            </span>
                            <span class="text-xs text-[var(--text-muted)] mt-0.5">跳转模拟沙箱支付</span>
                          </div>
                        </div>
                      </label>
                    </div>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="space-y-3">
                  <button
                    v-if="currentOrder.state === 1"
                    @click="handlePay"
                    :disabled="isPaying"
                    class="w-full py-4 rounded-xl bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] font-semibold hover:shadow-glow transition-all flex items-center justify-center gap-2 disabled:opacity-50"
                  >
                    <svg v-if="isPaying" class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    {{ isPaying ? '支付中...' : '立即支付' }}
                  </button>
                  <button
                    v-if="currentOrder.state === 1"
                    @click="handleCancelOrder"
                    class="w-full py-3 rounded-xl bg-danger/20 text-danger border border-danger/30 font-medium hover:bg-danger/30 transition-all"
                  >
                    取消订单
                  </button>
                  <button
                    v-if="Number(currentOrder.status) === 2 || currentOrder.state === 2 || currentOrder.state === 3"
                    @click="handleConfirmReceipt"
                    class="w-full py-4 rounded-xl bg-success/20 text-success border border-success/30 font-semibold hover:bg-success/30 transition-all flex items-center justify-center gap-2"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    确认收货
                  </button>

                  <button
                    v-if="currentOrder.state === 4"
                    disabled
                    class="w-full py-4 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] border border-[var(--border-color)] font-semibold flex items-center justify-center gap-2 cursor-not-allowed opacity-60"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    交易已完成
                  </button>
                  <router-link
                    to="/orders"
                    class="w-full py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all flex items-center justify-center gap-2"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    返回订单列表
                  </router-link>
                </div>
              </div>

              <!-- Help Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-4">需要帮助?</h3>
                <div class="space-y-3">
                  <a href="#" class="flex items-center gap-3 text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">
                    <div class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <span>常见问题</span>
                  </a>
                  <a href="#" class="flex items-center gap-3 text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">
                    <div class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
                      </svg>
                    </div>
                    <span>联系客服</span>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-[var(--bg-secondary)] flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <p class="text-[var(--text-muted)] text-lg">订单不存在</p>
          <router-link to="/orders" class="mt-4 inline-block btn-primary px-6 py-3 rounded-xl">返回订单列表</router-link>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <Transition name="fade">
      <div v-if="showToast" class="fixed bottom-8 left-1/2 -translate-x-1/2 z-50">
        <div class="glass-card px-6 py-3 rounded-full flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span class="text-[var(--text-primary)]">{{ toastMessage }}</span>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '../api'
import { useUserStore, useOrderStore, useThemeStore } from '../stores'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import { formatProductImage } from '../utils/image'

const router = useRouter()
const route = useRoute()
const { user, logout, deductBalance } = useUserStore()
const { currentOrder, loading, error, setCurrentOrder, setLoading, setError } = useOrderStore()
const { toggleTheme, initTheme } = useThemeStore()

const orderId = computed(() => route.params.id)
const isPaying = ref(false)
const payMethod = ref('balance')
const showToast = ref(false)
const toastMessage = ref('')
const showConfirmDialog = ref(false)

const orderSteps = computed(() => {
  const status = Number(currentOrder.value?.status) || 1
  const state = Number(currentOrder.value?.state) || status
  // 已取消订单显示特殊流程
  if (status === 3) {
    return [
      { label: '提交订单', completed: true, time: formatTime(currentOrder.value?.createTime) },
      { label: '订单取消', completed: true, time: formatTime(currentOrder.value?.updateTime) },
    ]
  }
  return [
    { label: '提交订单', completed: true, time: formatTime(currentOrder.value?.createTime) },
    { label: '支付订单', completed: state >= 2 || status >= 2, time: (state >= 2 || status >= 2) ? formatTime(currentOrder.value?.payCompleteTime) : '' },
    { label: '商家发货', completed: state >= 3, time: '' },
    { label: '确认收货', completed: state >= 4 || status >= 4, time: '' },
  ]
})

const shippingTracks = computed(() => {
  const order = currentOrder.value
  if (!order || Number(order.status) === 1 || Number(order.status) === 3) return []

  const list = []
  const baseTime = new Date(order.payCompleteTime || order.createTime || Date.now())

  // 辅助函数：格式化时间戳相对位移
  const offsetTimeStr = (offsetMs) => {
    return formatTime(baseTime.getTime() + offsetMs)
  }

  // 计算当前付款后过了多少毫秒
  const elapsed = Date.now() - baseTime.getTime()

  // 1. 已付款（始终存在，只要订单状态是已支付、已发货或已完成）
  const payTrack = {
    time: offsetTimeStr(0),
    status: '已付款',
    desc: '订单支付成功，商家正在紧急备货中。',
    icon: '💳'
  }

  // 2. 已出库（5秒后显示）
  const outTrack = {
    time: offsetTimeStr(5000),
    status: '已出库',
    desc: '您的包裹已打包出库，等待顺丰速运揽收。',
    icon: '🏬'
  }

  // 3. 揽收中（10秒后显示）
  const collectTrack = {
    time: offsetTimeStr(10000),
    status: '揽收中',
    desc: '顺丰速运已在【上海青浦仓】完成揽收，快件准备发往分拨中心。',
    icon: '📥'
  }

  // 4. 已送达（15秒后显示）
  const deliverTrack = {
    time: offsetTimeStr(15000),
    status: '已送达',
    desc: '您的包裹已送达目的地址并签收。感谢您使用顺丰速运！',
    icon: '✅'
  }

  // 如果是大状态已完成 (status === 4) 或者时间已经过了 15 秒，展示所有
  if (Number(order.status) === 4 || elapsed >= 15000) {
    list.push(deliverTrack)
    list.push(collectTrack)
    list.push(outTrack)
    list.push(payTrack)
  } else {
    // 否则根据时间差决定展示哪些
    if (elapsed >= 10000) {
      list.push(collectTrack)
      list.push(outTrack)
      list.push(payTrack)
    } else if (elapsed >= 5000) {
      list.push(outTrack)
      list.push(payTrack)
    } else {
      list.push(payTrack)
    }
  }

  return list
})

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const maskPhone = (phone) => {
  if (!phone) return '未知'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const getStatusStyle = (state) => {
  const status = Number(currentOrder.value?.status)
  if (Number(state) === 3 && status !== 3) {
    return 'bg-primary/10 text-primary border border-primary/30'
  }
  const styles = {
    1: 'bg-warning/10 text-warning border border-warning/30',
    2: 'bg-success/10 text-success border border-success/30',
    3: 'bg-danger/10 text-danger border border-danger/30',
    4: 'bg-primary/10 text-primary border border-primary/30',
  }
  return styles[state] || 'bg-black/5 dark:bg-white/5 text-[var(--text-muted)] border border-[var(--border-color)]'
}

const getStatusDotColor = (state) => {
  const status = Number(currentOrder.value?.status)
  if (Number(state) === 3 && status !== 3) {
    return 'bg-primary shadow-[0_0_8px_#6366f1]'
  }
  const colors = {
    1: 'bg-warning shadow-[0_0_8px_#f59e0b]',
    2: 'bg-success shadow-[0_0_8px_#10b981]',
    3: 'bg-danger shadow-[0_0_8px_#ef4444]',
    4: 'bg-primary shadow-[0_0_8px_#6366f1]',
  }
  return colors[state] || 'bg-[var(--text-muted)]'
}

const getStatusTextColor = (state) => {
  const status = Number(currentOrder.value?.status)
  if (Number(state) === 3 && status !== 3) {
    return 'text-primary'
  }
  const colors = {
    1: 'text-warning',
    2: 'text-success',
    3: 'text-danger',
    4: 'text-primary',
  }
  return colors[state] || 'text-[var(--text-muted)]'
}

const copyOrderId = () => {
  if (currentOrder.value?.id) {
    navigator.clipboard.writeText(currentOrder.value.id)
    showToastMessage('订单号已复制')
  }
}

const showToastMessage = (message) => {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const fetchOrderDetail = async () => {
  setLoading(true)
  setError(null)
  try {
    // 拦截本地模拟订单
    if (String(orderId.value).startsWith('mock_')) {
      const mockOrders = JSON.parse(localStorage.getItem('mock_orders')) || []
      const found = mockOrders.find(o => String(o.id) === String(orderId.value))
      if (found) {
        // 格式化一下
        const formatted = {
          ...found,
          goodsImg: found.goodsImg
        }
        setCurrentOrder(formatted)
        setLoading(false)
        return
      }
    }

    const response = await api.getOrderDetail(orderId.value)
    console.log('订单详情响应:', response)
    if (response.code === 0 || response.code === 200) {
      const orderData = {
        ...response.data,
        state: Number(response.data.status) || Number(response.data.state) || 1,
        stateDesc: response.data.stateDesc || '待支付',
        seckillPrice: response.data.seckillPrice || 0,
        goodsName: response.data.goodsName || '商品名称',
        goodsTitle: response.data.goodsTitle || '商品描述',
        goodsImg: formatProductImage(response.data.goodsImg, response.data.goodsName)
      }
      console.log('处理后orderData:', orderData)
      setCurrentOrder(orderData)
    } else {
      setError(response.message || response.msg || '获取订单详情失败')
    }
  } catch (err) {
    setError('获取订单详情失败，请稍后重试')
    console.error('获取订单详情失败:', err)
  } finally {
    setLoading(false)
  }
}

const handlePay = async () => {
  if (isPaying.value) return

  isPaying.value = true
  try {
    const orderPrice = parseFloat(currentOrder.value.seckillPrice || 0)

    // 模拟订单仅支持余额支付
    if (String(currentOrder.value.id).startsWith('mock_') && payMethod.value !== 'balance') {
      showToastMessage('模拟订单仅支持余额支付')
      payMethod.value = 'balance'
      isPaying.value = false
      return
    }

    if (payMethod.value === 'balance') {
      const currentBalance = parseFloat(user.balance || 0)
      if (currentBalance < orderPrice) {
        showToastMessage('账户余额不足')
        isPaying.value = false
        return
      }

      console.log('开始余额支付，订单ID:', currentOrder.value.id)
      let response
      if (String(currentOrder.value.id).startsWith('mock_')) {
        response = { code: 200, data: true, message: '余额支付成功' }
        // 更新本地存储状态
        const mockOrders = JSON.parse(localStorage.getItem('mock_orders')) || []
        const found = mockOrders.find(o => String(o.id) === String(currentOrder.value.id))
        if (found) {
          found.status = 2 // 2: 已支付
          found.state = 2
          found.stateDesc = '已付款'
          found.payCompleteTime = new Date().getTime() // 记录支付时间以渲染物流
          localStorage.setItem('mock_orders', JSON.stringify(mockOrders))
        }
      } else {
        response = await api.payWithBalance({
          orderId: currentOrder.value.id.toString()
        })
      }
      
      console.log('余额支付响应:', response)
      if (response.code === 0 || response.code === 200) {
        deductBalance(orderPrice)
        showToastMessage('余额支付成功！')
        await fetchOrderDetail()
      } else {
        showToastMessage(response.message || response.msg || '余额支付失败')
      }
      isPaying.value = false
      return
    }

    // 支付宝支付逻辑
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)

    console.log('开始创建支付宝支付订单，参数:', {
      orderId: currentOrder.value.id,
      amount: orderPrice.toString(),
      subject: currentOrder.value.goodsName || '商品名称',
      payType: isMobile ? 'h5' : 'pc'
    })

    const response = await api.createPayOrder({
      orderId: currentOrder.value.id,
      amount: orderPrice.toString(),
      subject: currentOrder.value.goodsName || '商品名称',
      payType: isMobile ? 'h5' : 'pc'
    })

    console.log('创建支付订单响应:', response)

    if (response.code === 0 || response.code === 200) {
      console.log('支付表单:', response.data.form)

      const payWindow = window.open('', '_blank', 'width=800,height=600')
      if (payWindow) {
        payWindow.document.write(response.data.form)
        payWindow.document.close()
      } else {
        console.error('浏览器阻止了弹出窗口，请允许弹出窗口后重试')
        showToastMessage('请允许弹出窗口后重试')
      }

      pollPaymentStatus()
    } else {
      console.error('创建支付订单失败:', response.message || response.msg)
      showToastMessage('创建支付订单失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    showToastMessage('支付失败，请稍后重试')
  } finally {
    isPaying.value = false
  }
}

const handleConfirmReceipt = async () => {
  try {
    console.log('开始确认收货，订单ID:', currentOrder.value.id)
    let response
    if (String(currentOrder.value.id).startsWith('mock_')) {
      response = { code: 200, data: true, message: '确认收货成功' }
      const mockOrders = JSON.parse(localStorage.getItem('mock_orders')) || []
      const found = mockOrders.find(o => String(o.id) === String(currentOrder.value.id))
      if (found) {
        found.status = 4 // 4: 已完成
        found.state = 4
        found.stateDesc = '交易完成'
        localStorage.setItem('mock_orders', JSON.stringify(mockOrders))
      }
    } else {
      response = await api.confirmReceipt(currentOrder.value.id.toString())
    }

    if (response.code === 0 || response.code === 200) {
      showToastMessage('确认收货成功！')
      await fetchOrderDetail()
    } else {
      showToastMessage(response.message || response.msg || '确认收货失败')
    }
  } catch (err) {
    console.error('确认收货失败:', err)
    showToastMessage('确认收货失败，请稍后重试')
  }
}

let pollInterval = null

const pollPaymentStatus = async () => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }

  pollInterval = setInterval(async () => {
    try {
      const response = await api.queryPayStatus(currentOrder.value.id)
      if (response.code === 0) {
        if (response.data.status === 'SUCCESS') {
          clearInterval(pollInterval)
          pollInterval = null
          showToastMessage('支付成功！')
          fetchOrderDetail()
        } else if (response.data.status === 'FAILED' || response.data.status === 'CLOSED') {
          clearInterval(pollInterval)
          pollInterval = null
          showToastMessage('支付失败或已关闭')
        }
      }
    } catch (error) {
      console.error('查询支付状态失败:', error)
    }
  }, 3000)
}

const checkAndUpdateMockOrdersLogistics = () => {
  const mockOrdersStr = localStorage.getItem('mock_orders')
  if (!mockOrdersStr) return false
  try {
    const mockOrders = JSON.parse(mockOrdersStr)
    let changed = false
    mockOrders.forEach(o => {
      const currentStatus = Number(o.status)
      if (currentStatus === 2 && o.payCompleteTime) {
        const elapsed = Date.now() - o.payCompleteTime
        const currentStep = Number(o.logisticsStep) || 1
        
        if (elapsed >= 15000 && currentStep < 4) {
          o.stateDesc = '已送达'
          o.logisticsStep = 4
          changed = true
        } else if (elapsed >= 10000 && elapsed < 15000 && currentStep < 3) {
          o.logisticsStep = 3
          changed = true
        } else if (elapsed >= 5000 && elapsed < 10000 && currentStep < 2) {
          o.state = 3
          o.stateDesc = '商家发货'
          o.logisticsStep = 2
          changed = true
        }
      }
    })
    if (changed) {
      localStorage.setItem('mock_orders', JSON.stringify(mockOrders))
      return true
    }
  } catch (e) {
    console.error('更新模拟物流状态失败:', e)
  }
  return false
}

let logisticsTimer = null

onMounted(async () => {
  initTheme()
  checkAndUpdateMockOrdersLogistics()
  await fetchOrderDetail()
  
  logisticsTimer = setInterval(() => {
    const isUpdated = checkAndUpdateMockOrdersLogistics()
    if (isUpdated && String(orderId.value).startsWith('mock_')) {
      fetchOrderDetail()
    }
  }, 1000)

  if (route.hash === '#logistics') {
    nextTick(() => {
      setTimeout(() => {
        const el = document.getElementById('logistics')
        if (el) {
          el.scrollIntoView({ behavior: 'smooth', block: 'center' })
        }
      }, 300)
    })
  }
})

const handleCancelOrder = () => {
  showConfirmDialog.value = true
}

const handleConfirmCancelOrder = async () => {
  if (!currentOrder.value) return

  try {
    let response
    if (String(currentOrder.value.id).startsWith('mock_')) {
      response = { code: 200, data: true, message: '取消订单成功' }
      const mockOrders = JSON.parse(localStorage.getItem('mock_orders')) || []
      const found = mockOrders.find(o => String(o.id) === String(currentOrder.value.id))
      if (found) {
        found.status = 3 // 3: 已取消
        found.state = 3
        found.stateDesc = '已取消'
        localStorage.setItem('mock_orders', JSON.stringify(mockOrders))
      }
    } else {
      response = await api.cancelOrder(currentOrder.value.id)
    }

    if (response.code === 0 || response.code === 200) {
      showToastMessage('取消订单成功')
      // 重新获取订单详情
      fetchOrderDetail()
    } else {
      showToastMessage(response.message || response.msg || '取消订单失败')
    }
  } catch (err) {
    showToastMessage('取消订单失败，请稍后重试')
    console.error('取消订单失败:', err)
  } finally {
    showConfirmDialog.value = false
  }
}

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
    pollInterval = null
  }
  if (logisticsTimer) {
    clearInterval(logisticsTimer)
    logisticsTimer = null
  }
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
