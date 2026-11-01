/** 任务列表分页测试，验证真实页面方法面对页大小切换和乱序响应的状态。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

test('切换每页条数回到首页，较旧分页响应不覆盖新结果', async () => {
  const pending = []
  const TaskList = loadVueScript('../src/views/TaskList.vue', {
    '../services/task-list-api': { listTasks: query => new Promise(resolve => pending.push({ query, resolve })) },
  })
  global.window = { innerHeight: 800, addEventListener() {}, removeEventListener() {} }
  const vm = new TaskList()
  assert.deepEqual(pending[0].query, { page: 1, pageSize: 10, status: undefined, keyword: undefined })
  pending[0].resolve({ items: [], total: 25, page: 1, pageSize: 10 })
  await new Promise(resolve => setImmediate(resolve))

  vm.changePage(2)
  vm.changePageSize(20)
  assert.equal(pending[1].query.page, 2)
  assert.deepEqual([pending[2].query.page, pending[2].query.pageSize], [1, 20])
  pending[2].resolve({ items: [{ taskId: 'new' }], total: 1, page: 1, pageSize: 20 })
  await new Promise(resolve => setImmediate(resolve))
  pending[1].resolve({ items: [{ taskId: 'stale' }], total: 25, page: 2, pageSize: 10 })
  await new Promise(resolve => setImmediate(resolve))

  assert.deepEqual([vm.currentPage, vm.pageSize, vm.total, vm.tasks[0].taskId], [1, 20, 1, 'new'])
  vm.$destroy()
  delete global.window
})
