/** 为显式接入的 Element UI 表格恢复和保存列宽，不保存业务行内容。 */
import Vue, { DirectiveOptions, VNode } from 'vue'
import { ColumnDescriptor, readWidths, saveWidth, tableWidthKey } from '../utils/workbench-preferences'

interface TableColumn extends ColumnDescriptor { width?: number; realWidth?: number }
interface WorkbenchTable extends Vue {
  store: { states: { columns: TableColumn[] } }
  doLayout(): void
}
interface TableState {
  table: WorkbenchTable
  id: string
  key: string
  columns: TableColumn[]
  pending: boolean
  disposed: boolean
  defaults: WeakMap<TableColumn, Pick<TableColumn, 'width' | 'realWidth'>>
  restore: () => void
  save: (width: number, old: number, column: TableColumn) => void
}
const states = new WeakMap<HTMLElement, TableState>()

/** 等待 Element 更新动态列后恢复一次，避免布局响应式更新产生循环。 */
function scheduleRestore(el: HTMLElement, id: string): void {
  const state = states.get(el)
  if (!state) return
  state.id = id
  if (state.pending) return
  state.pending = true
  void state.table.$nextTick(() => {
    state.pending = false
    if (state.disposed) return
    const columns = state.table.store.states.columns
    if (!columns.length) return
    const key = tableWidthKey(state.id, columns)
    if (key === state.key && columns.length === state.columns.length && columns.every((column, index) => column === state.columns[index])) return
    state.key = key
    state.columns = [...columns]
    columns.forEach(column => {
      const original = state.defaults.get(column)
      if (original) Object.assign(column, original)
      else state.defaults.set(column, { width: column.width, realWidth: column.realWidth })
    })
    try {
      const widths = readWidths(window.localStorage, key)
      columns.forEach((column, index) => {
        const width = widths[String(index)]
        if (width) column.width = column.realWidth = width
      })
      state.table.doLayout()
    } catch { /* 浏览器禁止访问存储时使用 Element 默认列宽。 */ }
  })
}

/** 初始化单个表格的列宽监听及生命周期状态。 */
function attach(el: HTMLElement, id: string, vnode: VNode): void {
  const table = vnode.componentInstance as WorkbenchTable | undefined
  if (!table) return
  const state: TableState = {
    table, id, key: '', columns: [], pending: false, disposed: false, defaults: new WeakMap(),
    /** 激活缓存页时重新布局，适配隐藏期间的视口变化。 */
    restore: () => table.doLayout(),
    /** 仅记录用户拖动的列，按当前字段结构隔离偏好。 */
    save: (width, _old, column) => {
      const columns = table.store.states.columns
      const index = columns.indexOf(column)
      if (index < 0) return
      try { saveWidth(window.localStorage, tableWidthKey(state.id, columns), String(index), width) } catch { /* 本地偏好写入失败不影响表格。 */ }
    },
  }
  states.set(el, state)
  table.$on('header-dragend', state.save)
  table.$on('hook:activated', state.restore)
  scheduleRestore(el, id)
}

export const workbenchTable: DirectiveOptions = {
  /** 插入后在下一轮更新读取 Element 已注册的列。 */
  inserted(el, binding, vnode) { attach(el, String(binding.value), vnode) },
  /** 动态结果列发生变化时重新读取对应偏好。 */
  componentUpdated(el, binding) { scheduleRestore(el, String(binding.value)) },
  /** 离开页面时释放实例监听，防止缓存及销毁后的异步回调。 */
  unbind(el) {
    const state = states.get(el)
    if (!state) return
    state.disposed = true
    state.table.$off('header-dragend', state.save)
    state.table.$off('hook:activated', state.restore)
    states.delete(el)
  },
}
