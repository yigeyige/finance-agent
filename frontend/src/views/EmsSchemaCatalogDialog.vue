<!-- 数据库实例结构目录详情：按需读取本地快照，左右切换表与字段，不展示业务行值。 -->
<template>
  <el-dialog v-workbench-dialog custom-class="workbench-dialog" :title="`${instanceName} · 结构目录`" :visible="visible" width="900px" :close-on-click-modal="false" @close="$emit('close')">
    <div v-loading="loading" class="schema-catalog-dialog">
      <div v-if="errorMessage" class="schema-catalog-dialog__state">
        <el-alert :title="errorMessage" type="error" :closable="false" show-icon />
        <el-button @click="load">重试</el-button>
      </div>
      <div v-else-if="!loading && catalog && catalog.tables.length === 0" class="schema-catalog-dialog__state">当前结构目录没有符合条件的表</div>
      <div v-else-if="catalog" class="schema-catalog-dialog__layout">
        <section class="schema-catalog-dialog__list" aria-label="表目录">
          <div class="schema-catalog-dialog__list-header">表目录 · {{ filteredTables.length }} 张</div>
          <el-input v-model.trim="keyword" clearable placeholder="搜索表名或注释" aria-label="搜索表名或注释" />
          <div class="schema-catalog-dialog__items">
            <button v-for="table in filteredTables" :key="table.schema + '.' + table.table" type="button" class="schema-catalog-dialog__item" :class="{ 'is-active': selectedTable === table }" :aria-pressed="selectedTable === table" @click="selectedKey = table.schema + '.' + table.table">
              <span class="schema-catalog-dialog__table-name">{{ table.table }}</span>
              <span class="schema-catalog-dialog__table-schema">{{ table.schema }}</span>
              <span v-if="table.comment" class="schema-catalog-dialog__table-comment">{{ table.comment }}</span>
            </button>
            <div v-if="filteredTables.length === 0" class="schema-catalog-dialog__empty">没有匹配的表</div>
          </div>
        </section>
        <section class="schema-catalog-dialog__detail" aria-label="字段结构">
          <template v-if="selectedTable">
            <div class="schema-catalog-dialog__detail-header">
              <div class="schema-catalog-dialog__detail-title">{{ selectedTable.schema }}.{{ selectedTable.table }}</div>
              <el-tag type="info">{{ selectedTable.columns.length }} 个字段</el-tag>
            </div>
            <div v-if="selectedTable.comment" class="schema-catalog-dialog__detail-comment">{{ selectedTable.comment }}</div>
            <el-table :data="selectedTable.columns" row-key="name" border stripe class="standard-data-table schema-catalog-dialog__fields" height="400" empty-text="该表没有字段">
              <el-table-column prop="name" label="字段名" min-width="150" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" min-width="135" show-overflow-tooltip />
              <el-table-column prop="comment" label="字段注释" min-width="190" show-overflow-tooltip />
            </el-table>
          </template>
          <div v-else class="schema-catalog-dialog__empty">请选择左侧表查看字段结构</div>
        </section>
      </div>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import Vue from 'vue'
import { EmsSchemaCatalog, getEmsDatabaseSchemaCatalog } from '../services/ems-database-config-api'
import { EmsSchemaTable } from '../api/generated'

