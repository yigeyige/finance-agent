<!-- 当前管理员最近任务的标准数据列表，只展示当前可信身份可见的任务。 -->
<template>
  <section class="evidence-page list-page">
    <div class="page-heading">
      <h2>任务列表</h2>
      <el-button type="primary" @click="$router.push('/')">新建业务处理</el-button>
    </div>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" show-icon />

    <div v-else class="standard-table-panel">
      <el-form :inline="true" class="task-list-filters" @submit.native.prevent="searchTasks">
        <el-form-item label="业务标题"><el-input v-model.trim="filters.keyword" maxlength="100" clearable placeholder="输入标题关键字" @keyup.enter.native="searchTasks" /></el-form-item>
        <el-form-item label="处理状态"><el-select v-model="filters.status" clearable placeholder="全部状态"><el-option v-for="status in statuses" :key="status" :label="statusLabel(status)" :value="status" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="searchTasks">查询</el-button><el-button @click="resetFilters">重置</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tasks" row-key="taskId" class="standard-data-table" border stripe
        @row-dblclick="openDetail"
        empty-text="暂无任务">
        <el-table-column prop="title" label="业务标题" min-width="320" show-overflow-tooltip />
        <el-table-column label="处理状态" width="168">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" effect="plain">{{ statusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="initiatorUserId" label="发起人标识" min-width="210" show-overflow-tooltip />
        <el-table-column label="创建时间" width="190">
          <template slot-scope="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination class="task-list-pagination" background layout="total, sizes, prev, pager, next" :current-page="currentPage" :page-size="pageSize" :page-sizes="[20, 50, 100]" :total="total" @current-change="changePage" @size-change="changePageSize" />
    </div>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

import { listTasks, TaskListPage } from '../services/task-list-api'
import { TaskListItem } from '../api/generated'

type StatusTagType = 'success' | 'warning' | 'danger' | 'info' | ''

const statusLabels: Record<string, string> = {
  DRAFT: '草稿',
  WAITING_SCOPE_CONFIRMATION: '待确认范围',
  READY: '待生成方案',
  WAITING_STEP_APPROVAL: '待处理',
  RUNNING_STEP: '处理中',
  ANALYZING: '分析中',
  WAITING_PROFESSIONAL_CONFIRMATION: '待专业确认',
  DRAFT_PACKET: '决策包草稿',
  WAITING_REVIEW: '待复核',
  RECHECKING_SCOPE: '重新核查范围',
  READY_TO_PUBLISH: '待发布',
  PUBLISHED: '已发布',
  HANDED_OFF: '已移交',
  CLOSED: '已关闭',
  PAUSED_MODEL_UNAVAILABLE: '模型暂不可用',
  PAUSED_BUDGET: '预算已暂停',
  PAUSED_SOURCE_UNAVAILABLE: '来源暂不可用',
  STALE_APPROVAL: '审批已失效',
  FAILED_RECOVERABLE: '处理失败，可重试',
  CANCELLED: '已取消',
}

/** 展示当前管理员最近任务的标准数据表格。 */
export default Vue.extend({
  name: 'TaskList',
  data() {
    return {
      tasks: [] as TaskListItem[],
      total: 0,
      currentPage: 1,
      pageSize: 20,
      filters: { keyword: '', status: '' },
      loading: false,
      errorMessage: '',
    }
  },
  created() {
    void this.loadTasks()
  },
  computed: {
    statuses(): string[] { return Object.keys(statusLabels) },
  },
  methods: {
    /** 加载当前身份范围内的任务页，并在失败时保留页面错误反馈。 */
    async loadTasks() {
      this.loading = true
      this.errorMessage = ''
      try {
        const result: TaskListPage = await listTasks({ page: this.currentPage, pageSize: this.pageSize, status: this.filters.status || undefined, keyword: this.filters.keyword || undefined })
        this.tasks = result.items
        this.total = result.total
      } catch (error: unknown) {
        this.errorMessage = error instanceof Error ? error.message : '任务列表暂时无法读取'
      } finally {
        this.loading = false
      }
    },
    /** 使用当前筛选条件从第一页重新查询。 */
    searchTasks() { this.currentPage = 1; void this.loadTasks() },
    /** 清除筛选条件并恢复第一页。 */
    resetFilters() { this.filters = { keyword: '', status: '' }; this.currentPage = 1; void this.loadTasks() },
    /** 切换任务页码。 */
    changePage(page: number) { this.currentPage = page; void this.loadTasks() },
    /** 调整每页条数并回到第一页。 */
    changePageSize(pageSize: number) { this.pageSize = pageSize; this.currentPage = 1; void this.loadTasks() },
    /** 将服务端任务状态转为业务可读中文文案。 */
    statusLabel(status: string) {
      return statusLabels[status] || '状态待确认'
    },
    /** 根据任务状态提供辅助标签语义，不仅依赖颜色表达状态。 */
    statusTagType(status: string): StatusTagType {
      if (['PUBLISHED', 'HANDED_OFF', 'CLOSED'].includes(status)) return 'success'
      if (['CANCELLED', 'FAILED_RECOVERABLE'].includes(status)) return 'danger'
      if (status.startsWith('PAUSED_') || status === 'STALE_APPROVAL') return 'warning'
      return 'info'
    },
    /** 按中文地区格式展示接口返回的创建时间。 */
    formatTime(value: string) {
      const date = new Date(value)
      return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString('zh-CN', { hour12: false })
    },
    /** 双击任务行进入只读详情，不在列表页直接修改历史任务。 */
    openDetail(task: TaskListItem) {
      void this.$router.push(`/tasks/${task.taskId}`)
    },
  },
})
</script>
<style scoped>
.task-list-filters{margin-bottom:16px}.task-list-pagination{margin-top:18px;text-align:right}
</style>
