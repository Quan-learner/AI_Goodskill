<template>
  <div class="ai-assistant-container fixed bottom-6 right-6 z-50 font-sans">
    <!-- 悬浮唤起按钮 (FAB) -->
    <Transition name="fab-fade">
      <button
        v-if="!isOpen"
        @click="toggleChat"
        class="w-14 h-14 rounded-full bg-gradient-to-br from-indigo-500 via-purple-500 to-cyan-500 flex items-center justify-center text-white shadow-glow hover:scale-110 active:scale-95 transition-all duration-300 relative group animate-float"
        title="AI 秒杀助手"
        id="ai-assistant-fab"
      >
        <span class="absolute inset-0 rounded-full bg-indigo-500/30 blur-md group-hover:blur-lg transition-all duration-300 opacity-75 group-hover:scale-125"></span>
        <svg xmlns="http://www.w3.org/2000/svg" class="h-7 w-7 relative z-10 transform group-hover:rotate-12 transition-transform duration-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M8 9l3 3-3 3m5 0h3M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <!-- 呼吸灯红点 -->
        <span class="absolute -top-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-500 rounded-full border-2 border-[var(--bg-primary)] flex items-center justify-center">
          <span class="absolute w-2 h-2 bg-emerald-400 rounded-full animate-ping opacity-75"></span>
        </span>
      </button>
    </Transition>

    <!-- 聊天卡片面板 -->
    <Transition name="panel-slide">
      <div
        v-if="isOpen"
        class="w-[380px] sm:w-[420px] h-[580px] glass-card shadow-card flex flex-col border border-[var(--border-color)] overflow-hidden transition-all duration-300"
        id="ai-assistant-panel"
      >
        <!-- 头部栏 -->
        <header class="flex justify-between items-center px-5 py-4 border-b border-[var(--border-color)] bg-gradient-to-r from-indigo-500/10 via-purple-500/10 to-transparent">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center shadow-glow">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5.5 w-5.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <h3 class="text-sm font-bold text-[var(--text-primary)] flex items-center gap-1.5 font-display">
                AI 智能助手
                <span class="inline-flex items-center px-1.5 py-0.2 rounded-md text-[10px] font-semibold bg-emerald-500/10 text-emerald-500 border border-emerald-500/20">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 mr-1 animate-pulse"></span>
                  在线
                </span>
              </h3>
              <p class="text-[10.5px] text-[var(--text-muted)] mt-0.5">为您全自动配置并开启秒杀任务</p>
            </div>
          </div>
          <!-- 头部操作按钮 -->
          <div class="flex items-center space-x-1.5">
            <!-- 模拟模式开关 -->
            <button
              @click="toggleSimulated"
              class="px-2.5 py-1 rounded-lg text-[10.5px] font-medium transition-all flex items-center space-x-1 border"
              :class="isSimulated 
                ? 'bg-amber-500/10 text-amber-500 border-amber-500/20 shadow-glow' 
                : 'bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] border-[var(--border-color)] hover:border-primary/30'"
              title="切换到模拟助手模式（免配置接口直接体验）"
            >
              <span>{{ isSimulated ? '模拟模式: ON' : '模拟模式' }}</span>
            </button>
            <!-- 清空聊天 -->
            <button
              @click="clearChat"
              class="p-2 rounded-xl text-[var(--text-secondary)] hover:text-danger hover:bg-black/5 dark:hover:bg-white/5 transition-colors"
              title="清空记录"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
            <!-- 最小化 -->
            <button
              @click="toggleChat"
              class="p-2 rounded-xl text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors"
              title="最小化"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </header>

        <!-- 消息列表区 -->
        <div ref="messageContainer" class="flex-1 overflow-y-auto px-5 py-4 space-y-4 messages-container scroll-smooth">
          <div
            v-for="msg in messages"
            :key="msg.id"
            class="flex flex-col"
            :class="msg.sender === 'user' ? 'items-end' : 'items-start'"
          >
            <!-- 时间标识 -->
            <span class="text-[9.5px] text-[var(--text-muted)] mb-1 px-1 font-mono">{{ msg.time }}</span>
            <div class="flex items-start gap-2.5 max-w-[85%]">
              <!-- AI 头像 -->
              <div
                v-if="msg.sender === 'ai'"
                class="w-8 h-8 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center text-white text-xs shadow-md shrink-0 mt-0.5"
              >
                AI
              </div>
              <!-- 消息气泡 -->
              <div
                class="px-4 py-3 rounded-2xl text-sm leading-relaxed transition-all duration-300"
                :class="msg.sender === 'user' 
                  ? 'bg-gradient-to-br from-indigo-500 to-purple-600 text-white rounded-tr-none shadow-md' 
                  : 'bg-black/5 dark:bg-white/5 text-[var(--text-primary)] border border-[var(--border-color)] rounded-tl-none font-normal'"
              >
                <!-- 支持 Markdown 渲染 -->
                <div v-html="formatMessageText(msg.text)" class="markdown-body"></div>
              </div>
            </div>
          </div>

          <!-- 等待输入加载动画 -->
          <div v-if="isLoading" class="flex items-center space-x-2.5 max-w-[85%] items-start">
            <div class="w-8 h-8 rounded-xl bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center text-white text-xs shadow-md shrink-0">
              AI
            </div>
            <div class="px-4.5 py-3.5 rounded-2xl rounded-tl-none bg-black/5 dark:bg-white/5 border border-[var(--border-color)] flex items-center space-x-1.5">
              <span class="w-2 h-2 rounded-full bg-[var(--text-secondary)] animate-bounce" style="animation-delay: 0ms"></span>
              <span class="w-2 h-2 rounded-full bg-[var(--text-secondary)] animate-bounce" style="animation-delay: 150ms"></span>
              <span class="w-2 h-2 rounded-full bg-[var(--text-secondary)] animate-bounce" style="animation-delay: 300ms"></span>
            </div>
          </div>
        </div>

        <!-- 快捷命令建议区 -->
        <div v-if="userStore.user.isLoggedIn" class="px-5 py-2 border-t border-[var(--border-color)] bg-black/5 dark:bg-white/2 overflow-x-auto flex space-x-2 scrollbar-none items-center shrink-0">
          <button
            v-for="(sug, index) in activeSuggestions"
            :key="index"
            @click="sendSuggestion(sug.text)"
            class="px-3.5 py-1.5 rounded-full text-[11px] bg-black/5 dark:bg-white/5 border border-[var(--border-color)] text-[var(--text-secondary)] hover:text-indigo-400 hover:border-indigo-500/50 hover:bg-indigo-500/5 transition-all duration-300 cursor-pointer whitespace-nowrap active:scale-95 shadow-sm"
          >
            {{ sug.label }}
          </button>
        </div>

        <!-- 底部输入栏 -->
        <footer v-if="!userStore.user.isLoggedIn" class="p-4 border-t border-[var(--border-color)] bg-gradient-to-b from-transparent to-black/10 flex flex-col items-center shrink-0 space-y-2 w-full">
          <p class="text-xs text-[var(--text-secondary)]">您当前未登录，无法发送指令</p>
          <router-link to="/login" @click="isOpen = false" class="px-5 py-2.5 rounded-xl bg-gradient-to-r from-indigo-500 to-purple-600 text-white text-xs font-semibold hover:scale-102 active:scale-95 transition-all text-center w-full shadow-glow">
            立即去登录
          </router-link>
        </footer>
        <footer v-else class="p-4 border-t border-[var(--border-color)] flex items-center space-x-2.5 bg-gradient-to-b from-transparent to-black/10 shrink-0">
          <input
            v-model="inputText"
            @keydown.enter.prevent="sendMessage"
            placeholder="问问秒杀任务，输入或点击建议标签..."
            class="flex-1 min-w-0 px-4 py-3 bg-[var(--bg-secondary)] border border-[var(--border-color)] rounded-2xl text-xs text-[var(--text-primary)] placeholder-[var(--text-muted)] focus:outline-none focus:border-indigo-500/50 focus:ring-2 focus:ring-indigo-500/10 transition-all font-normal"
            :disabled="isLoading"
            ref="textInput"
          />
          <button
            @click="sendMessage"
            class="p-3 rounded-2xl bg-gradient-to-r from-indigo-500 to-purple-600 text-white shadow-glow hover:scale-105 active:scale-95 transition-all shrink-0 cursor-pointer"
            :disabled="isLoading || !inputText.trim()"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4.5 w-4.5 transform rotate-90" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </footer>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, watch } from 'vue'
