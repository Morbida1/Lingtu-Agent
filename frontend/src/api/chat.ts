import http from "./index"

export interface ChatSessionVO {
  id: number
  userId: number
  title: string
  createTime: string
  updateTime: string
}

export interface ChatMessageVO {
  id: number
  sessionId: number
  userId: number
  role: string
  content: string
  createTime: string
}

export const chatApi = {
  send(sessionId: number | null, message: string) {
    return http.post("/api/chat/send", { sessionId, message }, { responseType: "text" }) as any
  },
  sendAgent(sessionId: number | null, message: string) {
    return http.post("/api/chat/send/agent", { sessionId, message }, { responseType: "text" }) as any
  },
  createSession(title: string) {
    return http.post<ChatSessionVO>(`/api/chat/session?title=${encodeURIComponent(title)}`)
  },
  listSessions() {
    return http.get<ChatSessionVO[]>("/api/chat/session")
  },
  listMessages(sessionId: number) {
    return http.get<ChatMessageVO[]>(`/api/chat/message/${sessionId}`)
  },
  deleteSession(sessionId: number) {
    return http.delete(`/api/chat/session/${sessionId}`)
  },
}
