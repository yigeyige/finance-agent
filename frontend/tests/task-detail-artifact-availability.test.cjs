/** 任务详情流程产物入口测试：仅有效场景可显示，异步结果不阻塞决策包。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

let resolveAvailability
let rejectAvailability
const TaskDetail = loadVueScript('../src/views/TaskDetail.vue', {
  '../services/task-list-api': {
    getTaskDetail: async taskId => ({ taskId, decisionPacket: { revision: 1 } }),
    getTaskProcessArtifactAvailability: () => new Promise((resolve, reject) => {
      resolveAvailability = resolve
      rejectAvailability = reject
    }),
  },
  '../services/process-artifact-api': {},
  '../constants/field-labels': { fieldAnnotation: () => '', fieldTooltip: () => '' },
})

function view(taskId = 'task-1') {
  const instance = { ...TaskDetail.options.data(), $route: { params: { taskId } } }
  for (const [name, method] of Object.entries(TaskDetail.options.methods)) instance[name] = method.bind(instance)
  return instance
}

test('决策包先显示，确认场景提示词有效后才显示按钮', async () => {
  const instance = view()
  await instance.loadDetail()
  assert.equal(instance.detail.taskId, 'task-1')
  assert.equal(TaskDetail.options.computed.showArtifactButton.call(instance), false)

  resolveAvailability(true)
  await new Promise(resolve => setImmediate(resolve))
  assert.equal(TaskDetail.options.computed.showArtifactButton.call(instance), true)
})

test('入口状态查询失败或旧任务响应不影响当前决策包', async () => {
  const instance = view()
  await instance.loadDetail()
  rejectAvailability(new Error('暂时无法读取'))
  await new Promise(resolve => setImmediate(resolve))
  assert.equal(instance.detail.taskId, 'task-1')
  assert.equal(TaskDetail.options.computed.showArtifactButton.call(instance), false)

  await instance.loadDetail()
  instance.detail = { taskId: 'task-2', decisionPacket: { revision: 1 } }
  resolveAvailability(true)
  await new Promise(resolve => setImmediate(resolve))
  assert.equal(TaskDetail.options.computed.showArtifactButton.call(instance), false)
})
