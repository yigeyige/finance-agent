<!-- 业务工作区页面，复用公共表格与弹窗交互，保持既有业务流程。 -->
<template>
  <section class="evidence-page list-page entry-page">
    <div class="page-heading entry-page-heading database-page-heading"><h2>数据库实例</h2><el-alert class="database-page-warning" title="仅用于业务处理模型的受控只读分析。系统代码拦截非 SELECT、多语句、写入、DDL 和危险语法；数据库账号仍应只授予业务库 SELECT 权限。" type="warning" :closable="false" show-icon /></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <section class="entry-content-card">
      <div class="entry-content-toolbar"><span class="entry-content-toolbar__meta">共 {{ items.length }} 项</span><el-button type="primary" @click="openCreate">新增连接</el-button></div>
      <div class="standard-table-panel model-config-table"><el-table v-workbench-table="'EmsDatabaseConfiguration-1'" v-loading="loading" :data="items" row-key="configId" class="standard-data-table" border stripe empty-text="暂无 EMS 数据库配置">
        <el-table-column show-overflow-tooltip prop="name" label="配置名称" min-width="140" /><el-table-column show-overflow-tooltip prop="username" label="数据库账号" min-width="130" /><el-table-column show-overflow-tooltip label="超时" width="160"><template slot-scope="scope">连接 {{ scope.row.connectTimeoutSeconds }} 秒 / 查询 {{ scope.row.queryTimeoutSeconds }} 秒</template></el-table-column><el-table-column show-overflow-tooltip prop="maxRows" label="最大返回行数" width="120" />
        <el-table-column show-overflow-tooltip label="状态" width="110"><template slot-scope="scope"><el-tag :type="scope.row.enabled ? 'success' : 'info'">{{ scope.row.enabled ? '已启用' : '未启用' }}</el-tag></template></el-table-column><el-table-column show-overflow-tooltip label="密码" width="100"><template slot-scope="scope">{{ scope.row.passwordConfigured ? '已配置' : '未配置' }}</template></el-table-column>
        <el-table-column label="结构目录" min-width="120"><template slot-scope="scope"><el-button v-if="scope.row.schemaSyncedAt" type="text" @click="openCatalog(scope.row)">{{ scope.row.schemaTableCount }} 张表</el-button><span v-else>未同步</span></template></el-table-column>
        <el-table-column label="操作" min-width="280"><template slot-scope="scope"><el-button type="text" :disabled="scope.row.enabled" @click="enable(scope.row)">启用</el-button><el-button type="text" :disabled="!scope.row.enabled" @click="disable(scope.row)">停用</el-button><el-button type="text" @click="test(scope.row)">测试</el-button><el-button type="text" :loading="syncingId === scope.row.configId" :disabled="Boolean(syncingId)" @click="syncSchema(scope.row)">同步表结构</el-button><el-button type="text" @click="openEdit(scope.row)">编辑</el-button></template></el-table-column>
      </el-table></div>
    </section>
    <el-dialog v-workbench-dialog custom-class="workbench-dialog" :title="editing ? '编辑 EMS 数据库连接' : '新增 EMS 数据库连接'" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false"><el-form :model="form" label-position="top" class="workbench-dialog-form">
      <el-form-item label="配置名称" required><el-input v-model.trim="form.name" maxlength="100" /></el-form-item><el-form-item label="JDBC 地址" required><el-input v-model.trim="form.jdbcUrl" placeholder="例如：jdbc:mysql://主机:3306/数据库" /></el-form-item><el-form-item label="数据库账号" required><el-input v-model.trim="form.username" maxlength="200" /></el-form-item><el-form-item :label="editing ? '更新密码' : '数据库密码'" required><el-input v-model.trim="form.password" type="password" show-password autocomplete="new-password" :placeholder="editing ? '留空则保留原密码' : '仅加密保存，后续不再展示'" /></el-form-item>
      <el-form-item label="连接超时（秒）"><el-input-number v-model="form.connectTimeoutSeconds" :min="1" :max="30" /></el-form-item><el-form-item label="查询超时（秒）"><el-input-number v-model="form.queryTimeoutSeconds" :min="1" :max="60" /></el-form-item><el-form-item label="最大返回行数"><el-input-number v-model="form.maxRows" :min="1" :max="100" /></el-form-item>
    </el-form><span slot="footer" class="workbench-dialog__actions"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></span></el-dialog>
    <ems-schema-catalog-dialog :visible="catalogVisible" :instance-id="catalogInstance.configId" :instance-name="catalogInstance.name" @close="catalogVisible = false" />
  </section>
