import http from "./index"

export interface PromptTemplate {
  id?: number
  category: string
  name: string
  content: string
  variables: string
  description: string
  createTime?: string
}

export const promptApi = {
  list() {
    return http.get<PromptTemplate[]>("/api/prompt/list")
  },
  listByCategory(category: string) {
    return http.get<PromptTemplate[]>(`/api/prompt/${category}`)
  },
  create(data: PromptTemplate) {
    return http.post("/api/prompt", data)
  },
  update(data: PromptTemplate) {
    return http.put("/api/prompt", data)
  },
  delete(id: number) {
    return http.delete(`/api/prompt/${id}`)
  },
  render(category: string, variables: Record<string, string>) {
    return http.post<string>("/api/prompt/render", variables, { params: { category } })
  },
}
