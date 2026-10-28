<template>
  <section class="data-exploration-page">
    <div class="page-heading entry-page-heading"><h2>数据探索</h2></div>
    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />
    <section class="data-exploration-shell" v-loading="loading">
      <aside class="data-exploration-catalog">
        <div class="catalog-heading"><span>数据库实例</span><el-button type="text" icon="el-icon-refresh" :disabled="loading" @click="loadCatalog">刷新</el-button></div>
        <el-select v-model="selectedInstanceId" size="small" placeholder="选择数据库实例" class="catalog-instance-select" @change="loadCatalog">
          <el-option v-for="instance in instances" :key="instance.configId" :label="instance.name" :value="instance.configId" />
        </el-select>
        <template v-if="catalog"><p class="catalog-source">{{ catalog.dataSourceName }}</p><el-tag type="success" size="mini">只读</el-tag></template>
        <p v-else class="catalog-empty">未读取到可用数据源</p>
        <el-input v-model.trim="tableKeyword" prefix-icon="el-icon-search" clearable placeholder="搜索表名或中文注释" class="catalog-search" />
        <el-tree v-if="catalog" :data="schemaTree" node-key="key" :props="treeProps" :expand-on-click-node="false" @node-click="handleTableClick">
          <span slot-scope="{ data }" class="catalog-node"><span>{{ data.label }}</span><small v-if="data.table">{{ data.table }}</small></span>
        </el-tree>
      </aside>
      <main class="data-exploration-workspace">
        <div class="query-tabs"><el-tabs v-model="activeQueryTab" type="card" class="query-tab-list" @tab-remove="removeQueryTab"><el-tab-pane v-for="tab in queryTabs" :key="tab.id" :label="tab.title" :name="tab.id" :closable="queryTabs.length > 1" /></el-tabs><el-button type="text" icon="el-icon-plus" aria-label="新增查询页签" @click="addQueryTab">新增查询</el-button><span v-if="catalog" class="query-source">数据源：{{ catalog.dataSourceName }}，最终行数受服务端限制</span></div>
        <div class="query-controls"><el-select v-model="requestedRows" size="small" aria-label="请求行数"><el-option v-for="limit in rowLimits" :key="limit" :label="`${limit} 行`" :value="limit" /></el-select><el-button type="primary" size="small" :loading="executing" :disabled="!catalog" @click="executeQuery">执行</el-button></div>
        <el-input :value="activeTab.sql" type="textarea" :rows="10" class="sql-input" aria-label="SQL 查询语句" placeholder="仅允许单条、库名限定的 SELECT，例如：SELECT * FROM basedata.sys_register_user LIMIT 20" @input="updateSql" @keydown.native.ctrl.enter="executeQuery" />
        <p class="query-tip">仅支持允许业务库的单条 SELECT；按 Ctrl + Enter 可执行；页面请求 {{ requestedRows }} 行，实际结果受数据源最大行数限制。</p>
        <el-tabs v-model="activeResultTab" class="result-tabs"><el-tab-pane label="查询结果" name="result"><div v-if="activeTab.result" class="result-summary">返回 {{ activeTab.result.rowCount }} 行，用时 {{ activeTab.result.elapsedMillis }} 毫秒</div><el-table v-if="activeTab.result && activeTab.result.columns.length" :data="resultRows" border stripe class="standard-data-table" empty-text="未查询到数据"><el-table-column v-for="column in activeTab.result.columns" :key="column" :prop="column" :label="column" min-width="150" show-overflow-tooltip /></el-table><div v-else class="data-exploration-empty">{{ activeTab.result ? '未查询到数据' : '尚未执行查询' }}</div></el-tab-pane><el-tab-pane label="表结构" name="columns"><template v-if="selectedTable"><div class="result-summary">{{ selectedTable.schema }}.{{ selectedTable.table }}</div><el-table v-loading="columnsLoading" :data="columns" border stripe class="standard-data-table" empty-text="未读取到字段"><el-table-column prop="name" label="字段名" min-width="160" /><el-table-column prop="type" label="类型" min-width="160" /><el-table-column prop="comment" label="注释" min-width="220" /></el-table></template><div v-else class="data-exploration-empty">从左侧选择表以查看字段结构</div></el-tab-pane></el-tabs>
      </main>
    </section>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'
