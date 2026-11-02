/** 任务详情检查结论展示测试：分句只改变显示，不改接口原文。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

const TaskDetail = loadVueScript('../src/views/TaskDetail.vue', {
  '../services/task-list-api': {},
  '../services/process-artifact-api': {},
  '../constants/field-labels': { fieldAnnotation: () => '', fieldTooltip: () => '' },
})

test('检查结论按中英文分号和句号分行，保留原始内容', () => {
  const conclusion = '第一项；第二项。第三项;第四项. 第五项'
  const view = { detail: { conclusion } }

  assert.equal(TaskDetail.options.computed.guidanceConclusionText.call(view),
    '第一项；\n第二项。\n第三项;\n第四项.\n第五项')
  assert.equal(view.detail.conclusion, conclusion)
})
