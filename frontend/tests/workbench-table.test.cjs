/** 表格指令行为测试：隔离 Vue/存储边界，验证真实指令的恢复、更新和解绑。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const { EventEmitter } = require('node:events')
require('./load-typescript.cjs')
const { workbenchTable } = require('../src/directives/workbench-table.ts')
const { tableWidthKey } = require('../src/utils/workbench-preferences.ts')

/** 提供表格组件使用的最小端口；列与布局结果由真实指令修改。 */
function fixture() {
  const values = new Map()
  global.window = { localStorage: { getItem: key => values.get(key) ?? null, setItem: (key, value) => values.set(key, value) } }
  const events = new EventEmitter()
  const table = {
    store: { states: { columns: [{ property: 'title', label: '标题', width: undefined, realWidth: 200 }] } },
    layouts: 0,
    doLayout() { this.layouts++ },
    $on: events.on.bind(events), $off: events.off.bind(events),
    $nextTick: callback => Promise.resolve().then(callback),
  }
  const el = {}
  const binding = { value: 'tasks' }
  const vnode = { componentInstance: table }
  return { values, events, table, el, binding, vnode }
}

test('拖动后新实例恢复列宽，离开页面后停止记录', async () => {
  const f = fixture()
  workbenchTable.inserted(f.el, f.binding, f.vnode)
  await Promise.resolve()
  f.events.emit('header-dragend', 280, 200, f.table.store.states.columns[0])
  const key = tableWidthKey('tasks', f.table.store.states.columns)
  assert.equal(f.values.get(key), '{"0":280}')
  workbenchTable.unbind(f.el)
  f.events.emit('header-dragend', 320, 280, f.table.store.states.columns[0])
  assert.equal(f.values.get(key), '{"0":280}')
  const next = { ...f.table, store: { states: { columns: [{ property: 'title', label: '标题', realWidth: 200 }] } } }
  workbenchTable.inserted({}, f.binding, { componentInstance: next })
  await Promise.resolve()
  assert.equal(next.store.states.columns[0].width, 280)
  assert.equal(next.store.states.columns[0].realWidth, 280)
})

test('动态列复用时不把旧字段宽度应用于新字段', async () => {
  const f = fixture()
  f.values.set(tableWidthKey('tasks', f.table.store.states.columns), '{"0":350}')
  workbenchTable.inserted(f.el, f.binding, f.vnode)
  await Promise.resolve()
  assert.equal(f.table.store.states.columns[0].width, 350)
  Object.assign(f.table.store.states.columns[0], { property: 'status', label: '状态' })
  workbenchTable.componentUpdated(f.el, f.binding)
  await Promise.resolve()
  assert.equal(f.table.store.states.columns[0].width, undefined)
  assert.equal(f.table.store.states.columns[0].realWidth, 200)
  workbenchTable.unbind(f.el)
})

test('同结构列重新创建后恢复宽度，普通数据刷新不重复布局', async () => {
  const f = fixture()
  f.values.set(tableWidthKey('tasks', f.table.store.states.columns), '{"0":290}')
  workbenchTable.inserted(f.el, f.binding, f.vnode)
  await Promise.resolve()
  f.table.store.states.columns = [{ property: 'title', label: '标题', realWidth: 200 }]
  workbenchTable.componentUpdated(f.el, f.binding)
  await Promise.resolve()
  assert.equal(f.table.store.states.columns[0].width, 290)
  const layouts = f.table.layouts
  workbenchTable.componentUpdated(f.el, f.binding)
  await Promise.resolve()
  assert.equal(f.table.layouts, layouts)
  workbenchTable.unbind(f.el)
})

test('待恢复的表格销毁后不再执行布局', async () => {
  const f = fixture()
  workbenchTable.inserted(f.el, f.binding, f.vnode)
  workbenchTable.unbind(f.el)
  await Promise.resolve()
  assert.equal(f.table.layouts, 0)
  assert.equal(f.events.listenerCount('header-dragend'), 0)
})

test('同一实例切换表格身份后，拖动宽度写入新的表格', async () => {
  const f = fixture()
  workbenchTable.inserted(f.el, f.binding, f.vnode)
  await Promise.resolve()
  f.binding.value = 'other-evidence-table'
  workbenchTable.componentUpdated(f.el, f.binding)
  await Promise.resolve()
  f.events.emit('header-dragend', 310, 200, f.table.store.states.columns[0])
  assert.equal(f.values.get(tableWidthKey('other-evidence-table', f.table.store.states.columns)), '{"0":310}')
  assert.equal(f.values.get(tableWidthKey('tasks', f.table.store.states.columns)), undefined)
  workbenchTable.unbind(f.el)
})