import { useGoodsStore, useUserStore } from '../stores'

const isOpen = ref(false)
const inputText = ref('')
const isLoading = ref(false)
const isSimulated = ref(localStorage.getItem('ai_simulated_mode') === 'true')
const messages = ref([])
const chatId = ref('')
const messageContainer = ref(null)
const textInput = ref(null)

const goodsStore = useGoodsStore()
const userStore = useUserStore()

// 建议提示卡片
const activeSuggestions = computed(() => {
  const list = goodsStore.goodsList.value || []
  const activeGoodsId = list.length > 0 ? (list[0].id || list[0].goodsId) : 1001
  
  return [
    { text: 'query_goods_detail_action', label: '🔍 查询商品详情' },
    { text: `对商品 ${activeGoodsId} 开启秒杀，数量 10，请求 100`, label: `🚀 开启秒杀任务` },
    { text: '获取最新任务耗时统计信息', label: '📊 获取任务耗时统计' }
  ]
})

// 格式化时间
const formatTime = () => {
  return new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

// 监听登录状态，自动重载欢迎词
watch(() => userStore.user.isLoggedIn, (newVal) => {
  loadInitialGreeting()
})

// 初始化对话ID和第一条消息
onMounted(() => {
  chatId.value = sessionStorage.getItem('ai_chat_id')
  if (!chatId.value) {
    chatId.value = 'chat_' + Math.random().toString(36).substring(2, 11)
    sessionStorage.setItem('ai_chat_id', chatId.value)
  }

  const savedMessages = localStorage.getItem(`ai_messages_${chatId.value}`)
  if (savedMessages && userStore.user.isLoggedIn) {
    try {
      messages.value = JSON.parse(savedMessages)
    } catch (e) {
      loadInitialGreeting()
    }
  } else {
    loadInitialGreeting()
  }
})

const loadInitialGreeting = () => {
  if (!userStore.user.isLoggedIn) {
    messages.value = [
      {
        id: Date.now(),
        sender: 'ai',
        text: '您好！我是您的 **AI 智能秒杀助手**。🤖\n\n您当前尚未登录，请先登录系统以解锁完整的 AI 自动配置、开启高并发秒杀任务与查看压测耗时统计功能！',
        time: formatTime()
      }
    ]
    return
  }
  
  messages.value = [
    {
      id: Date.now(),
      sender: 'ai',
      text: '您好！我是您的 **AI 智能秒杀助手**。🤖\n\n我可以帮助您完成以下操作：\n- **查询商品详情** (例如：`查询商品 1001 详情`)\n- **配置并启动秒杀** (例如：`对商品 1001 开启秒杀，数量 10，请求 100`)\n- **监控任务运行统计** (在模拟秒杀开启后，会自动/手动拉取响应数据并呈现)\n\n请输入问题，或点击下方的快捷命令进行探索。👇',
      time: formatTime()
    }
  ]
  saveMessages()
}

// 缓存消息
const saveMessages = () => {
  localStorage.setItem(`ai_messages_${chatId.value}`, JSON.stringify(messages.value))
}

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    nextTick(() => {
      scrollToBottom()
      if (textInput.value) {
        textInput.value.focus()
      }
    })
  }
}

