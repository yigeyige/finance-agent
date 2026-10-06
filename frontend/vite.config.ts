/**
 * 财务运维工作台的 Vite 构建与本地联调配置。
 * 开发服务器仅代理同源 API，请求路径和生产部署网关契约保持不变。
 */
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue2'

export default defineConfig(({ mode }) => {
  const environment = loadEnv(mode, process.cwd(), '')
  const developmentApiTarget = environment.VITE_DEV_API_TARGET || 'http://127.0.0.1:8080'

  return {
    plugins: [vue()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: developmentApiTarget,
          changeOrigin: true,
        },
      },
    },
    build: {
      sourcemap: false,
    },
  }
})
