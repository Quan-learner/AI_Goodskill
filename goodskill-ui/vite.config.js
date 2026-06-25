import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/auth': {
        target: 'http://127.0.0.1:19081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/auth/, '')
      },
      '/api/web': {
        target: 'http://127.0.0.1:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/web/, '')
      },
      '/api/seckill': {
        target: 'http://127.0.0.1:54870',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/seckill/, '')
      },
      '/api/order': {
        target: 'http://127.0.0.1:80',
        changeOrigin: true
      },
      '/api/common': {
        target: 'http://127.0.0.1:80',
        changeOrigin: true
      },
      '/api': {
        target: 'http://127.0.0.1:8082',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  define: {
    global: 'window'
  }
})
