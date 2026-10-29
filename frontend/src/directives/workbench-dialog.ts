/** 业务表单弹窗的移动、尺寸调整与最大化；关闭重置，销毁释放事件。 */
import Vue, { DirectiveOptions, VNode } from 'vue'
import { constrainDialog, DialogRect } from '../utils/workbench-preferences'

interface DialogInstance extends Vue { visible: boolean }
const cleanups = new WeakMap<HTMLElement, () => void>()

/** 绑定一个业务弹窗，所有窗口操作共用视口边界约束。 */
function attach(el: HTMLElement, vnode: VNode): void {
  const vm = vnode.componentInstance as DialogInstance | undefined
  const dialog = el.querySelector<HTMLElement>('.el-dialog')
  const header = dialog?.querySelector<HTMLElement>('.el-dialog__header')
  if (!vm || !dialog || !header) return
  const panel = dialog
  let originalStyle = panel.getAttribute('style') || ''
  let previousRect: DialogRect | null = null
  let activePointer: number | null = null
  let stopDrag: (() => void) | null = null
  let disposed = false
  const maximize = document.createElement('button')
  const resize = document.createElement('button')
  maximize.type = resize.type = 'button'
  maximize.className = 'workbench-dialog-maximize'
  resize.className = 'workbench-dialog-resize'
  resize.setAttribute('aria-label', '调整窗口大小')
  resize.title = '拖动调整宽高，方向键调整大小'
  header.tabIndex = 0
  header.title = '拖动移动窗口，方向键调整位置'
  header.appendChild(maximize)
  panel.appendChild(resize)
  panel.classList.add('workbench-dialog-interactive')

  /** 读取当前视口内的窗口位置，避免依赖初始宽度单位。 */
  function rect(): DialogRect {
    const bounds = panel.getBoundingClientRect()
    return { left: bounds.left, top: bounds.top, width: bounds.width, height: bounds.height }
  }
  /** 根据当前视口计算可用空间。 */
  function viewport(): { width: number; height: number } {
    return { width: document.documentElement.clientWidth, height: window.innerHeight }
  }
  /** 设置固定几何尺寸，同时约束越界和过小窗口。 */
  function apply(value: DialogRect): void {
    const next = constrainDialog(value, viewport())
    Object.assign(panel.style, {
      position: 'fixed', margin: '0', left: `${next.left}px`, top: `${next.top}px`,
      width: `${next.width}px`, height: `${next.height}px`,
    })
  }
  /** 同步最大化按钮中文名称及缩放柄状态。 */
  function updateControls(): void {
    const title = previousRect ? '还原窗口' : '最大化'
    maximize.textContent = title
    maximize.title = title
    maximize.setAttribute('aria-label', title)
    maximize.setAttribute('aria-pressed', String(Boolean(previousRect)))
    resize.hidden = Boolean(previousRect)
  }
  /** 在最大化与用户原窗口尺寸之间切换。 */
  function toggleMaximize(): void {
    stopDrag?.()
    if (previousRect) {
      const saved = previousRect
      previousRect = null
      apply(saved)
    } else {
      previousRect = rect()
      apply({ left: 8, top: 8, width: viewport().width - 16, height: viewport().height - 16 })
    }
    updateControls()
  }
  /** 开始移动或缩放；只处理主指针，并在结束、关闭时清理文档监听。 */
  function start(event: PointerEvent, mode: 'move' | 'resize'): void {
    if (event.button !== 0 || previousRect || !vm?.visible) return
    if (mode === 'move' && (event.target as Element).closest('button, input, textarea, select, a')) return
    stopDrag?.()
    event.preventDefault()
    const startRect = rect()
    const x = event.clientX
    const y = event.clientY
    activePointer = event.pointerId
    panel.classList.add('is-manipulating')
    /** 按初始矩形计算增量，避免连续移动造成累计漂移。 */
    function move(next: PointerEvent): void {
      if (next.pointerId !== activePointer) return
      const dx = next.clientX - x
      const dy = next.clientY - y
      apply(mode === 'move'
        ? { ...startRect, left: startRect.left + dx, top: startRect.top + dy }
        : { ...startRect, width: startRect.width + dx, height: startRect.height + dy })
    }
    /** 主指针结束或取消时停止当前手势。 */
    function end(next: PointerEvent): void { if (next.pointerId === activePointer) stopDrag?.() }
    /** 清理手势监听和临时状态；可由关闭、失焦或销毁触发。 */
    stopDrag = () => {
      document.removeEventListener('pointermove', move)
      document.removeEventListener('pointerup', end)
      document.removeEventListener('pointercancel', end)
      window.removeEventListener('blur', cancel)
      panel.classList.remove('is-manipulating')
      activePointer = null
      stopDrag = null
    }
    document.addEventListener('pointermove', move)
    document.addEventListener('pointerup', end)
    document.addEventListener('pointercancel', end)
    window.addEventListener('blur', cancel)
  }
  /** 窗口失焦时终止手势，防止鼠标松开后仍拖动。 */
  function cancel(): void { stopDrag?.() }
  /** 标题栏移动入口。 */
  function moveStart(event: PointerEvent): void { start(event, 'move') }
  /** 右下角尺寸调整入口。 */
  function resizeStart(event: PointerEvent): void { start(event, 'resize') }
  /** 方向键提供移动和缩放的替代操作，Shift 增大步长。 */
  function keydown(event: KeyboardEvent): void {
    if (previousRect || event.target !== header && event.target !== resize) return
    const directions: Record<string, [number, number]> = { ArrowLeft: [-1, 0], ArrowRight: [1, 0], ArrowUp: [0, -1], ArrowDown: [0, 1] }
    const direction = directions[event.key]
    if (!direction) return
    event.preventDefault()
    const value = rect()
    const step = event.shiftKey ? 40 : 10
    const [dx, dy] = direction.map(value => value * step)
    apply(event.target === resize ? { ...value, width: value.width + dx, height: value.height + dy }
      : { ...value, left: value.left + dx, top: value.top + dy })
  }
  /** 视口缩小时重新约束窗口，最大化状态继续铺满可用区域。 */
  function fitViewport(): void {
    if (!vm?.visible) return
    apply(previousRect ? { left: 8, top: 8, width: viewport().width - 16, height: viewport().height - 16 } : rect())
  }
  /** 每次关闭重置位置尺寸，保留框架原始的宽度和居中行为。 */
  function reset(): void {
    stopDrag?.()
    previousRect = null
    panel.setAttribute('style', originalStyle)
    updateControls()
  }
  const unwatch = vm.$watch('visible', (visible: boolean) => {
    if (!visible) reset()
    else void vm.$nextTick(() => {
      if (disposed || !vm.visible) return
      originalStyle = panel.getAttribute('style') || ''
      fitViewport()
    })
  }, { immediate: true })
  updateControls()
  header.addEventListener('pointerdown', moveStart)
  resize.addEventListener('pointerdown', resizeStart)
  header.addEventListener('keydown', keydown)
  resize.addEventListener('keydown', keydown)
  maximize.addEventListener('click', toggleMaximize)
  window.addEventListener('resize', fitViewport)
  /** 销毁所有交互资源，包含尚未结束的指针手势。 */
  cleanups.set(el, () => {
    disposed = true
    reset()
    unwatch()
    header.removeEventListener('pointerdown', moveStart)
    resize.removeEventListener('pointerdown', resizeStart)
    header.removeEventListener('keydown', keydown)
    resize.removeEventListener('keydown', keydown)
    maximize.removeEventListener('click', toggleMaximize)
    window.removeEventListener('resize', fitViewport)
    maximize.remove()
    resize.remove()
    panel.classList.remove('workbench-dialog-interactive')
  })
}

export const workbenchDialog: DirectiveOptions = {
  /** 等待 Element 初次渲染完成后绑定原生窗口控件。 */
  inserted(el, _binding, vnode) { attach(el, vnode) },
  /** 销毁组件时回收事件和原生控件。 */
  unbind(el) { cleanups.get(el)?.(); cleanups.delete(el) },
}
