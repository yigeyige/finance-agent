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
