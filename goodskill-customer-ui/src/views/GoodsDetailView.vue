<template>
  <div class="min-h-screen bg-[var(--bg-primary)] transition-colors duration-300">
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
            <router-link to="/orders" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">我的订单</router-link>
            <!-- 购物车 -->
            <button @click="showCartDrawer = true" class="p-2.5 rounded-xl text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-all relative" title="购物车">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <span v-if="cartStore.totalCount.value > 0" class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-br from-danger to-warning text-white text-[10px] rounded-full flex items-center justify-center font-bold animate-pulse">{{ cartStore.totalCount.value }}</span>
            </button>
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

    <!-- 商品详情 -->
    <div class="pt-24 pb-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <div class="flex items-center gap-2 text-sm text-[var(--text-muted)] mb-8">
          <router-link to="/" class="hover:text-[var(--text-primary)] transition-colors">首页</router-link>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <span class="text-[var(--text-secondary)]">商品详情</span>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="glass-card rounded-3xl p-12">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
            <div class="aspect-square rounded-2xl shimmer"></div>
            <div class="space-y-6">
              <div class="h-8 shimmer rounded w-3/4"></div>
              <div class="h-4 shimmer rounded w-1/2"></div>
              <div class="h-12 shimmer rounded w-1/3"></div>
              <div class="space-y-3">
                <div class="h-4 shimmer rounded"></div>
                <div class="h-4 shimmer rounded"></div>
                <div class="h-4 shimmer rounded w-2/3"></div>
              </div>
              <div class="h-14 shimmer rounded-xl"></div>
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
          <button @click="fetchGoodsDetail" class="mt-4 btn-primary px-6 py-3 rounded-xl">重新加载</button>
        </div>

        <!-- Product Detail -->
        <div v-else-if="goods" class="grid grid-cols-1 lg:grid-cols-2 gap-12">
          <!-- Left: Image Gallery -->
          <div class="space-y-4">
            <div class="glass-card rounded-3xl overflow-hidden aspect-square relative group">
              <img
                :src="goods.goodsImg"
                :alt="goods.goodsName"
                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
              />
              <!-- Discount Badge -->
              <div class="absolute top-6 left-6">
                <span class="badge badge-danger text-sm px-4 py-2">
                  -{{ Math.round((1 - goods.seckillPrice / goods.goodsPrice) * 100) }}%
                </span>
              </div>
              <!-- Stock Badge -->
              <div class="absolute top-6 right-6">
                <span v-if="goods.stockCount <= 5" class="badge badge-warning text-sm px-4 py-2">
                  仅剩 {{ goods.stockCount }} 件
                </span>
                <span v-else class="badge badge-success text-sm px-4 py-2">
                  有货
                </span>
              </div>
            </div>
            <!-- Thumbnail Gallery -->
            <div class="flex gap-3">
              <div
                v-for="i in 4"
                :key="i"
                class="w-20 h-20 rounded-xl overflow-hidden cursor-pointer border-2 transition-all"
                :class="i === 1 ? 'border-primary' : 'border-[var(--border-color)] hover:border-[var(--text-muted)]'"
              >
                <img :src="goods.goodsImg" class="w-full h-full object-cover" />
              </div>
            </div>
          </div>

          <!-- Right: Product Info -->
          <div class="space-y-6">
            <!-- Title -->
            <div>
              <h1 class="text-3xl md:text-4xl font-bold text-[var(--text-primary)] font-display mb-4">{{ goods.goodsName }}</h1>
              <p class="text-[var(--text-secondary)] text-lg">{{ goods.goodsTitle }}</p>
            </div>

            <!-- Rating -->
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-1">
                <svg v-for="i in 5" :key="i" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-warning" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                </svg>
              </div>
              <span class="text-[var(--text-muted)]">4.9 (2,847 评价)</span>
            </div>

            <!-- Price -->
            <div class="glass-card rounded-2xl p-6">
              <div class="flex items-baseline gap-4 mb-4">
                <span class="text-4xl md:text-5xl font-bold text-[var(--text-primary)] font-display">¥{{ goods.seckillPrice }}</span>
                <span class="text-xl text-[var(--text-muted)] line-through">¥{{ goods.goodsPrice }}</span>
                <span class="badge badge-danger">限时特惠</span>
              </div>

              <!-- Countdown Timer -->
              <div class="flex items-center gap-4 text-[var(--text-secondary)]">
                <span class="text-sm">距离结束还剩:</span>
                <div class="flex gap-2">
                  <div class="bg-[var(--bg-secondary)] rounded-lg px-3 py-2 text-center min-w-[48px]">
                    <div class="text-lg font-bold text-[var(--text-primary)] font-display">{{ countdown.hours }}</div>
                  </div>
                  <span class="text-xl self-center">:</span>
                  <div class="bg-[var(--bg-secondary)] rounded-lg px-3 py-2 text-center min-w-[48px]">
                    <div class="text-lg font-bold text-[var(--text-primary)] font-display">{{ countdown.minutes }}</div>
                  </div>
                  <span class="text-xl self-center">:</span>
                  <div class="bg-[var(--bg-secondary)] rounded-lg px-3 py-2 text-center min-w-[48px]">
                    <div class="text-lg font-bold text-[var(--text-primary)] font-display">{{ countdown.seconds }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Stock Info -->
            <div class="flex items-center gap-6">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full" :class="goods.stockCount > 0 ? 'bg-success shadow-[0_0_8px_#10b981]' : 'bg-danger shadow-[0_0_8px_#ef4444]'"></div>
                <span class="text-[var(--text-secondary)]">库存: <span class="text-[var(--text-primary)] font-medium">{{ goods.stockCount }}</span> 件</span>
              </div>
              <div class="flex items-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <span class="text-[var(--text-secondary)]">{{ viewerCount }} 人正在浏览</span>
              </div>
            </div>

            <!-- Delivery & Logistics Info -->
            <div class="glass-card rounded-2xl p-4 bg-black/5 dark:bg-white/5 border border-[var(--border-color)]">
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs sm:text-sm">
                <div class="flex items-center gap-2.5">
                  <span class="text-primary text-lg">🚚</span>
                  <div class="text-left">
                    <span class="text-[var(--text-secondary)]">发货地：</span>
                    <span class="text-[var(--text-primary)] font-semibold">浙江杭州仓</span>
                    <span class="mx-2 text-[var(--text-muted)]">|</span>
                    <span class="text-[var(--text-secondary)]">配送时效：</span>
                    <span class="text-success font-semibold">顺丰免邮，预计次日送达</span>
                  </div>
                </div>
                <div class="flex items-center shrink-0">
                  <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10.5px] font-semibold bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                    物流状况：顺畅
                  </span>
                </div>
              </div>
            </div>

            <!-- Description -->
            <div class="space-y-4">
              <h3 class="text-lg font-semibold text-[var(--text-primary)]">商品描述</h3>
              <p class="text-[var(--text-secondary)] leading-relaxed">{{ goods.goodsDesc }}</p>
            </div>

            <!-- Action Buttons -->
            <div class="flex gap-4 pt-4 items-center">
              <button
                @click="handleSeckill"
                :disabled="seckillLoading || goods.stockCount <= 0 || goods.endTime < Date.now()"
                class="flex-1 h-14 rounded-2xl font-semibold text-base transition-all duration-300 flex items-center justify-center gap-2.5 shadow-lg active:scale-95 cursor-pointer disabled:cursor-not-allowed"
                :class="[
                  goods.stockCount <= 0 || goods.endTime < Date.now()
                    ? 'bg-black/10 dark:bg-white/10 text-[var(--text-muted)] border border-[var(--border-color)]'
                    : 'bg-gradient-to-r from-primary via-indigo-500 to-secondary text-white hover:shadow-[0_0_20px_rgba(99,102,241,0.5)] hover:scale-[1.02]'
                ]"
              >
                <svg v-if="!seckillLoading" xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                <svg v-else class="animate-spin h-6 w-6 shrink-0" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span class="truncate">
                  {{ seckillLoading ? '抢购中...' : (goods.stockCount <= 0 ? '已售罄' : (goods.endTime < Date.now() ? '已结束' : '立即抢购')) }}
                </span>
              </button>

              <!-- 加入购物车按钮 (仅展示图标) -->
              <button
                @click="handleAddToCart"
                :disabled="goods.stockCount <= 0"
                class="w-14 h-14 rounded-2xl border transition-all duration-300 flex items-center justify-center shrink-0 border-[var(--border-color)] bg-black/5 dark:bg-white/5 text-[var(--text-primary)] hover:bg-indigo-500/10 hover:border-indigo-500/30 hover:text-indigo-400 hover:scale-[1.02] active:scale-95 cursor-pointer disabled:cursor-not-allowed"
                :class="goods.stockCount <= 0 ? 'opacity-50' : ''"
                title="加入购物车"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </button>

              <!-- 点赞按钮 -->
              <button
                @click="handleLike"
                class="w-14 h-14 rounded-2xl border transition-all duration-300 flex items-center justify-center relative group shrink-0 active:scale-95 cursor-pointer"
                :class="[
                  isLiked
                    ? 'bg-indigo-500/20 border-indigo-500 text-indigo-400 shadow-[0_0_15px_rgba(99,102,241,0.4)]'
                    : 'bg-black/5 dark:bg-white/5 border-[var(--border-color)] text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:border-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10'
                ]"
                title="点赞商品"
                :disabled="isLiked"
              >
                <!-- Thumbs Up Icon -->
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0 transition-transform duration-300 group-hover:scale-110" :fill="isLiked ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                </svg>
                
                <!-- Floating +1 Animation -->
                <span v-if="showPlusOne" class="absolute -top-8 text-indigo-400 font-bold text-sm animate-float-up">+1</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-[var(--bg-secondary)] flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
          </div>
          <p class="text-[var(--text-muted)] text-lg">商品不存在</p>
          <router-link to="/" class="mt-4 inline-block btn-primary px-6 py-3 rounded-xl">返回首页</router-link>
        </div>
      </div>
    </div>

    <!-- 秒杀结果弹窗 -->
    <Transition name="fade">
      <div v-if="showResultModal" class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4">
        <div class="glass-card rounded-3xl p-8 w-full max-w-md transform transition-all" :class="showResultModal ? 'scale-100 opacity-100' : 'scale-95 opacity-0'">
          <!-- Success State -->
          <div v-if="seckillResult.success" class="text-center">
            <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-success/20 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-[var(--text-primary)] mb-2 font-display">秒杀成功!</h3>
            <p class="text-[var(--text-secondary)] mb-6">恭喜您成功抢到商品，请在30分钟内完成支付</p>

            <div class="glass-card rounded-2xl p-4 mb-6 text-left">
              <div class="flex justify-between mb-2">
                <span class="text-[var(--text-muted)]">订单号</span>
                <span class="text-[var(--text-primary)] font-mono">{{ seckillResult.data?.orderId }}</span>
              </div>
              <div class="flex justify-between mb-2">
                <span class="text-[var(--text-muted)]">商品</span>
                <span class="text-[var(--text-primary)]">{{ goods?.goodsName }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-[var(--text-muted)]">价格</span>
                <span class="text-success font-bold">¥{{ goods?.seckillPrice }}</span>
              </div>
            </div>

            <div class="flex gap-3">
              <button
                @click="showResultModal = false"
                class="flex-1 py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all"
              >
                稍后支付
              </button>
              <button
                @click="goToOrderDetail(seckillResult.data?.orderId)"
                class="flex-1 py-3 rounded-xl bg-gradient-to-r from-primary to-secondary text-white font-medium hover:shadow-glow transition-all"
              >
                立即支付
              </button>
            </div>
          </div>

          <!-- Error State -->
          <div v-else class="text-center">
            <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-danger/20 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-[var(--text-primary)] mb-2 font-display">秒杀失败</h3>
            <p class="text-[var(--text-secondary)] mb-6">{{ seckillResult.message }}</p>
            <button
              @click="showResultModal = false"
              class="w-full py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all"
            >
              我知道了
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Toast Notification -->
    <Transition name="fade">
      <div v-if="toastMessage" class="fixed top-24 left-1/2 -translate-x-1/2 z-50 glass-card px-6 py-3 border border-emerald-500/30 bg-emerald-500/10 text-success shadow-glow flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="font-medium text-sm">{{ toastMessage }}</span>
      </div>
    </Transition>

    <!-- 购物车侧边抽屉 -->
    <Transition name="slide-over">
      <div v-if="showCartDrawer" class="fixed inset-0 z-50 overflow-hidden font-sans">
        <!-- 背景蒙版 -->
        <div class="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" @click="showCartDrawer = false"></div>
        
        <div class="absolute inset-y-0 right-0 max-w-full flex pl-10">
          <div class="w-screen max-w-md glass-card rounded-l-3xl rounded-r-none border-y-0 border-r-0 border-l border-[var(--border-color)] overflow-hidden shadow-card flex flex-col bg-slate-900/90 dark:bg-slate-950/90 backdrop-blur-xl">
            <!-- 头部 -->
            <header class="p-6 border-b border-[var(--border-color)] flex justify-between items-center bg-gradient-to-r from-indigo-500/10 to-transparent">
              <div class="flex items-center space-x-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
                <h2 class="text-lg font-bold text-[var(--text-primary)] font-display">我的购物车</h2>
                <span class="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-primary/10 text-primary">{{ cartStore.totalCount.value }}</span>
              </div>
              <div class="flex items-center space-x-2">
                <button
                  v-if="cartStore.cart.items.length > 0"
                  @click="cartStore.clearCart()"
                  class="text-xs text-[var(--text-muted)] hover:text-danger flex items-center gap-1 py-1 px-2 rounded-lg hover:bg-black/5 dark:hover:bg-white/5 transition-all cursor-pointer"
                >
                  清空
                </button>
                <button @click="showCartDrawer = false" class="p-2 rounded-xl text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors cursor-pointer">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </header>

            <!-- 列表区 -->
            <div class="flex-1 overflow-y-auto p-6 space-y-4">
              <div v-if="cartStore.cart.items.length === 0" class="flex flex-col items-center justify-center h-full text-center space-y-4">
                <div class="w-16 h-16 rounded-full bg-[var(--bg-secondary)] border border-[var(--border-color)] flex items-center justify-center text-[var(--text-muted)]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                </div>
                <p class="text-[var(--text-secondary)] text-sm">购物车是空的，快去选购吧！</p>
                <button @click="handleGoShopping" class="btn-primary text-xs font-semibold px-4 py-2 rounded-xl cursor-pointer">去逛逛</button>
              </div>

              <div
                v-else
                v-for="item in cartStore.cart.items"
                :key="item.id"
                class="flex gap-4 p-4 rounded-2xl bg-black/5 dark:bg-white/5 border border-[var(--border-color)] group hover:border-primary/20 transition-all duration-300"
              >
                <!-- 商品图 -->
                <div class="w-16 h-16 rounded-xl overflow-hidden shrink-0 bg-[var(--bg-secondary)] border border-[var(--border-color)]">
                  <img :src="item.goodsImg" :alt="item.goodsName" class="w-full h-full object-cover" />
                </div>
                <!-- 信息 -->
                <div class="flex-1 flex flex-col justify-between min-w-0">
                  <div class="flex justify-between items-start gap-2">
                    <h4 class="text-sm font-semibold text-[var(--text-primary)] truncate" :title="item.goodsName">{{ item.goodsName }}</h4>
                    <button @click="cartStore.removeFromCart(item.id)" class="text-[var(--text-muted)] hover:text-danger transition-colors p-1 cursor-pointer">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                  <div class="flex justify-between items-center mt-2">
                    <span class="text-sm font-bold text-primary">¥{{ item.seckillPrice }}</span>
                    <!-- 数量加减 -->
                    <div class="flex items-center border border-[var(--border-color)] rounded-xl overflow-hidden bg-[var(--bg-secondary)]">
                      <button @click="cartStore.updateCount(item.id, item.count - 1)" class="px-2.5 py-1 hover:bg-black/10 dark:hover:bg-white/10 text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-all cursor-pointer">-</button>
                      <span class="px-3 text-xs font-semibold text-[var(--text-primary)] select-none">{{ item.count }}</span>
                      <button @click="cartStore.updateCount(item.id, item.count + 1)" class="px-2.5 py-1 hover:bg-black/10 dark:hover:bg-white/10 text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-all cursor-pointer">+</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 底部结算区 -->
            <footer v-if="cartStore.cart.items.length > 0" class="p-6 border-t border-[var(--border-color)] bg-black/5 dark:bg-white/5 space-y-4">
              <div class="flex justify-between items-center text-sm">
                <span class="text-[var(--text-secondary)]">合计金额</span>
                <span class="text-xl font-bold text-success font-display">¥{{ cartStore.totalPrice.value }}</span>
              </div>
              <button @click="handleCartCheckout" class="w-full btn-primary py-3.5 rounded-2xl font-semibold flex items-center justify-center gap-2 hover:shadow-glow cursor-pointer">
                <span>去结算下单</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </button>
            </footer>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '../api'
import { useUserStore, useGoodsStore, useThemeStore, useCartStore } from '../stores'
import { formatProductImage } from '../utils/image'

const router = useRouter()
const route = useRoute()
const { user, logout, checkLoginStatus } = useUserStore()
const { currentGoods, loading, error, setCurrentGoods, setLoading, setError } = useGoodsStore()
const { toggleTheme, initTheme } = useThemeStore()
const cartStore = useCartStore()

const showCartDrawer = ref(false)

const handleGoShopping = () => {
  showCartDrawer.value = false
  router.push({ path: '/', query: { scroll: 'products' } })
}

const handleAddToCart = () => {
  if (!goods.value) return
  cartStore.addToCart(goods.value, 1)
  showToast('🛒 已成功加入购物车！')
}

const handleCartCheckout = () => {
  if (cartStore.cart.items.length === 0) return
  if (!user.isLoggedIn) {
    showToast('请先登录以完成结算')
    router.push('/login')
    return
  }

  // 生成 mock_orders 并存储
  const mockOrders = JSON.parse(localStorage.getItem('mock_orders')) || []
  cartStore.cart.items.forEach(item => {
    const mockId = 'mock_' + Math.random().toString(36).substring(2, 11)
    mockOrders.unshift({
      id: mockId,
      goodsId: item.id,
      goodsName: item.goodsName,
      goodsTitle: item.goodsName + ' (购物车合并下单)',
      seckillPrice: item.seckillPrice,
      goodsPrice: item.goodsPrice,
      goodsImg: item.goodsImg,
      count: item.count,
      status: 1, // 1: 待支付
      state: 1, // 1: 待支付
      stateDesc: '待支付',
      createTime: new Date().getTime(),
      userPhone: user.phone || '13800000000'
    })
  })
  localStorage.setItem('mock_orders', JSON.stringify(mockOrders))

  showToast('🎉 下单成功！已生成待支付订单')
  cartStore.clearCart()
  showCartDrawer.value = false
  setTimeout(() => {
    router.push('/orders')
  }, 1000)
}

// Liking and Toast states
const isLiked = ref(false)
const showPlusOne = ref(false)
const toastMessage = ref('')
let toastTimeout = null

const showToast = (message) => {
  if (toastTimeout) clearTimeout(toastTimeout)
  toastMessage.value = message
  toastTimeout = setTimeout(() => {
    toastMessage.value = ''
  }, 2500)
}

const checkLikedState = () => {
  isLiked.value = localStorage.getItem('liked_goods_' + goodsId.value) === 'true'
}

const handleLike = () => {
  if (isLiked.value) return

  // Save liked state for this product
  localStorage.setItem('liked_goods_' + goodsId.value, 'true')
  isLiked.value = true

  // Increment global likes count
  const likesCount = Number(localStorage.getItem('likes_count') || 0)
  localStorage.setItem('likes_count', likesCount + 1)

  // Trigger floating +1 animation
  showPlusOne.value = true
  setTimeout(() => {
    showPlusOne.value = false
  }, 800)

  showToast('点赞成功！感谢您的评价')
}

const goodsId = computed(() => route.params.id)
const goods = computed(() => currentGoods.value)

const seckillLoading = ref(false)
const showResultModal = ref(false)
const seckillResult = ref({})
const viewerCount = ref(0)

// Countdown timer
const countdown = ref({ hours: '00', minutes: '00', seconds: '00' })
let countdownInterval = null

const handleLogout = () => {
  logout()
  router.push('/login')
}

const updateCountdown = () => {
  if (!goods.value) return

  const now = Date.now()
  const endTime = goods.value.endTime
  const diff = endTime - now

  if (diff <= 0) {
    countdown.value = { hours: '00', minutes: '00', seconds: '00' }
    return
  }

  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24)
  const minutes = Math.floor((diff / (1000 * 60)) % 60)
  const seconds = Math.floor((diff / 1000) % 60)

  countdown.value = {
    hours: hours.toString().padStart(2, '0'),
    minutes: minutes.toString().padStart(2, '0'),
    seconds: seconds.toString().padStart(2, '0')
  }
}

