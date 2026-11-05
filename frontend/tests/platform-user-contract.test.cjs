/** 用户管理响应边界测试：角色与状态受控，额外敏感字段不可进入界面。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { parsePlatformUser } = require('../src/api/generated/index.ts')

const row = { userId: '00000000-0000-4000-8000-000000000100', authSubject: 'worker', employeeNo: 'W001',
  displayName: '业务人员', role: 'PLATFORM_USER', status: 'ACTIVE', createdAt: '2026-11-05T00:00:00Z' }

test('用户响应只保留安全账户信息', () => {
  assert.deepEqual(parsePlatformUser(row), row)
})

test('用户响应含密码或额外字段时拒绝展示', () => {
  for (const value of [{ ...row, passwordHash: 'hidden' }, { ...row, apiKey: 'hidden' },
    { ...row, role: 'VIEWER' }]) assert.throws(() => parsePlatformUser(value))
})
