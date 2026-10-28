/** 三步页面状态测试，执行页面实际方法，仅隔离后端 API。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

/** 准备可控的决策包 API，页面生命周期仍使用真实 Vue。 */
function fixture(generate, update = async () => ({ taskId: 'test-task' }), runtime = async () => ({ mode: 'LIVE', modelName: 'test-model' })) {
  const Workbench = loadVueScript('../src/views/WorkbenchHome.vue', {
    '../components/WorkbenchChat.vue': {},
    '../services/business-workflow': { DEFAULT_RUNTIME_BUNDLE_ID: 'test-bundle' },
    '../services/identity': { getCurrentIdentity: () => null },
    '../services/business-skill-api': { listBusinessSceneSkills: async () => [] },
    '../services/ems-database-config-api': { listEnabledDatabaseInstances: async () => [] },
    '../services/task-api': { isStaleError: () => false, updateTask: update },
    '../services/model-api': { generateBusinessDecisionPacket: generate, getBusinessEvidenceAccums: async () => [], getModelRuntimeSummary: runtime },
  })
  const vm = new Workbench()
  vm.task = { taskId: 'test-task' }
  vm.guidance = { draft: { subjectKey: 'test-subject' } }
  vm.activeStep = 1
  vm.$message = { success() {} }
  return vm
}

test('决策包生成成功才进入第三步，失败保留协作与重试入口', async () => {
  const success = fixture(async () => ({ revision: 1, content: { conclusion: '测试结论', evidence: [] } }))
  await success.generateDecisionPacket()
  assert.equal(success.activeStep, 2)
  assert.equal(success.decisionPacket.revision, 1)
  success.$destroy()
  const failure = fixture(async () => { throw Error('生成失败') })
  await failure.generateDecisionPacket()
  assert.equal(failure.activeStep, 1)
  assert.equal(failure.packetLoading, false)
  assert.equal(failure.decisionPacket, null)
  assert.equal(failure.errorMessage, '生成失败')
  failure.$destroy()
})

test('正在生成时再次点击或模型忙碌时不重复调用决策包接口', async () => {
  let resolve
  let requests = 0
  const vm = fixture(() => { requests++; return new Promise(done => { resolve = done }) })
  const pending = vm.generateDecisionPacket()
  const duplicate = vm.generateDecisionPacket()
  assert.equal(requests, 1)
  resolve({ revision: 1, content: { evidence: [] } })
  await Promise.all([pending, duplicate])
  vm.chatLoading = true
  await vm.generateDecisionPacket()
  assert.equal(requests, 1)
  vm.$destroy()
})

test('新模型建议使本地旧决策包失效，保留任务与会话', () => {
  const vm = fixture(async () => ({}))
  vm.decisionPacket = { revision: 1 }
  vm.chatMessages = [{ id: 'operator-1', role: 'operator', content: '继续修改' }]
  vm.applyGuidance({ draft: { subjectKey: 'new-subject', objectKey: 'new-object' } })
  assert.equal(vm.decisionPacket, null)
  assert.equal(vm.task.taskId, 'test-task')
  assert.equal(vm.chatMessages[0].content, '继续修改')
  assert.equal(vm.form.subjectKey, 'new-object')
  vm.$destroy()
})

test('任务信息保存失败保留原成果，成功保存后要求重新生成', async () => {
  const success = fixture(async () => ({}))
  success.decisionPacket = { revision: 1 }
  await success.saveTaskInfo()
  assert.equal(success.decisionPacket, null)
  success.$destroy()
  const failure = fixture(async () => ({}), async () => { throw Error('保存失败') })
  failure.decisionPacket = { revision: 1 }
  await failure.saveTaskInfo()
  assert.equal(failure.decisionPacket.revision, 1)
  assert.equal(failure.errorMessage, '保存失败')
  failure.$destroy()
})

test('模型响应记录最近调用元数据，清空草稿时移除且不累加为任务消耗', () => {
  const vm = fixture(async () => ({}))
  vm.applyGuidance({ draft: { subjectKey: 'test-subject' }, metadata: { mode: 'LIVE', modelName: 'actual-model', tokens: 125, latencyMillis: 500 } })
  assert.equal(vm.lastModelMetadata.tokens, 125)
  vm.applyGuidance({ draft: { subjectKey: 'test-subject' }, metadata: { mode: 'LIVE', modelName: 'other-model', tokens: 70, latencyMillis: 300 } })
  assert.equal(vm.lastModelMetadata.tokens, 70)
  assert.equal(vm.lastModelMetadata.modelName, 'other-model')
  vm.resetDraft()
  assert.equal(vm.lastModelMetadata, null)
  vm.$destroy()
})

test('运行摘要读取失败不阻断业务流程，也不伪造模型名', async () => {
  const vm = fixture(async () => ({}), undefined, async () => { throw Error('摘要不可用') })
  await vm.$nextTick()
  await vm.loadRuntimeModel()
  assert.equal(vm.runtimeModel, null)
  assert.equal(vm.runtimeModelLoading, false)
  assert.equal(vm.errorMessage, '')
  assert.equal(vm.activeStep, 1)
  vm.$destroy()
})
