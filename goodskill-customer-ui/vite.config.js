import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    viteMockServe({
      mockPath: './src/mock',
      localEnabled: false,
      prodEnabled: false
    })
  ],
  server: {
    host: 'localhost',
    port: 3000,
    proxy: {
      '/api/auth': {
        target: 'http://127.0.0.1:80',
        changeOrigin: true
      },
      '/api/seckill': {
        target: 'http://127.0.0.1:80',
        changeOrigin: true
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
        target: 'http://127.0.0.1:80',
        changeOrigin: true
      }
    }
  }
})
