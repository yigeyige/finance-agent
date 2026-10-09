<template>
  <section class="evidence-page list-page">
    <div class="page-heading"><h2>大模型配置</h2><el-button type="primary" @click="openCreate">新增模型</el-button></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <div v-else class="standard-table-panel"><el-table v-loading="loading" :data="items" row-key="configId" border stripe empty-text="暂无大模型配置">
      <el-table-column prop="name" label="配置名称" min-width="140" /><el-table-column prop="provider" label="提供方" width="120" />
      <el-table-column prop="modelName" label="模型名称" min-width="140" /><el-table-column prop="reasoningEffort" label="推理强度" width="120"><template slot-scope="scope">{{ scope.row.reasoningEffort || '默认' }}</template></el-table-column>
      <el-table-column label="状态" width="110"><template slot-scope="scope"><el-tag :type="scope.row.enabled ? 'success' : 'info'">{{ scope.row.enabled ? '已启用' : '未启用' }}</el-tag></template></el-table-column>
      <el-table-column label="密钥" width="100"><template slot-scope="scope">{{ scope.row.apiKeyConfigured ? '已配置' : '未配置' }}</template></el-table-column>
      <el-table-column label="操作" width="170"><template slot-scope="scope"><el-button type="text" :disabled="scope.row.enabled" @click="enable(scope.row)">启用</el-button><el-button type="text" @click="openEdit(scope.row)">编辑</el-button></template></el-table-column>
    </el-table></div>
    <el-dialog :title="editing ? '编辑大模型' : '新增大模型'" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false"><el-form :model="form" label-width="100px">
      <el-form-item label="配置名称" required><el-input v-model.trim="form.name" maxlength="100" /></el-form-item><el-form-item label="提供方"><el-select v-model="form.provider" disabled><el-option label="DeepSeek" value="DEEPSEEK" /></el-select></el-form-item>
      <el-form-item label="API 地址" required><el-input v-model.trim="form.apiUrl" placeholder="例如：https://api.deepseek.com" /></el-form-item><el-form-item label="模型名称" required><el-input v-model.trim="form.modelName" placeholder="例如：deepseek-chat" /></el-form-item>
      <el-form-item label="推理强度"><el-select v-model="form.reasoningEffort" clearable><el-option label="低" value="low"/><el-option label="中" value="medium"/><el-option label="高" value="high"/></el-select></el-form-item>
      <el-form-item :label="editing ? '更新密钥' : 'API Key'" required><el-input v-model.trim="form.apiKey" type="password" show-password autocomplete="new-password" :placeholder="editing ? '留空则保留原密钥' : '仅保存，后续不再展示'" /></el-form-item>
    </el-form><span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></span></el-dialog>
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { ModelConnectionConfig, ModelConnectionConfigPayload, createModelConfiguration, enableModelConfiguration, listModelConfigurations, updateModelConfiguration } from '../services/model-config-api'
interface Form extends ModelConnectionConfigPayload { apiKey: string }
const blank = (): Form => ({ name: '', provider: 'DEEPSEEK', apiUrl: 'https://api.deepseek.com', modelName: '', reasoningEffort: '', apiKey: '' })
/** 平台管理员维护数据库中的大模型连接信息，密钥只可写入不可回显。 */
export default Vue.extend({ name: 'ModelConfiguration', data: () => ({ items: [] as ModelConnectionConfig[], loading: false, saving: false, errorMessage: '', dialogVisible: false, editing: '' as string, form: blank() }), created() { void this.load() }, methods: {
  /** 加载可维护的模型连接。 */ async load(): Promise<void> { this.loading = true; try { this.items = await listModelConfigurations() } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : '大模型配置暂时无法读取' } finally { this.loading = false } },
  /** 打开新增表单。 */ openCreate(): void { this.editing = ''; this.form = blank(); this.dialogVisible = true },
  /** 打开编辑表单并确保密钥不回显。 */ openEdit(item: ModelConnectionConfig): void { this.editing = item.configId; this.form = { name: item.name, provider: item.provider, apiUrl: item.apiUrl, modelName: item.modelName, reasoningEffort: item.reasoningEffort || '', apiKey: '' }; this.dialogVisible = true },
  /** 保存配置及可选的新密钥。 */ async save(): Promise<void> { if (!this.form.name || !this.form.apiUrl || !this.form.modelName || (!this.editing && !this.form.apiKey)) { this.$message.warning('请填写名称、API 地址、模型名称和 API Key'); return } this.saving = true; try { if (this.editing) await updateModelConfiguration(this.editing, this.form); else await createModelConfiguration(this.form); this.dialogVisible = false; await this.load(); this.$message.success('大模型配置已保存') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : '大模型配置保存失败') } finally { this.saving = false } },
  /** 启用选中模型作为唯一运行时连接。 */ async enable(item: ModelConnectionConfig): Promise<void> { try { await enableModelConfiguration(item.configId); await this.load(); this.$message.success('已启用该大模型') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : '启用失败') } },
} })
</script>
