/** 工作台列宽偏好与弹窗几何约束；不接触业务数据。 */
export interface ColumnDescriptor { property?: string; label?: string; type?: string }
export interface DialogRect { left: number; top: number; width: number; height: number }
export type WidthStorage = Pick<Storage, 'getItem' | 'setItem'>

/** 根据表格身份及字段结构生成本地偏好键。 */
export function tableWidthKey(table: string, columns: ColumnDescriptor[]): string {
  return `finance-workbench:column-widths:v1:${table}:${JSON.stringify(columns.map(column => [column.property || '', column.label || '', column.type || '']))}`
}
/** 只允许像素数值，排除损坏存储及超出正常阅读范围的宽度。 */
function validWidth(width: unknown): width is number {
  return typeof width === 'number' && Number.isFinite(width) && width >= 30 && width <= 4096
}
/** 安全读取有效列宽，不可信存储不影响页面使用。 */
export function readWidths(storage: WidthStorage, key: string): Record<string, number> {
  try {
    const value: unknown = JSON.parse(storage.getItem(key) || '{}')
    if (!value || typeof value !== 'object' || Array.isArray(value)) return {}
    return Object.fromEntries(Object.entries(value).filter(([column, width]) => /^\d+$/.test(column) && validWidth(width)))
  } catch { return {} }
}
/** 合并保存单列宽度，存储不可写时降级。 */
export function saveWidth(storage: WidthStorage, key: string, column: string, width: number): void {
  if (!/^\d+$/.test(column) || !validWidth(width)) return
  try { storage.setItem(key, JSON.stringify({ ...readWidths(storage, key), [column]: Math.round(width) })) } catch { /* 存储禁用或配额不足时保留当前会话交互。 */ }
}
/** 将窗口尺寸和位置限制于当前视口。 */
export function constrainDialog(rect: DialogRect, viewport: { width: number; height: number }): DialogRect {
  const availableWidth = Math.max(1, viewport.width - 16)
  const availableHeight = Math.max(1, viewport.height - 16)
  const width = Math.min(availableWidth, Math.max(480, rect.width))
  const height = Math.min(availableHeight, Math.max(280, rect.height))
  return {
    left: Math.max(8, Math.min(rect.left, viewport.width - width - 8)),
    top: Math.max(8, Math.min(rect.top, viewport.height - height - 8)),
    width, height,
  }
}
