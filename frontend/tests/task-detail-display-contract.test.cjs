/** 任务详情只读响应契约测试，覆盖名称投影及旧响应兼容。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { parseTaskDetail } = require('../src/api/generated/index.ts')

const base = { taskId: 'task-1', title: '核对凭证', description: '核对业务说明', status: 'DRAFT', initiatorUserId: 'user-1', createdAt: '2026-11-01T00:00:00Z', premises: [], scope: null, plans: [], evidence: [] }

test('任务详情解析发起人、业务场景和数据库实例的展示名称', () => {
  const detail = parseTaskDetail({ ...base, initiatorName: '张三', businessSceneName: '凭证核对', databaseInstanceName: '共享库' })
  assert.equal(detail.initiatorName, '张三')
  assert.equal(detail.businessSceneName, '凭证核对')
  assert.equal(detail.databaseInstanceName, '共享库')
})

test('任务详情允许无关联与旧响应，拒绝错误字段类型', () => {
  const detail = parseTaskDetail(base)
  assert.equal(detail.initiatorName, '—')
  assert.equal(detail.businessSceneName, null)
  assert.equal(detail.databaseInstanceName, null)
  assert.throws(() => parseTaskDetail({ ...base, initiatorName: 42 }))
})
