<!-- 平台管理员用户管理页：复用工作台表格与弹窗，固定 admin 不提供操作入口。 -->
<template>
  <section class="entry-page user-management-page">
    <div class="page-heading entry-page-heading"><h2>用户管理</h2></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <section v-if="!errorMessage" class="entry-content-card">
      <div class="entry-content-toolbar">
        <span class="entry-content-toolbar__meta">共 {{ users.length }} 位用户</span>
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>
      <div class="standard-table-panel">
        <el-table v-loading="loading" :data="users" row-key="userId" class="standard-data-table" border stripe empty-text="暂无用户">
          <el-table-column prop="authSubject" label="登录账号" min-width="150" />
          <el-table-column prop="employeeNo" label="工号" min-width="130" />
          <el-table-column prop="displayName" label="显示名称" min-width="160" />
          <el-table-column label="角色" min-width="130"><template slot-scope="scope">{{ roleLabel(scope.row.role) }}</template></el-table-column>
          <el-table-column label="状态" min-width="100"><template slot-scope="scope">{{ scope.row.status === 'ACTIVE' ? '启用' : '停用' }}</template></el-table-column>
          <el-table-column label="操作" min-width="250" align="center">
            <template slot-scope="scope">
              <template v-if="scope.row.authSubject !== 'admin'">
                <el-button type="text" @click="openEdit(scope.row)">编辑</el-button>
                <el-button type="text" @click="openPassword(scope.row)">重置密码</el-button>
                <el-button v-if="scope.row.status === 'DISABLED'" type="text" :disabled="busy" @click="enable(scope.row)">启用</el-button>
                <el-button v-else-if="scope.row.userId !== currentUserId" type="text" :disabled="busy" @click="disable(scope.row)">删除</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>

    <el-dialog class="user-dialog" v-workbench-dialog custom-class="workbench-dialog" :title="editingId ? '编辑用户' : '新增用户'" :visible.sync="formVisible" width="600px" :close-on-click-modal="false" @closed="clearForm">
      <el-form :model="form" label-position="top" class="workbench-dialog-form">
        <el-form-item label="登录账号" required><el-input v-model.trim="form.authSubject" :disabled="Boolean(editingId)" :maxlength="200" /></el-form-item>
        <el-form-item label="工号" required><el-input v-model.trim="form.employeeNo" :disabled="Boolean(editingId)" :maxlength="100" /></el-form-item>
        <el-form-item label="显示名称" required><el-input v-model.trim="form.displayName" :maxlength="200" /></el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="普通用户" value="PLATFORM_USER" :disabled="editingId === currentUserId" />
            <el-option label="平台管理员" value="PLATFORM_ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!editingId" label="初始密码" required><el-input v-model="form.password" type="password" autocomplete="new-password" /></el-form-item>
      </el-form>
      <span slot="footer" class="workbench-dialog__actions">
        <el-button :disabled="busy" @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="busy" @click="save">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog v-workbench-dialog custom-class="workbench-dialog" title="重置密码" :visible.sync="passwordVisible" width="500px" :close-on-click-modal="false" @closed="clearPassword">
      <el-form label-position="top" class="workbench-dialog-form">
        <el-form-item label="新密码" required><el-input v-model="newPassword" type="password" autocomplete="new-password" /></el-form-item>
      </el-form>
      <span slot="footer" class="workbench-dialog__actions">
        <el-button :disabled="busy" @click="passwordVisible = false">取消</el-button>
        <el-button type="primary" :loading="busy" @click="savePassword">保存</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { RoleCode } from '../api/generated'
import { getCurrentIdentity } from '../services/identity'
import { PlatformUser, createPlatformUser, disablePlatformUser, enablePlatformUser,
  listPlatformUsers, resetPlatformUserPassword, updatePlatformUser } from '../services/platform-user-api'

interface UserForm { authSubject: string; employeeNo: string; displayName: string; role: RoleCode; password: string }
const emptyForm = (): UserForm => ({ authSubject: '', employeeNo: '', displayName: '', role: 'PLATFORM_USER', password: '' })

