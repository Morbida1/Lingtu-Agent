<template>
  <div class="chat-page">
    <!-- 左侧会话列表 -->
    <div class="chat-sidebar">
      <div class="chat-sidebar-header">
        <el-button type="primary" style="width:100%" @click="handleNewSession">
          <el-icon><Plus /></el-icon> 新建会话
        </el-button>
      </div>
      <div class="session-list">
        <div
          v-for="s in chatStore.sessions"
          :key="s.id"
          :class="['session-item', { active: chatStore.currentSession?.id === s.id }]"
          @click="handleSelectSession(s)"
        >
          <el-icon><ChatDotSquare /></el-icon>
          <span class="session-title">{{ s.title }}</span>
          <el-icon class="delete-btn" @click.stop="handleDeleteSession(s.id)"><Delete /></el-icon>
        </div>
        <el-empty v-if="!chatStore.sessions.length" description="暂无会话" :image-size="60" />
      </div>
    </div>

    <!-- 右侧聊天区 -->
    <div class="chat-main">
      <div class="message-list" ref="messageListRef">
        <div v-for="(msg, idx) in messages" :key="msg.id || idx" :class="['message-item', msg.role]">
          <el-avatar :size="36" :icon="msg.role === 'user' ? 'UserFilled' : 'MagicStick'" :style="{ background: msg.role === 'user' ? '#3699ff' : '#13c2c2' }" />
          <div :class="['message-bubble', msg.role]">
            <MarkdownRenderer v-if="msg.role === 'assistant'" :content="msg.content" />
            <div v-else class="user-text">{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="streaming" class="message-item assistant">
          <el-avatar :size="36" icon="MagicStick" style="background:#13c2c2" />
          <div class="message-bubble assistant">
            <MarkdownRenderer :content="streamContent" />
            <el-icon class="loading-icon" color="#3699ff"><Loading /></el-icon>
          </div>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="input-area">
        <div class="mode-switch">
          <el-switch v-model="agentMode" active-text="Agent 模式" inactive-text="普通模式" />
        </div>
        <div class="input-row">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息，按 Ctrl+Enter 发送..."
            :disabled="streaming"
            @keydown.ctrl.enter="handleSend"
          />
          <el-button type="primary" :loading="streaming" @click="handleSend" class="send-btn">
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { useChatStore } from "@/stores/chat"
import { chatApi } from "@/api/chat"
import { useAuthStore } from "@/stores/auth"
import MarkdownRenderer from "@/components/MarkdownRenderer.vue"

const chatStore = useChatStore()
const authStore = useAuthStore()
const messageListRef = ref<HTMLElement>()
const inputMessage = ref("")
const streaming = ref(false)
const streamContent = ref("")
const agentMode = ref(false)

const messages = ref<any[]>([])

onMounted(() => {
  chatStore.loadSessions()
})

watch(() => chatStore.messages, (val) => {
  messages.value = val.map((m) => ({ ...m }))
}, { immediate: true, deep: true })

watch(messages, () => {
  scrollToBottom()
}, { deep: true })

watch(streamContent, () => {
  scrollToBottom()
})

function scrollToBottom() {
  nextTick(() => {
    const el = messageListRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

async function handleNewSession() {
  const { value } = await ElMessageBox.prompt("请输入会话标题", "新建会话", {
    inputValue: "新会话",
    inputPlaceholder: "会话标题",
  })
  if (value) {
    await chatStore.createSession(value)
    messages.value = []
  }
}

async function handleSelectSession(session: any) {
  await chatStore.selectSession(session)
}

async function handleDeleteSession(id: number) {
  try {
    await ElMessageBox.confirm("确定删除此会话？", "提示")
    await chatStore.deleteSession(id)
    if (chatStore.currentSession?.id !== id) {
      messages.value = chatStore.messages.map((m) => ({ ...m }))
    } else {
      messages.value = []
    }
  } catch {
    // cancelled
  }
}

async function handleSend() {
  const text = inputMessage.value.trim()
  if (!text || streaming.value) return

  const userMsg = {
    id: Date.now(),
    sessionId: chatStore.currentSession?.id || 0,
    userId: authStore.user?.id || 0,
    role: "user",
    content: text,
    createTime: new Date().toISOString(),
  }
  messages.value.push(userMsg)
  inputMessage.value = ""

  // 如果无当前会话，自动创建
  if (!chatStore.currentSession) {
    const title = text.length > 20 ? text.substring(0, 20) + "..." : text
    await chatStore.createSession(title)
  }

  const sessionId = chatStore.currentSession!.id

  streaming.value = true
  streamContent.value = ""

  try {
    const endpoint = agentMode.value ? chatApi.sendAgent : chatApi.send
    const res = await endpoint(sessionId, text)
    const rawText = typeof res === "string" ? res : (res.data || res)

    // 解析 SSE 数据：event:message data:内容
    const lines = rawText.split("\n")
    let fullContent = ""
    for (const line of lines) {
      const msgMatch = line.match(/^data:\s*(.+)$/)
      if (msgMatch) {
        const data = msgMatch[1].trim()
        if (data === "[DONE]") break
        try {
          const parsed = JSON.parse(data)
          if (parsed.content) {
            fullContent += parsed.content
          } else {
            fullContent += data
          }
        } catch {
          fullContent += data
        }
        streamContent.value = fullContent
        await nextTick()
      }
    }

    // 添加到消息列表
    const assistantMsg = {
      id: Date.now() + 1,
      sessionId,
      userId: authStore.user?.id || 0,
      role: "assistant",
      content: fullContent,
      createTime: new Date().toISOString(),
    }
    messages.value.push(assistantMsg)

    // 刷新当前会话消息
    await chatStore.selectSession(chatStore.currentSession!)
  } catch (e: any) {
    ElMessage.error("发送失败: " + (e.message || "未知错误"))
  } finally {
    streaming.value = false
    streamContent.value = ""
  }
}
</script>

<style scoped>
.chat-page {
  display: flex;
  height: calc(100vh - 60px - 40px);
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}
.chat-sidebar {
  width: 240px;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}
.chat-sidebar-header {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
}
.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}
.session-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 2px;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
}
.session-item:hover, .session-item.active {
  background: #e6f0ff;
  color: #3699ff;
}
.session-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.delete-btn {
  opacity: 0;
  transition: opacity 0.2s;
}
.session-item:hover .delete-btn {
  opacity: 1;
}
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.message-item.user {
  flex-direction: row-reverse;
}
.message-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
}
.message-bubble.user {
  background: #3699ff;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.message-bubble.assistant {
  background: #f0f0f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}
.user-text {
  white-space: pre-wrap;
}
.loading-icon {
  margin-top: 4px;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.input-area {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #fff;
}
.mode-switch {
  margin-bottom: 8px;
  display: flex;
  justify-content: flex-end;
}
.input-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}
.send-btn {
  height: 74px;
  width: 56px;
}
</style>
