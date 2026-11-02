/** 最近异常页面测试：进入时只读取一次，并保留空记录与失败状态。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

let calls = 0
let response = { item: null }
let failure = null
const Page = loadVueScript('../src/views/LatestException.vue', {
  '../services/latest-exception-api': {
    fetchLatestException: async () => {
      calls += 1
      if (failure) throw failure
      return response
    },
  },
})

test('页面进入时读取一次，空记录保持空状态', async () => {
  calls = 0
  response = { item: null }
  failure = null
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  assert.equal(calls, 1)
  assert.equal(page.snapshot, null)
  assert.equal(page.loading, false)
  assert.equal(page.errorMessage, '')
  page.$destroy()
})

test('最近异常读取失败展示错误，不伪造旧记录', async () => {
  calls = 0
  failure = new Error('暂时无法读取')
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  assert.equal(calls, 1)
  assert.equal(page.snapshot, null)
  assert.match(page.errorMessage, /暂时无法读取/)
  page.$destroy()
})

test('有记录时展示最近异常快照且不持续请求', async () => {
  calls = 0
  failure = null
  response = { item: { occurredAt: '2026-11-02T00:00:00Z', source: 'MODEL_INTERACTION',
    exceptionType: 'java.lang.IllegalStateException', stackTrace: 'at Model.call(Model.java:23)' } }
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  assert.equal(calls, 1)
  assert.equal(page.snapshot.source, 'MODEL_INTERACTION')
  assert.equal(Page.options.computed.sourceLabel.call(page), '大模型交互')
  assert.match(page.snapshot.stackTrace, /Model.java:23/)
  page.$destroy()
})