import { DataExplorationCatalog, DataExplorationQueryResult, EmsDatabaseColumnMetadata, EmsDatabaseTableMetadata } from '../services/data-exploration-api'
import { executeDataExplorationQuery, fetchDataExplorationCatalog, fetchDataExplorationColumns } from '../services/data-exploration-api'
import { EmsDatabaseConnectionConfig, listEmsDatabaseConfigurations } from '../services/ems-database-config-api'

interface TreeNode { key: string; label: string; schema?: string; table?: string; children?: TreeNode[] }
interface QueryTab { id: string; title: string; sql: string; result: DataExplorationQueryResult | null }

/** 平台管理员浏览 EMS 元数据并执行受控只读 SQL 的工作台页面。 */
export default Vue.extend({
  name: 'DataExploration',
  data: () => ({ catalog: null as DataExplorationCatalog | null, instances: [] as EmsDatabaseConnectionConfig[], selectedInstanceId: '' as string, loading: false, executing: false, columnsLoading: false, errorMessage: '', tableKeyword: '', requestedRows: 20, rowLimits: [20, 50, 100], activeResultTab: 'result', selectedTable: null as EmsDatabaseTableMetadata | null, columns: [] as EmsDatabaseColumnMetadata[], queryTabs: [{ id: 'query-1', title: '查询 1', sql: '', result: null }] as QueryTab[], activeQueryTab: 'query-1', treeProps: { children: 'children', label: 'label' } }),
  computed: {
    /** 按库组织并筛选当前允许的表目录。 */
    schemaTree(): TreeNode[] { const keyword = this.tableKeyword.toLowerCase(); const tables = this.catalog?.tables.filter((item) => !keyword || `${item.table} ${item.comment}`.toLowerCase().includes(keyword)) || []; const groups = new Map<string, TreeNode>(); tables.forEach((item) => { const group = groups.get(item.schema) || { key: item.schema, label: item.schema, children: [] }; group.children?.push({ key: `${item.schema}.${item.table}`, label: item.comment || item.table, schema: item.schema, table: item.table }); groups.set(item.schema, group) }); return Array.from(groups.values()) },
    /** 将二维查询结果投影给 Element 表格。 */
    resultRows(): Array<Record<string, string | null>> { const result = this.activeTab.result; if (!result) return []; return result.rows.map((row) => result.columns.reduce((item, column, index) => ({ ...item, [column]: row[index] }), {} as Record<string, string | null>)) },
    /** 返回当前激活且保存在浏览器内存中的查询页签。 */
    activeTab(): QueryTab { return this.queryTabs.find((item) => item.id === this.activeQueryTab) || this.queryTabs[0] },
  },
  created() { void this.loadInstances() },
  methods: {
    /** 加载启用实例列表并默认选择第一个启用实例。 */
    async loadInstances(): Promise<void> { this.errorMessage = ''; try { const all = await listEmsDatabaseConfigurations(); this.instances = all.filter((item) => item.enabled); this.selectedInstanceId = this.instances[0]?.configId || '' } catch (error: unknown) { this.instances = []; this.selectedInstanceId = '' } finally { await this.loadCatalog() } },
    /** 刷新所选实例的受控目录。 */
    async loadCatalog(): Promise<void> { this.loading = true; this.selectedTable = null; this.columns = []; try { this.catalog = await fetchDataExplorationCatalog(this.selectedInstanceId || undefined); this.errorMessage = '' } catch (error: unknown) { this.catalog = null; this.errorMessage = error instanceof Error ? error.message : '数据探索目录暂时无法读取' } finally { this.loading = false } },
    /** 选择表后读取结构，不自动查询业务数据。 */
    async handleTableClick(node: TreeNode): Promise<void> { if (!node.schema || !node.table) return; this.selectedTable = { schema: node.schema, table: node.table, comment: node.label }; this.activeResultTab = 'columns'; this.columnsLoading = true; try { this.columns = await fetchDataExplorationColumns(node.schema, node.table, this.selectedInstanceId || undefined); this.errorMessage = '' } catch (error: unknown) { this.columns = []; this.errorMessage = error instanceof Error ? error.message : '表结构暂时无法读取' } finally { this.columnsLoading = false } },
    /** 在浏览器内存中新建一个空查询页签，不持久化 SQL 或结果。 */
    addQueryTab(): void { if (this.queryTabs.length >= 5) { this.$message.warning('最多同时保留 5 个查询页签'); return } const id = `query-${Date.now()}`; this.queryTabs.push({ id, title: `查询 ${this.queryTabs.length + 1}`, sql: '', result: null }); this.activeQueryTab = id },
    /** 关闭非唯一查询页签并切换到仍可用的页签。 */
    removeQueryTab(id: string): void { if (this.queryTabs.length === 1) return; const index = this.queryTabs.findIndex((item) => item.id === id); this.queryTabs.splice(index, 1); if (this.activeQueryTab === id) this.activeQueryTab = this.queryTabs[Math.max(0, index - 1)].id },
    /** 同步当前页签的输入内容，避免在切换时丢失。 */
    updateSql(value: string): void { this.activeTab.sql = value },
    /** 提交单条 SELECT；服务端是 SQL 合规性和结果范围的最终权威。 */
    async executeQuery(): Promise<void> { if (this.executing || !this.activeTab.sql.trim()) { this.$message.warning('请先输入单条 SELECT 查询'); return } this.executing = true; try { this.activeTab.result = await executeDataExplorationQuery(this.withLimit(this.activeTab.sql.trim()), this.selectedInstanceId || undefined); this.activeResultTab = 'result'; this.errorMessage = '' } catch (error: unknown) { this.activeTab.result = null; this.errorMessage = error instanceof Error ? error.message : '查询暂时不可用' } finally { this.executing = false } },
    /** 对未包含 LIMIT 的查询追加页面请求行数，不覆盖用户已明确指定的限制。 */
    withLimit(value: string): string { return /\blimit\s+\d+/i.test(value) ? value : `${value} LIMIT ${this.requestedRows}` },
  },
})
</script>

