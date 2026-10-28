/** 聊天组件行为测试：真实 Vue props、事件与更新调度，不调用业务 API。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')
const Chat = loadVueScript('../src/components/WorkbenchChat.vue')

test('按钮与快捷键共用发送校验，忙碌、空白或不可用时均不发送', () => {
  for (const props of [
    { enabled: false, value: '补充' }, { enabled: true, value: '  ' },
    { enabled: true, loading: true, value: '补充' }, { enabled: true, analyzing: true, value: '补充' },
  ]) {
    const vm = new Chat({ propsData: { messages: [], ...props } })
    let sends = 0
    vm.$on('send', () => sends++)
    vm.sendMessage()
    assert.equal(sends, 0)
    vm.$destroy()
  }
  const vm = new Chat({ propsData: { messages: [], enabled: true, value: '补充事实' } })
  let sends = 0
  vm.$on('send', () => sends++)
  vm.sendMessage()
  assert.equal(sends, 1)
  vm.$destroy()
})

test('输入事件交给父页面且不修改 value prop', () => {
  const vm = new Chat({ propsData: { messages: [], value: '原输入' } })
  let input
  vm.$on('input', value => { input = value })
  vm.updateValue('新输入')
  assert.equal(input, '新输入')
  assert.equal(vm.value, '原输入')
  vm.$destroy()
})

test('新消息与同数量替换的模型回复都滚动到最新内容', async () => {
  const vm = new Chat({ propsData: { messages: [] } })
  const history = { scrollTop: 0, scrollHeight: 460 }
  vm.$refs.history = history
  vm.messages = [{ id: 'model-1', role: 'model', content: '第一条回复' }]
  await vm.$nextTick()
  await vm.$nextTick()
  assert.equal(history.scrollTop, 460)
  history.scrollHeight = 720
  vm.messages = [{ id: 'model-2', role: 'model', content: '重新分析后的回复' }]
  await vm.$nextTick()
  await vm.$nextTick()
  assert.equal(history.scrollTop, 720)
  vm.$destroy()
})

test('空闲时无等待状态，模型与最近调用统计分开说明来源', () => {
  const vm = new Chat({ propsData: { messages: [], runtimeModel: { mode: 'LIVE', modelName: 'current-model' },
    metadata: { mode: 'LIVE', modelName: 'last-model', tokens: 1234, latencyMillis: 1250 } } })
  assert.equal(vm.statusText, '')
  assert.equal(vm.modelText, 'current-model')
  assert.equal(vm.usageText, '最近调用 1,234 Token · 1.3 秒')
  assert.match(vm.usageDescription, /last-model/)
  vm.$destroy()
})

test('未调用不伪造 Token，无启用模型与模拟模型都有明确说明', () => {
  const vm = new Chat({ propsData: { messages: [], runtimeModel: { mode: 'LIVE', modelName: null } } })
  assert.equal(vm.modelText, '未启用模型')
  assert.equal(vm.usageText, '')
  vm.runtimeModel = { mode: 'STUB', modelName: '本地模拟模型' }
  assert.equal(vm.modelText, '本地模拟模型（模拟）')
  vm.$destroy()
})

test('聊天按实际顶部位置填充视口，高屏不再受720像素上限约束', () => {
  const vm = new Chat({ propsData: { messages: [] } })
  const oldWindow = global.window
  global.window = { innerHeight: 1440 }
  vm.$el = { getBoundingClientRect: () => ({ top: 128 }) }
  vm.fitViewport()
  assert.equal(vm.availableHeight, 1296)
  global.window.innerHeight = 900
  vm.$el = { getBoundingClientRect: () => ({ top: 170 }) }
  vm.fitViewport()
  assert.equal(vm.availableHeight, 714)
  global.window = oldWindow
  vm.$destroy()
})
