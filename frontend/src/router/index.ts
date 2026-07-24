import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/auth"
import MainLayout from "@/layouts/MainLayout.vue"

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/login/LoginView.vue"),
      meta: { noAuth: true },
    },
    {
      path: "/",
      component: MainLayout,
      redirect: "/home",
      children: [
        { path: "home", name: "Home", component: () => import("@/views/home/HomeView.vue"), meta: { title: "首页" } },
        { path: "chat", name: "Chat", component: () => import("@/views/chat/ChatView.vue"), meta: { title: "AI 聊天" } },
        { path: "planner", name: "Planner", component: () => import("@/views/planner/PlannerView.vue"), meta: { title: "旅行规划" } },
        { path: "guide", name: "Guide", component: () => import("@/views/guide/GuideView.vue"), meta: { title: "城市导览" } },
        { path: "knowledge", name: "Knowledge", component: () => import("@/views/knowledge/KnowledgeView.vue"), meta: { title: "知识库", role: "admin" } },
        { path: "prompt", name: "Prompt", component: () => import("@/views/prompt/PromptView.vue"), meta: { title: "Prompt 管理", role: "admin" } },
        { path: "profile", name: "Profile", component: () => import("@/views/profile/ProfileView.vue"), meta: { title: "个人信息" } },
        { path: "admin/city", name: "AdminCity", component: () => import("@/views/admin/CityView.vue"), meta: { title: "城市管理", role: "admin" } },
        { path: "admin/spot", name: "AdminSpot", component: () => import("@/views/admin/SpotView.vue"), meta: { title: "景点管理", role: "admin" } },
        { path: "admin/hotel", name: "AdminHotel", component: () => import("@/views/admin/HotelView.vue"), meta: { title: "酒店管理", role: "admin" } },
        { path: "admin/food", name: "AdminFood", component: () => import("@/views/admin/FoodView.vue"), meta: { title: "美食管理", role: "admin" } },
        { path: "admin/itinerary", name: "AdminItinerary", component: () => import("@/views/admin/ItineraryView.vue"), meta: { title: "行程管理", role: "admin" } },
        { path: "admin/user", name: "AdminUser", component: () => import("@/views/admin/UserView.vue"), meta: { title: "用户管理", role: "admin" } },
        { path: "admin/knowledge", name: "AdminKnowledge", component: () => import("@/views/admin/KnowledgeAdminView.vue"), meta: { title: "知识库管理", role: "admin" } },
        { path: "admin/recycle-bin", name: "AdminRecycleBin", component: () => import("@/views/admin/RecycleBinView.vue"), meta: { title: "回收站", role: "admin" } },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  if (!to.meta.noAuth && !authStore.isLoggedIn) {
    next("/login")
  } else if (to.meta.role === "admin" && authStore.user?.role !== "admin") {
    next("/home")
  } else {
    next()
  }
})

export default router