const toggleSimulated = () => {
  isSimulated.value = !isSimulated.value
  localStorage.setItem('ai_simulated_mode', isSimulated.value)
  messages.value.push({
    id: Date.now(),
    sender: 'ai',
    text: isSimulated.value 
      ? '⚠️ 已开启 **模拟调试模式**。接下来的聊天回复将由内置模拟引擎直接提供响应，不再发起真实的 AI 服务调用。这适合在本地无 Dashscope API Key 的环境下调试。' 
      : '✅ 已切换回 **真实 AI 服务模式**。发送消息将调用后端大模型代理及相关 Feign 秒杀接口。',
    time: formatTime()
  })
  saveMessages()
  nextTick(scrollToBottom)
}

const clearChat = () => {
  localStorage.removeItem(`ai_messages_${chatId.value}`)
  loadInitialGreeting()
  nextTick(scrollToBottom)
}

const sendSuggestion = (text) => {
  if (text === 'query_goods_detail_action') {
    const goodsName = window.prompt('请输入您要查询的商品名称：')
    if (goodsName && goodsName.trim()) {
      inputText.value = `查询商品 ${goodsName.trim()} 详情`
      sendMessage()
    }
  } else {
    inputText.value = text
    sendMessage()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// 正则替换简易 Markdown 为 HTML
const formatMessageText = (text) => {
  if (!text) return ''
  // 转义基础标签防 XSS
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // 1. 代码块 ```code```
  html = html.replace(/```([\s\S]*?)```/g, (match, code) => {
    return `<pre class="bg-black/30 dark:bg-black/50 p-3 rounded-2xl font-mono text-[11px] overflow-x-auto my-2 border border-white/5 text-cyan-400 select-all">${code.trim()}</pre>`
  })

  // 2. 行内代码 `code`
  html = html.replace(/`([^`\n]+)`/g, '<code class="bg-black/20 dark:bg-white/10 px-1.5 py-0.5 rounded-lg font-mono text-[11.5px] text-cyan-400">$1</code>')

  // 3. 加粗 **text**
  html = html.replace(/\*\*([^*]+)\*\*/g, '<strong class="font-bold text-[var(--accent-cyan)]">$1</strong>')

  // 4. 水平分割线
  html = html.replace(/^---$/gm, '<hr class="border-[var(--border-color)] my-3">')

  // 5. 换行
  html = html.replace(/\n/g, '<br>')

  return html
}

// 发送消息
const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || isLoading.value) return

  // 添加用户消息
  messages.value.push({
    id: Date.now(),
    sender: 'user',
    text,
    time: formatTime()
  })
  inputText.value = ''
  isLoading.value = true
  saveMessages()
  nextTick(scrollToBottom)

  if (isSimulated.value) {
    // 运行模拟回复逻辑
    setTimeout(() => {
      handleSimulatedReply(text)
    }, 1000)
  } else {
    // 调用后端真实 AI 服务
    try {
      await callRealAiService(text)
    } catch (e) {
      console.error('真实 AI 服务调用失败:', e)
      // 出错后添加 AI 错误提示，并建议切到模拟模式
      messages.value.push({
        id: Date.now(),
        sender: 'ai',
        text: `❌ **真实 AI 对话连接异常**: ${e.message || '网络连接超时'}\n\n这通常是由于未配置 Dashscope API 密钥、数据库连接问题或微服务离线引起的。您可以点击顶部 **[模拟模式]** 按钮，直接在前端体验 AI 配置与全流程秒杀压力测试！`,
        time: formatTime()
      })
      isLoading.value = false
      saveMessages()
      nextTick(scrollToBottom)
    }
  }
}

// 模拟回复引擎
const handleSimulatedReply = (text) => {
  let reply = ''
  isLoading.value = false

  const list = goodsStore.goodsList.value || []
  let matchedProduct = null
  let targetId = 1001

  const hasIdMatch = text.match(/\d+/)
  if (hasIdMatch) {
    targetId = parseInt(hasIdMatch[0])
    matchedProduct = list.find(g => (g.id === targetId || g.goodsId === targetId))
  } else {
    // 按名称模糊匹配
    const nameMatch = text.match(/查询商品\s*(.+?)\s*详情/) || text.match(/查询\s*(.+?)\s*详情/)
    if (nameMatch) {
      const nameKey = nameMatch[1].trim().toLowerCase()
      matchedProduct = list.find(g => {
        const gName = (g.goodsName || g.name || '').toLowerCase()
        return gName.includes(nameKey)
      })
      if (matchedProduct) {
        targetId = matchedProduct.id || matchedProduct.goodsId
      }
    }
  }

  if (text.includes('查询') || text.includes('详情')) {
    if (matchedProduct) {
      reply = `🔍 **【模拟助理】为您查得商品详情**：\n\n- **商品 ID**: \`${matchedProduct.id || matchedProduct.goodsId}\`\n- **商品名称**: **${matchedProduct.goodsName || matchedProduct.name || 'XiaoMi 14'}**\n- **商品标题**: ${matchedProduct.goodsTitle || '特价秒杀中'}\n- **活动原价**: ¥${matchedProduct.goodsPrice || 3999}\n- **秒杀价格**: ¥${matchedProduct.seckillPrice || 2999}\n- **剩余库存**: \`${matchedProduct.stockCount || matchedProduct.number}\` 件\n- **结束时间**: ${matchedProduct.endTime}\n- **活动状态**: **正在进行中**\n\n该商品支持自动秒杀任务，提供秒杀商品数量和并发请求次数后即可启动。`
    } else {
      const queryName = text.replace(/查询商品|详情|查询|\s/g, '')
      reply = `🔍 **【模拟助理】为您查询商品详情（未找到名称包含 "${queryName || '未知'}" 的商品）**：\n\n- **搜索关键字**: \`"${queryName}"\`\n- **提示**: 系统中目前拥有的秒杀商品包含：\n${list.map(g => `  - \`${g.goodsName || g.name}\` (ID: ${g.id || g.goodsId})`).join('\n') || '  - （暂无商品）'}\n\n您可以尝试输入其中包含的字样重新查询。`
    }
    messages.value.push({
      id: Date.now(),
      sender: 'ai',
      text: reply,
      time: formatTime()
    })
    saveMessages()
    nextTick(scrollToBottom)
  } else if (text.includes('秒杀') || text.includes('抢购') || text.includes('开启') || text.includes('压力测试')) {
    // 模拟开启秒杀
    const countMatch = text.match(/(?:数量|秒杀数)\s*(\d+)/) || text.match(/(\d+)\s*(?:件|个)/)
    const reqMatch = text.match(/(?:请求|并发|次数)\s*(\d+)/) || text.match(/(\d+)\s*(?:次|个请求)/)
    const count = countMatch ? parseInt(countMatch[1]) : 10
    const requests = reqMatch ? parseInt(reqMatch[1]) : 100

    reply = `🚀 **【模拟助理】已向 seckill-mock 触发秒杀并发任务！**\n\n- **活动 ID**: \`${targetId}\`\n- **商品名**: **${matchedProduct ? (matchedProduct.goodsName || matchedProduct.name) : '模拟数码产品'}**\n- **秒杀总数**: \`${count}\` 件\n- **并发请求**: \`${requests}\` 次\n- **分发策略**: **Procedure 数据库存储过程原子扣减**\n\n后台正在生成高并发测试线程进行秒杀压测，这需要约 3-5 秒，请稍后...`
    
    messages.value.push({
      id: Date.now(),
      sender: 'ai',
      text: reply,
      time: formatTime()
    })
    saveMessages()
    nextTick(scrollToBottom)

    // 延迟 4 秒返回耗时统计结果
    isLoading.value = true
    setTimeout(() => {
      isLoading.value = false
      const mockTaskId = 'task_' + Math.random().toString(36).substring(2, 11)
      const successCount = Math.min(count, Math.round(requests * 0.15))
      const metricsText = `📊 **【模拟助理】高并发秒杀压力测试完成！**\n\n已成功获取任务耗时统计信息：\n\n- **任务 ID**: \`${mockTaskId}\`\n- **处理总量**: \`${requests}\` 次请求\n- **成功下单**: \`${successCount}\` 笔\n- **QPS 吞吐量**: \`${(requests / 0.35).toFixed(1)}/s\`\n- **平均响应时间**: \`11.2ms\`\n- **核心锁耗时**: \`3.5ms\`\n- **网络传输延迟**: \`4.1ms\`\n\n压测数据库一致性核对完毕：**无超卖**，**无异常抛出**，订单已同步至订单中心数据库中。您可以输入“获取任务耗时统计信息”再次查看结果。`
      
      messages.value.push({
        id: Date.now(),
        sender: 'ai',
        text: metricsText,
        time: formatTime()
      })
      saveMessages()
      nextTick(scrollToBottom)
    }, 3500)
  } else if (text.includes('统计') || text.includes('耗时') || text.includes('指标') || text.includes('最新')) {
    reply = `📊 **【模拟助理】获取最新压测耗时详情**：\n\n- **平均延迟**: \`10.5ms\`\n- **TP90 延迟**: \`15.4ms\`\n- **TP99 延迟**: \`22.8ms\`\n- **数据库平均写入**: \`5.1ms\`\n- **Redis 预热校验**: \`2.8ms\`\n\n系统瓶颈分析：目前瓶颈在数据库连接池上限。建议提升 Druid 线程上限以支持更大并发。`
    messages.value.push({
      id: Date.now(),
      sender: 'ai',
      text: reply,
      time: formatTime()
    })
    saveMessages()
    nextTick(scrollToBottom)
  } else {
    reply = `🤖 **【模拟助理】收到您的信息**：\n\n“${text}”\n\n目前模拟引擎支持快捷调试。您可以问我：\n1. “查询商品 1001 详情”\n2. “对商品 1001 开启秒杀，数量 10，并发 200”\n3. “获取任务耗时统计”`
    messages.value.push({
      id: Date.now(),
      sender: 'ai',
      text: reply,
      time: formatTime()
    })
    saveMessages()
    nextTick(scrollToBottom)
  }
}

