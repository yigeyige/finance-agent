/** 弹窗交互测试；用最小 DOM 端口提供事件与几何，执行真实指令。 */
const { test } = require('node:test')
const assert = require('node:assert/strict')
require('./load-typescript.cjs')
const { workbenchDialog } = require('../src/directives/workbench-dialog.ts')

/** Node 没有浏览器 DOM，此端口只提供指令依赖的样式、事件和节点操作。 */
class ElementPort extends EventTarget {
  constructor(tag = 'div') {
    super()
    this.tag = tag
    this.style = {}
    this.attributes = new Map()
    this.children = []
    const classes = new Set()
    this.classList = { add: value => classes.add(value), remove: value => classes.delete(value) }
  }
  getAttribute(key) {
    if (key === 'style') return Object.entries(this.style).map(([key, value]) => `${key}:${value}`).join(';')
    return this.attributes.get(key)
  }
  setAttribute(key, value) {
    if (key === 'style') this.style = Object.fromEntries(value.split(';').filter(Boolean).map(pair => pair.split(':')))
    else this.attributes.set(key, value)
  }
  appendChild(child) { this.children.push(child); child.parent = this }
  remove() { this.parent.children = this.parent.children.filter(child => child !== this) }
  closest() { return this.tag === 'button' ? this : null }
  getBoundingClientRect() {
    return { left: parseFloat(this.style.left || '340'), top: parseFloat(this.style.top || '120'),
      width: parseFloat(this.style.width || '600'), height: parseFloat(this.style.height || '500') }
  }
}

/** 创建独立视口、弹窗与可观察 visible 端口。 */
function fixture() {
  global.window = Object.assign(new EventTarget(), { innerHeight: 800 })
  global.document = Object.assign(new EventTarget(), { documentElement: { clientWidth: 1280 }, createElement: tag => new ElementPort(tag) })
  const header = new ElementPort()
  const panel = new ElementPort()
  panel.style = { width: '600px', marginTop: '15vh' }
  panel.querySelector = () => header
  const el = { querySelector: () => panel }
  let listener
  let watching = true
  const vm = {
    visible: false,
    $nextTick: callback => Promise.resolve().then(callback),
    $watch(_key, callback) { listener = callback; callback(false); return () => { watching = false } },
  }
  workbenchDialog.inserted(el, {}, { componentInstance: vm })
  return { header, panel, el, vm, maximize: header.children[0], resize: panel.children[0],
    setVisible(value) { vm.visible = value; if (watching) listener(value) } }
}

/** 构造真实可取消事件；坐标与方向键通过只读属性提供。 */
function event(type, properties = {}) {
  const value = new Event(type, { cancelable: true })
  Object.entries(properties).forEach(([key, property]) => Object.defineProperty(value, key, { value: property }))
  return value
}

test('弹窗移动和缩放遵守视口边界，最大化后可还原', async () => {
  const f = fixture()
  f.setVisible(true)
  await Promise.resolve()
  f.header.dispatchEvent(event('pointerdown', { button: 0, pointerId: 1, clientX: 400, clientY: 150 }))
  document.dispatchEvent(event('pointermove', { pointerId: 1, clientX: -500, clientY: 2000 }))
  document.dispatchEvent(event('pointerup', { pointerId: 1 }))
  assert.equal(f.panel.style.left, '8px')
  assert.equal(f.panel.style.top, '292px')
  f.resize.dispatchEvent(event('keydown', { key: 'ArrowRight' }))
  assert.equal(f.panel.style.width, '610px')
  f.maximize.dispatchEvent(event('click'))
  assert.equal(f.panel.style.width, '1264px')
  assert.equal(f.panel.style.height, '784px')
  assert.equal(f.resize.hidden, true)
  assert.equal(f.maximize.getAttribute('aria-label'), '还原窗口')
  f.maximize.dispatchEvent(event('click'))
  assert.equal(f.panel.style.width, '610px')
  assert.equal(f.panel.style.top, '292px')
  workbenchDialog.unbind(f.el)
})

test('打开时水平垂直居中，且固定 margin 归零', async () => {
  const f = fixture()
  f.setVisible(true)
  await Promise.resolve()
  assert.equal(f.panel.style.left, '340px')
  assert.equal(f.panel.style.top, '150px')
  assert.equal(f.panel.style.margin, '0')
  workbenchDialog.unbind(f.el)
})

test('拖动中关闭重置默认尺寸并停止移动，再打开不保留上次几何', async () => {
  const f = fixture()
  f.setVisible(true)
  await Promise.resolve()
  f.resize.dispatchEvent(event('pointerdown', { button: 0, pointerId: 2, clientX: 940, clientY: 620 }))
  document.dispatchEvent(event('pointermove', { pointerId: 2, clientX: 1100, clientY: 700 }))
  assert.equal(f.panel.style.width, '760px')
  f.setVisible(false)
  assert.deepEqual(f.panel.style, { width: '600px', marginTop: '15vh' })
  document.dispatchEvent(event('pointermove', { pointerId: 2, clientX: 1200, clientY: 750 }))
  assert.deepEqual(f.panel.style, { width: '600px', marginTop: '15vh' })
  f.setVisible(true)
  await Promise.resolve()
  assert.equal(f.panel.style.width, '600px')
  assert.equal(f.panel.style.left, '340px')
  workbenchDialog.unbind(f.el)
})

test('最大化适配视口缩小，销毁清理控件和活动指针', async () => {
  const f = fixture()
  f.setVisible(true)
  await Promise.resolve()
  f.maximize.dispatchEvent(event('click'))
  document.documentElement.clientWidth = 360
  window.innerHeight = 240
  window.dispatchEvent(event('resize'))
  assert.equal(f.panel.style.width, '344px')
  assert.equal(f.panel.style.height, '224px')
  f.maximize.dispatchEvent(event('click'))
  f.header.dispatchEvent(event('pointerdown', { button: 0, pointerId: 3, clientX: 100, clientY: 50 }))
  workbenchDialog.unbind(f.el)
  document.dispatchEvent(event('pointermove', { pointerId: 3, clientX: 200, clientY: 80 }))
  window.dispatchEvent(event('resize'))
  assert.deepEqual(f.panel.style, { width: '600px', marginTop: '15vh' })
  assert.equal(f.header.children.length, 0)
  assert.equal(f.panel.children.length, 0)
})
