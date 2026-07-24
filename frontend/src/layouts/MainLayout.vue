<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="sidebar-logo">
        <el-icon :size="24"><Compass /></el-icon>
        <span class="sidebar-title">灵途 Agent</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#1e1e2d"
        text-color="#a2a3b7"
        active-text-color="#3699ff"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><ChatDotSquare /></el-icon>
          <span>AI 聊天</span>
        </el-menu-item>
        <el-menu-item index="/planner">
          <el-icon><MapLocation /></el-icon>
          <span>旅行规划</span>
        </el-menu-item>
        <el-menu-item index="/guide">
          <el-icon><Guide /></el-icon>
          <span>城市导览</span>
        </el-menu-item>

        <!-- 管理员专属菜单 -->
        <template v-if="isAdmin">
          <el-menu-item index="/knowledge">
            <el-icon><FolderOpened /></el-icon>
            <span>知识库</span>
          </el-menu-item>
          <el-menu-item index="/prompt">
            <el-icon><Edit /></el-icon>
            <span>Prompt 管理</span>
          </el-menu-item>

          <el-sub-menu index="/admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>管理后台</span>
            </template>
            <el-menu-item index="/admin/city">城市管理</el-menu-item>
            <el-menu-item index="/admin/spot">景点管理</el-menu-item>
            <el-menu-item index="/admin/hotel">酒店管理</el-menu-item>
            <el-menu-item index="/admin/food">美食管理</el-menu-item>
            <el-menu-item index="/admin/itinerary">行程管理</el-menu-item>
            <el-menu-item index="/admin/user">用户管理</el-menu-item>
            <el-menu-item index="/admin/knowledge">知识库管理</el-menu-item>
            <el-menu-item index="/admin/recycle-bin">
              <el-icon><DeleteFilled /></el-icon>
              <span>回收站</span>
            </el-menu-item>
          </el-sub-menu>
        </template>

        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="user-info" @click="$router.push('/profile')">
          <el-avatar :size="32" icon="UserFilled" />
          <span class="user-name">{{ authStore.user?.nickname || authStore.user?.username || "用户" }}</span>
        </div>
        <el-button text style="color:#a2a3b7; padding: 0" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </el-aside>

    <!-- 主区域 -->
    <el-container>
      <el-header class="main-header">
        <div class="header-left">
          <el-icon :size="20" style="cursor:pointer" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="header-title">{{ route.meta?.title || "灵途 Agent" }}</span>
          <el-tag v-if="isAdmin" size="small" type="warning" effect="dark">管理员</el-tag>
        </div>
        <div class="header-right">
          <el-tag type="info" effect="plain">v1.0.0</el-tag>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth"

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isCollapse = ref(false)
const sidebarWidth = computed(() => (isCollapse.value ? "64px" : "220px"))

const activeMenu = computed(() => route.path)
const isAdmin = computed(() => authStore.user?.role === "admin")

function handleLogout() {
  authStore.logout()
  router.push("/login")
}
</script>

<style scoped>
.sidebar {
  background-color: #1e1e2d;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.3s;
}
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.sidebar-title {
  white-space: nowrap;
}
.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
}
.sidebar-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(255,255,255,0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #a2a3b7;
  flex: 1;
  min-width: 0;
}
.user-name {
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.main-header {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.main-content {
  background: #f5f5f9;
  padding: 20px;
  overflow-y: auto;
}
</style>