</template>
<script lang="ts">
import Vue from 'vue'
import { EmsDatabaseConnectionConfig, EmsDatabaseConnectionConfigPayload, createEmsDatabaseConfiguration, disableEmsDatabaseConfiguration, enableEmsDatabaseConfiguration, listEmsDatabaseConfigurations, syncEmsDatabaseSchema, testEmsDatabaseConfiguration, updateEmsDatabaseConfiguration } from '../services/ems-database-config-api'
import EmsSchemaCatalogDialog from './EmsSchemaCatalogDialog.vue'
interface Form extends EmsDatabaseConnectionConfigPayload { password: string }
const blank = (): Form => ({ name: '', jdbcUrl: '', username: '', password: '', connectTimeoutSeconds: 5, queryTimeoutSeconds: 10, maxRows: 20 })
/** 平台管理员维护模型分析使用的 EMS MySQL 只读连接。 */
export default Vue.extend({ name: 'EmsDatabaseConfiguration', components: { EmsSchemaCatalogDialog }, data: () => ({ items: [] as EmsDatabaseConnectionConfig[], loading: false, saving: false, syncingId: '', errorMessage: '', dialogVisible: false, catalogVisible: false, catalogInstance: { configId: '', name: '' }, editing: '' as string, form: blank() }), created() { void this.load() }, methods: {
  /** 加载 EMS 数据库连接。 */ async load(): Promise<void> { this.loading = true; try { this.items = await listEmsDatabaseConfigurations(); this.errorMessage = '' } catch (error: unknown) { this.errorMessage = error instanceof Error ? error.message : 'EMS 数据库配置暂时无法读取' } finally { this.loading = false } },
  /** 打开新增表单。 */ openCreate(): void { this.editing = ''; this.form = blank(); this.dialogVisible = true },
  /** 打开编辑表单，密码不回填。 */ openEdit(item: EmsDatabaseConnectionConfig): void { this.editing = item.configId; this.form = { name: item.name, jdbcUrl: item.jdbcUrl, username: item.username, password: '', connectTimeoutSeconds: item.connectTimeoutSeconds, queryTimeoutSeconds: item.queryTimeoutSeconds, maxRows: item.maxRows }; this.dialogVisible = true },
  /** 保存连接配置。 */ async save(): Promise<void> { if (!this.form.name || !this.form.jdbcUrl || !this.form.username || (!this.editing && !this.form.password)) { this.$message.warning('请填写名称、JDBC 地址、账号和数据库密码'); return } this.saving = true; try { if (this.editing) await updateEmsDatabaseConfiguration(this.editing, this.form); else await createEmsDatabaseConfiguration(this.form); this.dialogVisible = false; await this.load(); this.$message.success('EMS 数据库配置已保存') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : 'EMS 数据库配置保存失败') } finally { this.saving = false } },
  /** 启用一个数据库实例。 */ async enable(item: EmsDatabaseConnectionConfig): Promise<void> { try { await enableEmsDatabaseConfiguration(item.configId); await this.load(); this.$message.success('已启用数据库实例') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : '启用失败') } },
  /** 停用当前数据库实例。 */ async disable(item: EmsDatabaseConnectionConfig): Promise<void> { try { await this.$confirm(`停用“${item.name}”后，该实例将不再被数据探索和业务处理模型使用。`, '确认停用数据库实例', { confirmButtonText: '确认停用', cancelButtonText: '取消', type: 'warning' }); await disableEmsDatabaseConfiguration(item.configId); await this.load(); this.$message.success('已停用数据库实例') } catch (error: unknown) { if (error === 'cancel' || error === 'close') return; this.$message.error(error instanceof Error ? error.message : '停用失败') } },
  /** 仅执行固定 SELECT 1 连通性测试。 */ async test(item: EmsDatabaseConnectionConfig): Promise<void> { try { await testEmsDatabaseConfiguration(item.configId); this.$message.success('EMS 数据库连接测试通过') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : 'EMS 数据库连接测试失败') } },
  /** 打开指定实例的本地结构目录详情。 */ openCatalog(item: EmsDatabaseConnectionConfig): void { this.catalogInstance = { configId: item.configId, name: item.name }; this.catalogVisible = true },
  /** 手动同步选中实例的只读表字段目录，并阻止重复提交。 */ async syncSchema(item: EmsDatabaseConnectionConfig): Promise<void> { if (this.syncingId) return; this.syncingId = item.configId; try { await syncEmsDatabaseSchema(item.configId); await this.load(); this.$message.success('表结构目录已同步') } catch (error: unknown) { this.$message.error(error instanceof Error ? error.message : '表结构同步失败，原目录已保留') } finally { this.syncingId = '' } },
} })
</script>
<style scoped>
.database-page-heading { display: flex; align-items: center; flex-wrap: wrap; }
.database-page-heading h2 { flex: none; margin: 0; }
.database-page-warning { flex: 1 1 660px; min-width: 0; padding: 6px 10px; }
.database-page-warning ::v-deep .el-alert__title { font-size: 14px; line-height: 20px; }
.model-config-table { overflow-x: auto; }
@media (max-width: 900px) { .database-page-warning { flex-basis: 100%; } }
</style>
