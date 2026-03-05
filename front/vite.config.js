import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/login': 'http://localhost:8080',
      '/patients': 'http://localhost:8080',
      '/doctors': 'http://localhost:8080',
      '/aliments': 'http://localhost:8080',
      '/meals': 'http://localhost:8080',
      '/blood-tests': 'http://localhost:8080',
    }
  }
})