const goToOrderDetail = (orderId) => {
  showResultModal.value = false
  if (orderId) {
    router.push(`/order/${orderId}`)
  } else {
    router.push('/orders')
  }
}

const fetchGoodsDetail = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getGoodsDetail(goodsId.value)
    console.log('商品详情响应:', response)
    if (response && (response.seckillId || response.goodsId)) {
      const name = response.goodsName || response.name
      const rawImg = response.goodsImg || response.photoUrl
      response.goodsImg = formatProductImage(rawImg, name)
      setCurrentGoods(response)
    } else if (response.code === 0 || response.code === 200) {
      const rawImg = response.data.goodsImg || response.data.photoUrl
      const goodsName = response.data.name || response.data.goodsName
      const goodsImg = formatProductImage(rawImg, goodsName)
      const transformedGoods = {
        id: response.data.seckillId || response.data.id,
        goodsName: response.data.name || response.data.goodsName,
        goodsTitle: response.data.name || response.data.goodsTitle,
        seckillPrice: response.data.price || response.data.seckillPrice,
        goodsPrice: (response.data.price || response.data.seckillPrice) * 1.2,
        stockCount: response.data.number || response.data.stockCount,
        endTime: new Date(response.data.endTime).getTime() || response.data.endTime,
        goodsDesc: (response.data.name || response.data.goodsName || '商品') + ' 是一款非常好的商品，采用优质材料制作，工艺精湛，品质保证。限时秒杀价，抢到就是赚到！',
        goodsImg: goodsImg
      }
      console.log('处理后transformedGoods:', transformedGoods)
      setCurrentGoods(transformedGoods)
    } else {
      setError(response.message || response.msg || '获取商品详情失败')
    }
  } catch (err) {
    setError('获取商品详情失败，请稍后重试')
    console.error('获取商品详情失败:', err)
  } finally {
    setLoading(false)
  }
}