/** 管理员按实例查看已保存的表字段目录。 */
export default Vue.extend({
  name: 'EmsSchemaCatalogDialog',
  props: { visible: { type: Boolean, required: true }, instanceId: { type: String, required: true }, instanceName: { type: String, required: true } },
  data: () => ({ catalog: null as EmsSchemaCatalog | null, loading: false, errorMessage: '', keyword: '', selectedKey: '', requestSerial: 0 }),
  computed: {
    /** 按表名、库名或表注释在当前快照中本地筛选。 */
    filteredTables(): EmsSchemaTable[] {
      const term = this.keyword.toLocaleLowerCase()
      return this.catalog?.tables.filter((table) => `${table.schema}.${table.table} ${table.comment}`.toLocaleLowerCase().includes(term)) ?? []
    },
    /** 优先保持管理员已选表；筛选后自动显示首张匹配表。 */
    selectedTable(): EmsSchemaTable | null {
      return this.filteredTables.find((table) => `${table.schema}.${table.table}` === this.selectedKey) ?? this.filteredTables[0] ?? null
    },
  },
  watch: {
    /** 弹窗打开时读取一次快照，关闭时使未完成请求失效。 */
    visible(value: boolean): void {
      if (value) void this.load()
      else { this.requestSerial++; this.catalog = null; this.keyword = ''; this.selectedKey = '' }
    },
  },
  methods: {
    /** 按需读取本地目录，忽略关闭或切换实例后的过期响应。 */
    async load(): Promise<void> {
      const serial = ++this.requestSerial
      this.loading = true
      this.errorMessage = ''
      this.catalog = null
      try {
        const catalog = await getEmsDatabaseSchemaCatalog(this.instanceId)
        if (serial !== this.requestSerial || !this.visible) return
        this.catalog = catalog
        this.selectedKey = catalog.tables.length ? `${catalog.tables[0].schema}.${catalog.tables[0].table}` : ''
      } catch (error: unknown) {
        if (serial !== this.requestSerial || !this.visible) return
        this.errorMessage = error instanceof Error ? error.message : '结构目录读取失败'
      } finally {
        if (serial === this.requestSerial) this.loading = false
      }
    },
  },
})
</script>
<style scoped>
.schema-catalog-dialog { min-height: 440px; }
.schema-catalog-dialog__layout { display: grid; grid-template-columns: minmax(230px, 31%) minmax(0, 1fr); gap: 14px; min-height: 440px; }
.schema-catalog-dialog__list, .schema-catalog-dialog__detail { min-width: 0; padding: 14px; border: 1px solid #e7dfd4; border-radius: 10px; background: #fffdf9; }
.schema-catalog-dialog__list { display: flex; flex-direction: column; gap: 10px; }
.schema-catalog-dialog__list-header { color: #4a4640; font-weight: 600; }
.schema-catalog-dialog__items { flex: 1; max-height: 400px; overflow-y: auto; }
.schema-catalog-dialog__item { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 2px 8px; width: 100%; margin-bottom: 5px; padding: 9px 10px; border: 1px solid transparent; border-radius: 7px; background: transparent; color: #3d3933; font: inherit; text-align: left; cursor: pointer; }
.schema-catalog-dialog__item:hover, .schema-catalog-dialog__item.is-active { border-color: #e9c99e; background: #fff4e5; }
.schema-catalog-dialog__item:focus-visible { outline: 2px solid #c87422; outline-offset: -2px; }
.schema-catalog-dialog__table-name { overflow: hidden; font-weight: 600; text-overflow: ellipsis; white-space: nowrap; }
.schema-catalog-dialog__table-schema { color: #736b61; }
.schema-catalog-dialog__table-comment { grid-column: 1 / -1; overflow: hidden; color: #5d554c; text-overflow: ellipsis; white-space: nowrap; }
.schema-catalog-dialog__detail-header { display: flex; align-items: center; justify-content: space-between; gap: 10px; margin-bottom: 8px; }
.schema-catalog-dialog__detail-title { overflow-wrap: anywhere; color: #2e2c29; font-weight: 600; }
.schema-catalog-dialog__detail-comment { margin-bottom: 10px; color: #5d554c; }
.schema-catalog-dialog__fields { width: 100%; }
.schema-catalog-dialog__empty, .schema-catalog-dialog__state { display: flex; align-items: center; justify-content: center; min-height: 200px; color: #665e54; }
.schema-catalog-dialog__state { flex-direction: column; gap: 14px; }
.schema-catalog-dialog__state .el-alert { width: 100%; }
@media (max-width: 680px) { .schema-catalog-dialog__layout { grid-template-columns: minmax(0, 1fr); } .schema-catalog-dialog__items { max-height: 180px; } }
</style>
