import {computed, reactive, ref} from 'vue'

// 主题状态
export const useThemeStore = () => {
  const theme = ref(localStorage.getItem('theme') || 'dark')

  const isDark = computed(() => theme.value === 'dark')
  const isLight = computed(() => theme.value === 'light')

  const setTheme = (newTheme) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
    applyTheme()
  }

  const toggleTheme = () => {
    const newTheme = theme.value === 'dark' ? 'light' : 'dark'
    setTheme(newTheme)
  }

  const applyTheme = () => {
    const html = document.documentElement
    if (theme.value === 'dark') {
      html.classList.add('dark')
      html.classList.remove('light')
    } else {
      html.classList.add('light')
      html.classList.remove('dark')
    }
  }

  const initTheme = () => {
    applyTheme()
  }

  return {
    theme,
    isDark,
    isLight,
    setTheme,
    toggleTheme,
    initTheme
  }
}

// 全局共享的响应式用户状态单例
const globalUser = reactive({
  isLoggedIn: false,
  userId: null,
  username: null,
  phone: null,
  email: null,
  avatar: null,
  balance: localStorage.getItem('user_balance') || '10000000.00',
  token: localStorage.getItem('access_token') || null
})

// 用户状态
export const useUserStore = () => {
  const user = globalUser

  const login = (token, userId, username, phone, email = null, avatar = null) => {
    user.token = token
    user.userId = userId
    user.username = username
    user.phone = phone
    user.email = email
    user.avatar = avatar
    user.balance = localStorage.getItem('user_balance') || '10000000.00'
    user.isLoggedIn = true
    localStorage.setItem('access_token', token)
    localStorage.setItem('user_balance', user.balance)
    localStorage.removeItem('local_avatar_cache') // 登录新用户时清除上一个用户的本地头像缓存，防交叉污染
    localStorage.setItem('userinfo', JSON.stringify({
      userId,
      username,
      phone,
      email,
      avatar
    }))
  }

  const logout = () => {
    user.token = null
    user.userId = null
    user.username = null
    user.phone = null
    user.email = null
    user.avatar = null
    user.balance = '10000000.00'
    user.isLoggedIn = false
    localStorage.removeItem('access_token')
    localStorage.removeItem('user_balance')
    localStorage.removeItem('userinfo')
    localStorage.removeItem('local_avatar_cache') // 登出时清除头像缓存
  }

  const deductBalance = (amount) => {
    const current = parseFloat(user.balance) || 0
    const newBal = Math.max(0, current - parseFloat(amount)).toFixed(2)
    user.balance = newBal
    localStorage.setItem('user_balance', newBal)
  }

  const checkLoginStatus = () => {
    const token = localStorage.getItem('access_token')
    if (token) {
      user.token = token
      user.isLoggedIn = true
      user.balance = localStorage.getItem('user_balance') || '10000000.00'
      // 恢复用户信息
      const userinfoStr = localStorage.getItem('userinfo')
      if (userinfoStr) {
        try {
          const userinfo = JSON.parse(userinfoStr)
          user.userId = userinfo.userId
          user.username = userinfo.username
          user.phone = userinfo.phone
          user.email = userinfo.email
          
          user.avatar = userinfo.avatar
        } catch (e) {
          console.error('解析用户信息失败:', e)
        }
      }
    }
  }

  const updateUserPhone = (phone) => {
    user.phone = phone
    // 更新localStorage中的用户信息
    const userinfoStr = localStorage.getItem('userinfo')
    if (userinfoStr) {
      try {
        const userinfo = JSON.parse(userinfoStr)
        userinfo.phone = phone
        localStorage.setItem('userinfo', JSON.stringify(userinfo))
      } catch (e) {
        console.error('更新用户信息失败:', e)
      }
    }
  }

  const updateUserInfo = (userInfo) => {
    user.userId = userInfo.userId
    user.username = userInfo.username
    user.phone = userInfo.mobile
    user.email = userInfo.emailAddr
    user.avatar = userInfo.avatar

    // 写入localStorage
    const userinfoStr = localStorage.getItem('userinfo')
    if (userinfoStr) {
      try {
        const userinfo = JSON.parse(userinfoStr)
        userinfo.username = userInfo.username
        userinfo.email = userInfo.emailAddr
        userinfo.avatar = userInfo.avatar
        localStorage.setItem('userinfo', JSON.stringify(userinfo))
      } catch (e) {
        console.error('更新用户信息失败:', e)
      }
    }
  }

  const updateAvatar = (avatarUrl) => {
    user.avatar = avatarUrl
    
    // 更新localStorage中的用户信息
    const userinfoStr = localStorage.getItem('userinfo')
    if (userinfoStr) {
      try {
        const userinfo = JSON.parse(userinfoStr)
        userinfo.avatar = avatarUrl
        localStorage.setItem('userinfo', JSON.stringify(userinfo))
      } catch (e) {
        console.error('更新头像失败:', e)
      }
    }
  }

  return {
    user,
    login,
    logout,
    checkLoginStatus,
    updateUserPhone,
    updateUserInfo,
    updateAvatar,
    deductBalance
  }
}

// 商品状态
export const useGoodsStore = () => {
  const goodsList = ref([])
  const currentGoods = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const setGoodsList = (list) => {
    goodsList.value = list
  }

  const setCurrentGoods = (goods) => {
    currentGoods.value = goods
  }

  const setLoading = (status) => {
    loading.value = status
  }

  const setError = (err) => {
    error.value = err
  }

  return {
    goodsList,
    currentGoods,
    loading,
    error,
    setGoodsList,
    setCurrentGoods,
    setLoading,
    setError
  }
}

// 订单状态
export const useOrderStore = () => {
  const orderList = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const setOrderList = (list) => {
    orderList.value = list
  }

  const setCurrentOrder = (order) => {
    currentOrder.value = order
  }

  const setLoading = (status) => {
    loading.value = status
  }

  const setError = (err) => {
    error.value = err
  }

  return {
    orderList,
    currentOrder,
    loading,
    error,
    setOrderList,
    setCurrentOrder,
    setLoading,
    setError
  }
}

// 全局购物车状态单例
const globalCart = reactive({
  items: JSON.parse(localStorage.getItem('cart_items')) || []
})

// 购物车状态 Store
export const useCartStore = () => {
  const cart = globalCart

  const saveCart = () => {
    localStorage.setItem('cart_items', JSON.stringify(cart.items))
  }

  const addToCart = (product, count = 1) => {
    const existing = cart.items.find(item => item.id === product.id)
    if (existing) {
      existing.count += count
    } else {
      cart.items.push({
        id: product.id,
        goodsName: product.goodsName || product.name,
        goodsImg: product.goodsImg,
        seckillPrice: product.seckillPrice,
        goodsPrice: product.goodsPrice,
        count: count
      })
    }
    saveCart()
  }

  const removeFromCart = (productId) => {
    cart.items = cart.items.filter(item => item.id !== productId)
    saveCart()
  }

  const updateCount = (productId, count) => {
    const item = cart.items.find(item => item.id === productId)
    if (item) {
      item.count = Math.max(1, count)
      saveCart()
    }
  }

  const clearCart = () => {
    cart.items = []
    saveCart()
  }

  const totalCount = computed(() => {
    return cart.items.reduce((sum, item) => sum + item.count, 0)
  })

  const totalPrice = computed(() => {
    return cart.items.reduce((sum, item) => sum + item.count * item.seckillPrice, 0).toFixed(2)
  })

  return {
    cart,
    addToCart,
    removeFromCart,
    updateCount,
    clearCart,
    totalCount,
    totalPrice
  }
}
