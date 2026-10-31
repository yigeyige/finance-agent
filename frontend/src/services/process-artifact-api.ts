/** 流程文件生成、批次查看与下载，仅供用户主动操作。 */
import { getJson, httpClient, postJson } from './http'

export interface ProcessArtifactFileInfo { fileId: string; kind: string; fileName: string }
export interface ProcessArtifactBatch { batchId: string; packetId: string; packetRevision: number; importStatus: string; missingItems: string[]; createdAt: string; files: ProcessArtifactFileInfo[] }

/** 生成一批绑定决策包修订的流程文件。 */
export async function generateProcessArtifacts(taskId: string, packetId: string): Promise<ProcessArtifactBatch> {
  return postJson(`/v1/tasks/${taskId}/process-artifacts`, { packetId }, { timeout: 600_000 })
}

/** 查看最近两批文件元数据。 */
export async function listProcessArtifacts(taskId: string): Promise<ProcessArtifactBatch[]> {
  return getJson(`/v1/tasks/${taskId}/process-artifacts`)
}

/** 下载单文件或按选择打包的 ZIP。 */
export async function downloadProcessArtifact(taskId: string, batchId: string, fileId?: string, kinds?: string[], name?: string): Promise<void> {
  const path = `/v1/tasks/${taskId}/process-artifacts/${batchId}`
  const selected = new URLSearchParams()
  if (!fileId) (kinds || ['MODEL']).forEach(kind => selected.append('kinds', kind))
  const url = fileId ? `${path}/files/${fileId}` : `${path}/zip?${selected.toString()}`
  const response = await httpClient.get<Blob>(url, { responseType: 'blob', timeout: 60_000 })
  const blobUrl = URL.createObjectURL(response.data)
  const anchor = document.createElement('a')
  anchor.href = blobUrl
  anchor.download = name || '流程文件.zip'
  document.body.appendChild(anchor)
  anchor.click()
  anchor.remove()
  setTimeout(() => URL.revokeObjectURL(blobUrl), 1000)
}
