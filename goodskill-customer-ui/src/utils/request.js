import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('access_token')
    if (token) {
      config.headers['access_token'] = token
      console.log('Token added to request:', token)
    } else {
      console.log('No token found in localStorage')
    }
    console.log('Request URL:', config.url)
    console.log('Request Headers:', config.headers)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 登录接口使用code=0表示成功，其他接口使用code=200表示成功
    if (res.code !== 0 && res.code !== 200) {
      const errorMsg = res.msg || res.message || 'Error'
      console.error('响应错误:', errorMsg)
      return Promise.reject(new Error(errorMsg))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    if (error.response && error.response.status === 401) {
      console.warn('登录已过期或未授权，跳转至登录页')
      localStorage.removeItem('access_token')
      localStorage.removeItem('userinfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service