// 调用真实后端 AI 接口
const callRealAiService = async (text) => {
  // 创建占位 AI 消息以流式填入
  const placeholderId = Date.now()
  messages.value.push({
    id: placeholderId,
    sender: 'ai',
    text: '',
    time: formatTime()
  })

  const url = `/api/ai/assistant/chat?chatId=${chatId.value}&userMessage=${encodeURIComponent(text)}`
  const token = localStorage.getItem('access_token') || ''
  
  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'access_token': token
    }
  })

  if (!response.ok) {
    // 移除占位消息
    messages.value = messages.value.filter(m => m.id !== placeholderId)
    throw new Error(`HTTP ${response.status} ${response.statusText}`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  let done = false
  let accumulatedText = ''

  while (!done) {
    const { value, done: doneReading } = await reader.read()
    done = doneReading
    if (value) {
      const chunk = decoder.decode(value, { stream: !done })
      
      // SSE 格式解析 (data: xxxx)
      const lines = chunk.split('\n')
      for (const line of lines) {
        const trimmed = line.trim()
        if (trimmed.startsWith('data:')) {
          const payload = trimmed.substring(5).trim()
          if (payload) {
            accumulatedText += payload + '\n'
          }
        } else if (trimmed && !trimmed.startsWith(':') && !trimmed.startsWith('event:')) {
          // 容错：直接按文本流累加
          accumulatedText += trimmed
        }
      }

      // 动态更新消息列表中的文本
      const targetMsgIndex = messages.value.findIndex(m => m.id === placeholderId)
      if (targetMsgIndex !== -1) {
        messages.value[targetMsgIndex].text = accumulatedText.trim()
        nextTick(scrollToBottom)
      }
    }
  }

  isLoading.value = false
  saveMessages()
}
</script>

<script>
// 用于禁用全局 CSS 样式干扰
export default {
  name: 'AiAssistant'
}
</script>

<style scoped>
/* 悬浮按钮动效 */
.shadow-glow {
  box-shadow: 0 8px 30px rgba(99, 102, 241, 0.4);
}

.animate-float {
  animation: float 4s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
  100% { transform: translateY(0px); }
}

/* 进出过渡动画 */
.fab-fade-enter-active, .fab-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.fab-fade-enter-from, .fab-fade-leave-to {
  opacity: 0;
  transform: scale(0.5);
}

.panel-slide-enter-active, .panel-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.panel-slide-enter-from, .panel-slide-leave-to {
  opacity: 0;
  transform: translateY(40px) scale(0.95);
}

/* 滚动条隐藏与样式 */
.scrollbar-none::-webkit-scrollbar {
  display: none;
}
.scrollbar-none {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.messages-container::-webkit-scrollbar {
  width: 5px;
}
.messages-container::-webkit-scrollbar-track {
  background: transparent;
}
.messages-container::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 99px;
}

/* 局部 Markdown 样式微调 */
.markdown-body {
  word-break: break-word;
}
.markdown-body ul, .markdown-body ol {
  padding-left: 1.25rem;
  margin-top: 0.25rem;
  margin-bottom: 0.25rem;
}
.markdown-body li {
  margin-bottom: 0.125rem;
}
.markdown-body pre {
  max-width: 100%;
}
</style>
