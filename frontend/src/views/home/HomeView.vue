<template>
  <div class="home-page">
    <div class="welcome-section">
      <h1>欢迎回来，{{ authStore.user?.nickname || "旅行者" }} 👋</h1>
      <p v-if="isAdmin">管理后台，掌控全局</p>
      <p v-else>今天想去哪里？灵途 Agent 为您服务</p>
    </div>

    <!-- 普通用户功能卡片 -->
    <div class="feature-grid">
      <el-card shadow="hover" class="feature-card" @click="$router.push('/chat')">
        <div class="card-icon" style="background:#e6f7ff"><el-icon :size="32" color="#1890ff"><ChatDotSquare /></el-icon></div>
        <h3>AI 聊天</h3>
        <p>与智能 AI 助手对话，获取旅行建议和帮助</p>
      </el-card>
      <el-card shadow="hover" class="feature-card" @click="$router.push('/planner')">
        <div class="card-icon" style="background:#fff7e6"><el-icon :size="32" color="#fa8c16"><MapLocation /></el-icon></div>
        <h3>旅行规划</h3>
        <p>输入需求，AI 自动为您生成完整行程</p>
      </el-card>
      <el-card shadow="hover" class="feature-card" @click="$router.push('/guide')">
        <div class="card-icon" style="background:#f0f5ff"><el-icon :size="32" color="#2f54eb"><Guide /></el-icon></div>
        <h3>城市导览</h3>
        <p>浏览城市、景点、酒店和美食信息</p>
      </el-card>

      <!-- 管理员额外功能 -->
      <template v-if="isAdmin">
        <el-card shadow="hover" class="feature-card" @click="$router.push('/knowledge')">
          <div class="card-icon" style="background:#f6ffed"><el-icon :size="32" color="#52c41a"><FolderOpened /></el-icon></div>
          <h3>知识库</h3>
          <p>管理 RAG 知识文档，增强 AI 回答质量</p>
        </el-card>
        <el-card shadow="hover" class="feature-card" @click="$router.push('/admin/city')">
          <div class="card-icon" style="background:#fff0f0"><el-icon :size="32" color="#f5222d"><Setting /></el-icon></div>
          <h3>管理后台</h3>
          <p>城市、景点、酒店、美食等数据管理</p>
        </el-card>
      </template>
    </div>

    <!-- 快捷统计 -->
    <div class="quick-stats">
      <el-row :gutter="20">
        <el-col :span="isAdmin ? 8 : 8">
          <el-card shadow="never">
            <div class="stat-item"><span class="stat-label">AI 会话</span><span class="stat-value">{{ stats.sessions }}</span></div>
          </el-card>
        </el-col>
        <el-col :span="isAdmin ? 8 : 8">
          <el-card shadow="never">
            <div class="stat-item"><span class="stat-label">城市导览</span><span class="stat-value">{{ stats.cities }}</span></div>
          </el-card>
        </el-col>
        <el-col :span="isAdmin ? 8 : 8">
          <el-card shadow="never">
            <div class="stat-item"><span class="stat-label">旅行计划</span><span class="stat-value">{{ stats.plans }}</span></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import { useAuthStore } from "@/stores/auth"
import { chatApi } from "@/api/chat"
import { cityApi } from "@/api/crud"

const authStore = useAuthStore()
const isAdmin = computed(() => authStore.user?.role === "admin")
const stats = ref({ sessions: 0, cities: 0, plans: 0 })

onMounted(async () => {
  try {
    const [sessionsRes, citiesRes] = await Promise.all([
      chatApi.listSessions(),
      cityApi.list(),
    ])
    stats.value.sessions = ((sessionsRes.data as any) || []).length
    stats.value.cities = ((citiesRes.data as any) || []).length
  } catch { /* ignore */ }
})
</script>

<style scoped>
.welcome-section { margin-bottom:28px; }
.welcome-section h1 { font-size:24px; color:#303133; margin-bottom:6px; }
.welcome-section p { color:#909399; font-size:14px; }
.feature-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:20px; margin-bottom:28px; }
.feature-card { cursor:pointer; transition:transform .2s,box-shadow .2s; }
.feature-card:hover { transform:translateY(-4px); }
.card-icon { width:56px; height:56px; border-radius:12px; display:flex; align-items:center; justify-content:center; margin-bottom:12px; }
.feature-card h3 { font-size:16px; margin-bottom:6px; color:#303133; }
.feature-card p { font-size:13px; color:#909399; line-height:1.4; }
.stat-item { display:flex; justify-content:space-between; align-items:center; }
.stat-label { color:#909399; font-size:14px; }
.stat-value { font-size:24px; font-weight:700; color:#3699ff; }
</style>
