/** 任务列表只读响应契约测试，覆盖新增展示字段及敏感配置拒绝。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { parseTaskList } = require('../src/api/generated/index.ts')

test('任务列表解析发起人名称及业务上下文', () => {
  const item = { taskId: 'task-1', title: '核对凭证', description: '核对跨月凭证差异', status: 'DRAFT', initiatorUserId: 'user-1', initiatorName: '张三', businessSceneName: '凭证核对', databaseInstanceName: '共享库', createdAt: '2026-11-01T00:00:00Z' }
  assert.deepEqual(parseTaskList({ items: [item], total: 1, page: 1, pageSize: 10 }).items[0], item)
  assert.throws(() => parseTaskList({ items: [{ ...item, jdbcUrl: 'jdbc:mysql://secret' }], total: 1, page: 1, pageSize: 10 }))
})

test('任务列表允许未关联业务场景和数据库实例', () => {
  const item = { taskId: 'task-2', title: '查账', description: '查账说明', status: 'DRAFT', initiatorUserId: 'user-2', initiatorName: '李四', businessSceneName: null, databaseInstanceName: null, createdAt: '2026-11-01T00:00:00Z' }
  assert.deepEqual(parseTaskList({ items: [item], total: 1, page: 1, pageSize: 10 }).items[0], item)
})

test('旧版任务列表响应缺少新增字段时仍可展示基础记录', () => {
  const legacy = { taskId: 'task-3', title: '查账', status: 'DRAFT', initiatorUserId: 'user-3', createdAt: '2026-11-01T00:00:00Z' }
  const item = parseTaskList({ items: [legacy], total: 1, page: 1, pageSize: 10 }).items[0]
  assert.equal(item.description, '—')
  assert.equal(item.initiatorName, '—')
  assert.equal(item.businessSceneName, null)
  assert.equal(item.databaseInstanceName, null)
  assert.throws(() => parseTaskList({ items: [{ ...legacy, description: 42 }], total: 1, page: 1, pageSize: 10 }))
  assert.throws(() => parseTaskList({ items: [{ ...legacy, initiatorName: 42 }], total: 1, page: 1, pageSize: 10 }))
})