<style scoped>
.data-exploration-shell { display: flex; min-height: 680px; border: 1px solid #e8dfd5; border-radius: 10px; background: #fffdfa; overflow: hidden; box-shadow: 0 8px 20px rgba(73, 49, 30, .05); }
.data-exploration-catalog { width: 270px; flex: 0 0 270px; padding: 18px; border-right: 1px solid #e8dfd5; background: #fffaf4; }
.catalog-heading, .query-tabs, .query-controls { display: flex; align-items: center; justify-content: space-between; gap: 10px; }.catalog-heading { font-weight: 700; }.catalog-instance-select { width: 100%; margin-top: 12px; }.catalog-source { display: inline-block; margin: 12px 8px 12px 0; color: #4d392b; }.catalog-empty, .query-tip, .data-exploration-empty { color: #8d8177; }.catalog-search { margin: 20px 0 12px; }.catalog-node { display: flex; flex-direction: column; min-width: 0; }.catalog-node small { overflow: hidden; color: #96877c; text-overflow: ellipsis; white-space: nowrap; }
.data-exploration-workspace { min-width: 0; flex: 1; padding: 18px 20px; }.query-tabs { min-height: 34px; border-bottom: 1px solid #eee5dc; }.query-tab-list { flex: 1; margin-bottom: -1px; }.query-source { color: #8d8177; font-size: 13px; }.query-controls { justify-content: flex-end; margin: 14px 0; }.sql-input :deep(textarea) { min-height: 220px; border-color: #e0d2c4; background: #fff; color: #493428; font-family: Consolas, 'Courier New', monospace; line-height: 1.7; }.query-tip { margin: 8px 0 16px; font-size: 13px; }.result-tabs { border-top: 1px solid #eee5dc; }.result-summary { margin: 8px 0 12px; color: #806348; font-size: 13px; }.data-exploration-empty { display: grid; min-height: 220px; place-items: center; }
@media (max-width: 900px) { .data-exploration-shell { display: block; }.data-exploration-catalog { width: auto; border-right: 0; border-bottom: 1px solid #e8dfd5; }.data-exploration-workspace { padding: 14px; } }
</style>
