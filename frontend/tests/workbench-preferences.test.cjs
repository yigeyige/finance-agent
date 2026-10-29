/** 公共交互边界测试：执行真实 TypeScript 模块，不依赖浏览器或业务服务。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { tableWidthKey, readWidths, saveWidth, constrainDialog } = require('../src/utils/workbench-preferences.ts')

/** 模拟浏览器键值存储，仅隔离外部持久化边界。 */
function storage() {
  const values = new Map()
  return { getItem: key => values.get(key) ?? null, setItem: (key, value) => values.set(key, value) }
}

test('列宽跨重新读取保留，且不同表格与字段结构互不污染', () => {
  const store = storage()
  const key = tableWidthKey('tasks', [{ property: 'title', label: '标题' }])
  saveWidth(store, key, '0', 265)
  assert.deepEqual(readWidths(store, key), { 0: 265 })
  assert.deepEqual(readWidths(store, tableWidthKey('models', [{ property: 'title', label: '标题' }])), {})
  assert.deepEqual(readWidths(store, tableWidthKey('tasks', [{ property: 'status', label: '状态' }])), {})
})

test('单列调整保留其他列，拒绝异常宽度且不持久化行内容', () => {
  const store = storage()
  saveWidth(store, 'table', '0', 180)
  saveWidth(store, 'table', '1', 220)
  saveWidth(store, 'table', '2', -20)
  saveWidth(store, 'table', '3', Infinity)
  assert.deepEqual(JSON.parse(store.getItem('table')), { 0: 180, 1: 220 })
})

test('损坏JSON、无效宽度及存储禁用不阻断交互', () => {
  const store = storage()
  store.setItem('broken', '{')
  store.setItem('array', '[180]')
  store.setItem('mixed', '{"0":240,"1":"200","2":0,"3":99999,"4":null}')
  assert.deepEqual(readWidths(store, 'broken'), {})
  assert.deepEqual(readWidths(store, 'array'), {})
  assert.deepEqual(readWidths(store, 'mixed'), { 0: 240 })
  const denied = { getItem() { throw Error('denied') }, setItem() { throw Error('denied') } }
  assert.deepEqual(readWidths(denied, 'table'), {})
  assert.doesNotThrow(() => saveWidth(denied, 'table', '0', 180))
})

test('移动与缩放受视口约束，较小视口优先于默认最小尺寸', () => {
  assert.deepEqual(constrainDialog({ left: -40, top: 900, width: 1800, height: 1200 }, { width: 1280, height: 800 }),
    { left: 8, top: 8, width: 1264, height: 784 })
  assert.deepEqual(constrainDialog({ left: 1000, top: -40, width: 40, height: 20 }, { width: 1280, height: 800 }),
    { left: 792, top: 8, width: 480, height: 280 })
  assert.deepEqual(constrainDialog({ left: 0, top: 0, width: 600, height: 500 }, { width: 360, height: 240 }),
    { left: 8, top: 8, width: 344, height: 224 })
})
