<!-- 工作台账号密码登录页，只在成功建立服务端会话后进入业务页面。 -->
<template>
  <main class="login-page">
    <section class="login-card">
      <span class="login-mark">财</span>
      <h1>财务运维工作台</h1>
      <p>请输入账号和密码登录</p>
      <el-form @submit.native.prevent="submit">
        <el-form-item label="账号">
          <el-input v-model.trim="username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="password"
            type="password"
            show-password
            autocomplete="current-password"
            @keyup.enter.native="submit"
          />
        </el-form-item>
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
        <el-button type="primary" class="login-submit" :loading="submitting" @click="submit">登录</el-button>
      </el-form>
    </section>
  </main>
</template>

<script lang="ts">
import Vue from 'vue'

import { login } from '../services/identity'

/** 提供账号密码登录表单并在成功后恢复原访问页面。 */
export default Vue.extend({
  name: 'Login',
  data: () => ({
    username: '',
    password: '',
    submitting: false,
    errorMessage: '',
  }),
  methods: {
    /** 提交凭据并跳转到受保护页面。 */
    async submit(): Promise<void> {
      if (!this.username || !this.password) {
        this.errorMessage = '请输入账号和密码'
        return
      }
      this.submitting = true
      this.errorMessage = ''
      try {
        await login(this.username, this.password)
        const redirect = typeof this.$route.query.redirect === 'string' ? this.$route.query.redirect : '/'
        await this.$router.replace(redirect)
      } catch {
        this.errorMessage = '账号或密码错误，请重新输入'
      } finally {
        this.submitting = false
      }
    },
  },
})
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  background: #f4f6fa;
}

.login-card {
  width: min(400px, calc(100% - 32px));
  padding: 36px;
  background: #fffdf9;
  border: 1px solid #e7dfd4;
  border-radius: 16px;
  box-shadow: 0 14px 32px rgba(83, 70, 48, 0.1);
}

.login-mark {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #dd7a13;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
}

.login-card h1 {
  margin: 16px 0 6px;
  font-size: 24px;
}

.login-card p {
  margin: 0 0 22px;
  color: #817b73;
}

.login-submit {
  width: 100%;
}

.login-card .el-alert {
  margin-bottom: 16px;
}
</style>
