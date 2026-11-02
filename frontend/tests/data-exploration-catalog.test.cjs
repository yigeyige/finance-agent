/** 数据探索目录回归测试：表名展示与注释搜索、选表元数据保持独立。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
const loadVueScript = require('./load-vue-script.cjs')

const Page = loadVueScript('../src/views/DataExploration.vue', {
  '../services/data-exploration-api': {
    fetchDataExplorationColumns: async () => [],
  },
  '../services/ems-database-config-api': {},
})

const tables = [
  { schema: 'basedata', table: 'sys_user', comment: '用户信息' },
  { schema: 'basedata', table: 'sys_role', comment: '' },
]

test('表节点始终显示表名，注释仍可搜索且选表保留真实注释', async () => {
  const view = { ...Page.options.data(), catalog: { tables }, tableKeyword: '' }
  const tree = Page.options.computed.schemaTree.call(view)
  assert.deepEqual(tree[0].children.map(node => node.label), ['sys_user', 'sys_role'])

  view.tableKeyword = '用户信息'
  const found = Page.options.computed.schemaTree.call(view)
  assert.equal(found[0].children.length, 1)
  assert.equal(found[0].children[0].label, 'sys_user')

  await Page.options.methods.handleTableClick.call(view, found[0].children[0])
  assert.equal(view.selectedTable.table, 'sys_user')
  assert.equal(view.selectedTable.comment, '用户信息')
})
