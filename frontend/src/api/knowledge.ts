import http from "./index"

export interface KnowledgeDocVO {
  id: number
  userId: number
  title: string
  fileType: string
  content: string
  chunkCount: number
  status: number
  createTime: string
}

export const knowledgeApi = {
  upload(formData: FormData) {
    return http.post<KnowledgeDocVO>("/api/knowledge/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    })
  },
  list() {
    return http.get<KnowledgeDocVO[]>("/api/knowledge/list")
  },
  query(question: string) {
    return http.post<string>("/api/knowledge/query", null, { params: { question } })
  },
  delete(docId: number) {
    return http.delete(`/api/knowledge/${docId}`)
  },
}

export const plannerApi = {
  plan(request: string) {
    return http.post<string>("/api/ai/planner", null, { params: { request } })
  },
}

export const adminKnowledgeApi = {
  page(params: { pageNum: number; pageSize: number; keyword?: string }) {
    return http.get<{ records: KnowledgeDocVO[]; total: number; pages: number }>("/api/admin/knowledge/page", { params })
  },
  get(docId: number) {
    return http.get<KnowledgeDocVO>(`/api/admin/knowledge/${docId}`)
  },
  delete(docId: number) {
    return http.delete(`/api/admin/knowledge/${docId}`)
  },
}