/** 管理员维护账户、角色和密码，不在页面保留已提交的密码。 */
export default Vue.extend({
  name: 'PlatformUsers',
  data() {
    return { users: [] as PlatformUser[], loading: false, busy: false, errorMessage: '',
      formVisible: false, editingId: '', form: emptyForm(), passwordVisible: false, passwordUserId: '', newPassword: '' }
  },
  computed: {
    /** 当前管理员标识用于隐藏自我停用入口。 */
    currentUserId(): string { return getCurrentIdentity()?.userId || '' },
  },
  created() { void this.loadUsers() },
  methods: {
    /** 读取用户列表并在失败时保留可见错误。 */
    async loadUsers(): Promise<void> {
      this.loading = true
      this.errorMessage = ''
      try { this.users = await listPlatformUsers() }
      catch (error: unknown) { this.users = []; this.errorMessage = error instanceof Error ? error.message : '用户列表读取失败' }
      finally { this.loading = false }
    },
    /** 以空表单新增用户。 */
    openCreate(): void { this.editingId = ''; this.form = emptyForm(); this.formVisible = true },
    /** 编辑保留账号和工号，仅修改名称和角色。 */
    openEdit(user: PlatformUser): void {
      this.editingId = user.userId
      this.form = { authSubject: user.authSubject, employeeNo: user.employeeNo,
        displayName: user.displayName, role: user.role, password: '' }
      this.formVisible = true
    },
    /** 提交前校验必填项，防止重复提交。 */
    async save(): Promise<void> {
      if (this.busy) return
      if (!this.form.authSubject || !this.form.employeeNo || !this.form.displayName
          || (!this.editingId && (this.form.password.length < 8 || this.form.password.length > 128))) {
        this.notify('请填写完整资料，密码长度为 8 至 128 位', 'warning'); return
      }
      this.busy = true
      try {
        if (this.editingId) await updatePlatformUser(this.editingId,
          { displayName: this.form.displayName, role: this.form.role })
        else await createPlatformUser({ ...this.form })
        this.formVisible = false
        this.clearForm()
        await this.loadUsers()
        this.notify('用户已保存', 'success')
      } catch (error: unknown) { this.notify(error instanceof Error ? error.message : '用户保存失败', 'error') }
      finally { this.busy = false }
    },
    /** 删除按停用执行，先让管理员确认。 */
    async disable(user: PlatformUser): Promise<void> {
      if (this.busy) return
      try { await (this as Vue & { $confirm: Function }).$confirm(`确认停用“${user.displayName}”吗？`, '停用用户',
        { confirmButtonText: '停用', cancelButtonText: '取消', type: 'warning' }) }
      catch { return }
      this.busy = true
      try { await disablePlatformUser(user.userId); await this.loadUsers(); this.notify('用户已停用', 'success') }
      catch (error: unknown) { this.notify(error instanceof Error ? error.message : '用户停用失败', 'error') }
      finally { this.busy = false }
    },
    /** 恢复停用账号。 */
    async enable(user: PlatformUser): Promise<void> {
      if (this.busy) return
      this.busy = true
      try { await enablePlatformUser(user.userId); await this.loadUsers(); this.notify('用户已启用', 'success') }
      catch (error: unknown) { this.notify(error instanceof Error ? error.message : '用户启用失败', 'error') }
      finally { this.busy = false }
    },
    /** 打开密码重置表单，不读取旧密码。 */
    openPassword(user: PlatformUser): void { this.passwordUserId = user.userId; this.newPassword = ''; this.passwordVisible = true },
    /** 设置新密码后立即清除页面中的明文。 */
    async savePassword(): Promise<void> {
      if (this.busy) return
      if (this.newPassword.length < 8 || this.newPassword.length > 128) {
        this.notify('密码长度为 8 至 128 位', 'warning'); return
      }
      this.busy = true
      try { await resetPlatformUserPassword(this.passwordUserId, this.newPassword)
        this.passwordVisible = false; this.clearPassword(); this.notify('密码已重置', 'success') }
      catch (error: unknown) { this.notify(error instanceof Error ? error.message : '密码重置失败', 'error') }
      finally { this.busy = false }
    },
    /** 关闭弹窗时清空待提交资料与密码。 */
    clearForm(): void { this.editingId = ''; this.form = emptyForm() },
    /** 清空密码弹窗中的敏感输入。 */
    clearPassword(): void { this.passwordUserId = ''; this.newPassword = '' },
    /** 将角色转换为中文展示名。 */
    roleLabel(role: RoleCode): string { return role === 'PLATFORM_ADMIN' ? '平台管理员' : '普通用户' },
    /** 在页面反馈操作结果。 */
    notify(message: string, type: 'success' | 'warning' | 'error'): void {
      ;(this as Vue & { $message: Function }).$message({ message, type })
    },
  },
})
</script>
