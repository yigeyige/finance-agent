/** 模型运行摘要严格解析测试，防止配置详情混入工作台公开响应。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { parseModelRuntimeSummary } = require('../src/api/generated/index.ts')

test('运行摘要解析真实、模拟与未启用模型', () => {
  for (const value of [{ mode: 'LIVE', modelName: 'test-model' }, { mode: 'STUB', modelName: '本地模拟模型' }, { mode: 'LIVE', modelName: null }]) {
    assert.deepEqual(parseModelRuntimeSummary(value), value)
  }
})

test('拒绝错误模式、缺失名称与额外配置详情', () => {
  for (const value of [{ mode: 'OTHER', modelName: null }, { mode: 'LIVE' }, { mode: 'LIVE', modelName: '' },
    { mode: 'LIVE', modelName: 'test-model', apiUrl: 'https://example.test/private' },
    { mode: 'LIVE', modelName: 'test-model', apiKey: 'synthetic-key' }]) {
    assert.throws(() => parseModelRuntimeSummary(value))
  }
})