const handleSeckill = async () => {
  if (goods.value.stockCount <= 0 || goods.value.endTime < Date.now()) return

  if (!user.isLoggedIn || !user.phone) {
    seckillResult.value = {
      success: false,
      message: '请先登录并完善手机号信息'
    }
    showResultModal.value = true
    setTimeout(() => {
      showResultModal.value = false
      router.push('/login')
    }, 1500)
    return
  }

  seckillLoading.value = true
  try {
    const exposeResponse = await api.exportSeckillUrl(goodsId.value)
    if (exposeResponse.code === 0 && exposeResponse.data.exposed) {
      const executeResponse = await api.executeSeckill({
        seckillId: goodsId.value,
        userPhone: user.phone,
        userId: user.id,
        md5: exposeResponse.data.md5
      })
      if (executeResponse.code === 0 || executeResponse.code === 200) {
        seckillResult.value = {
          success: true,
          message: '秒杀成功！请在30分钟内完成支付。',
          data: executeResponse.data
        }
        // Increment the sold count in localStorage!
        const soldCount = Number(localStorage.getItem('today_sold_count') || 2847)
        localStorage.setItem('today_sold_count', soldCount + 1)
      } else {
        seckillResult.value = {
          success: false,
          message: executeResponse.msg || executeResponse.message || '秒杀失败'
        }
      }
      showResultModal.value = true
      fetchGoodsDetail()
    } else {
      seckillResult.value = {
        success: false,
        message: '秒杀活动未开始或已结束'
      }
      showResultModal.value = true
    }
  } catch (err) {
    seckillResult.value = {
      success: false,
      message: '秒杀失败，请稍后重试'
    }
    showResultModal.value = true
    console.error('秒杀失败:', err)
  } finally {
    seckillLoading.value = false
  }
}

onMounted(() => {
  initTheme()
  checkLoginStatus()
  fetchGoodsDetail()
  checkLikedState()
  countdownInterval = setInterval(updateCountdown, 1000)
  updateCountdown()
  // 生成浏览人数
  viewerCount.value = Math.floor(300 + Math.random() * 200)
})

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
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

@keyframes floatUp {
  0% {
    transform: translateY(10px);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateY(-20px);
    opacity: 0;
  }
}

.animate-float-up {
  animation: floatUp 0.8s ease-out forwards;
}

/* slide-over 动画 */
.slide-over-enter-active,
.slide-over-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.slide-over-enter-from,
.slide-over-leave-to {
  opacity: 0;
}

.slide-over-enter-from .w-screen,
.slide-over-leave-to .w-screen {
  transform: translateX(100%);
}
</style>
