const { test } = require('node:test')
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const loadVueScript = require('./load-vue-script.cjs')

let response = { items: [] }
let jobs = { items: [{ code: 'retention-cleanup', name: '业务数据清理',
  javaClass: 'cn.com.crc.ems.finance.agent.retention.RetentionJob',
  frequency: 'WEEKLY', dayOfWeek: 7, dayOfMonth: null, time: '02:00', zone: 'Asia/Shanghai' }] }
let runCalls = 0
let jobCalls = 0
let updated = null
let saveFailure = false
let updateCalls = 0
let manualCalls = 0
let manualFailure = false
const Page = loadVueScript('../src/views/RetentionRuns.vue', {
  '../services/retention-api': {
    fetchRetentionRuns: async () => { runCalls += 1; return response },
    fetchScheduledJobs: async () => { jobCalls += 1; return jobs },
    updateRetentionSchedule: async request => {
      updateCalls += 1
      if (saveFailure) throw new Error('保存失败')
      updated = request
      return { ...jobs.items[0], ...request }
    },
    executeRetentionNow: async () => {
      manualCalls += 1
      if (manualFailure) throw new Error('提交失败')
      response = { items: [{ startedAt: '2026-11-05T06:00:00Z', finishedAt: '2026-11-05T06:00:01Z',
        jobCode: 'retention-cleanup', status: '成功', deleted: {},
        workspaceFiles: 0, objectFiles: 0, localLogFiles: 0, errors: [] }] }
    },
  },
})

test('保存右侧手动执行经确认后只提交一次并刷新最新日志', async () => {
  const source = fs.readFileSync(path.resolve(__dirname, '../src/views/RetentionRuns.vue'), 'utf8')
  assert.match(source, /@click="saveSchedule">保存<\/el-button>\s*<el-button[^>]*@click="runOnce">立即执行一次<\/el-button>/)
  response = { items: [] }
  manualCalls = 0
  manualFailure = false
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))
  page.$confirm = async () => undefined
  page.draftTime = '03:00'
  await page.runOnce()
  assert.equal(manualCalls, 1)
  assert.equal(page.latestRun.startedAt, '2026-11-05T06:00:00Z')
  assert.equal(page.selectedJob.time, '02:00')
  assert.equal(page.draftTime, '03:00')
  page.runningNow = true
  await page.runOnce()
  assert.equal(manualCalls, 1)
  page.$destroy()
})

test('取消或提交失败不伪造清理结果', async () => {
  response = { items: [] }
  manualCalls = 0
  manualFailure = false
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))
  page.$confirm = async () => { throw 'cancel' }
  await page.runOnce()
  assert.equal(manualCalls, 0)
  page.$confirm = async () => undefined
  manualFailure = true
  await page.runOnce()
  assert.equal(manualCalls, 1)
  assert.equal(page.runError, '提交失败')
  assert.equal(page.latestRun, undefined)
  page.$destroy()
})

test('调度任务页面显示左侧任务与右侧最新日志，并可更新每周时间', async () => {
  runCalls = 0
  jobCalls = 0
  updated = null
  saveFailure = false
  response = { items: [{ startedAt: '2026-11-04T00:00:00Z', finishedAt: '2026-11-04T00:01:00Z',
    jobCode: 'retention-cleanup', status: '成功', deleted: { 'task.investigation_task': 2, 'task.task_attachment': 3 },
    workspaceFiles: 1, objectFiles: 0, localLogFiles: 0, errors: [] }] }
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  assert.equal(runCalls, 1)
  assert.equal(jobCalls, 1)
  assert.equal(page.jobs.length, 1)
  assert.equal(page.selectedJob.name, '业务数据清理')
  assert.equal(page.javaClassLabel, 'RetentionJob')
  assert.equal(page.latestRun.startedAt, '2026-11-04T00:00:00Z')
  assert.equal(page.runs.length, 1)
  assert.equal(Page.options.methods.deletedTotal.call(page, page.runs[0]), 5)
  assert.equal(Page.options.methods.tableLabel.call(page, 'task.investigation_task'), '任务')
  page.draftDay = 2
  page.draftTime = '08:30'
  await page.saveSchedule()
  assert.deepEqual(updated, { frequency: 'WEEKLY', dayOfWeek: 2, dayOfMonth: null, time: '08:30' })
  assert.equal(page.selectedJob.time, '08:30')
  page.$destroy()
})

test('频率可从每周改为每天或每月并提交对应日期', async () => {
  response = { items: [] }
  saveFailure = false
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  page.draftFrequency = 'DAILY'
  page.draftTime = '01:30'
  await page.saveSchedule()
  assert.deepEqual(updated, { frequency: 'DAILY', dayOfWeek: null, dayOfMonth: null, time: '01:30' })

  page.draftFrequency = 'MONTHLY'
  page.draftMonthDay = 15
  page.draftTime = '06:20'
  await page.saveSchedule()
  assert.deepEqual(updated, { frequency: 'MONTHLY', dayOfWeek: null, dayOfMonth: 15, time: '06:20' })
  page.$destroy()
})

test('暂无运行记录时仍显示任务，保存失败可重试且忙碌时不重复提交', async () => {
  response = { items: [] }
  saveFailure = true
  updateCalls = 0
  const page = new Page()
  await new Promise(resolve => setImmediate(resolve))

  assert.equal(page.jobs.length, 1)
  assert.equal(page.latestRun, undefined)
  page.draftDay = 4
  await page.saveSchedule()
  assert.equal(page.saveError, '保存失败')
  page.saving = true
  await page.saveSchedule()
  assert.equal(updateCalls, 1)
  page.$destroy()
})
