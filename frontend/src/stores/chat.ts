import { defineStore } from "pinia"
import { ref } from "vue"
import { chatApi, type ChatSessionVO, type ChatMessageVO } from "@/api/chat"

export const useChatStore = defineStore("chat", () => {
  const sessions = ref<ChatSessionVO[]>([])
  const currentSession = ref<ChatSessionVO | null>(null)
  const messages = ref<ChatMessageVO[]>([])
  const loading = ref(false)

  async function loadSessions() {
    const res = await chatApi.listSessions()
    sessions.value = (res.data as any) || []
  }

  async function createSession(title: string) {
    const res = await chatApi.createSession(title)
    const session = res.data as any
    sessions.value.unshift(session)
    currentSession.value = session
    messages.value = []
    return session
  }

  async function selectSession(session: ChatSessionVO) {
    currentSession.value = session
    const res = await chatApi.listMessages(session.id)
    messages.value = (res.data as any) || []
  }

  async function deleteSession(id: number) {
    await chatApi.deleteSession(id)
    sessions.value = sessions.value.filter((s) => s.id !== id)
    if (currentSession.value?.id === id) {
      currentSession.value = null
      messages.value = []
    }
  }

  return { sessions, currentSession, messages, loading, loadSessions, createSession, selectSession, deleteSession }
})
