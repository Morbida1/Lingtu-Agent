import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { authApi, type UserVO } from "@/api/auth"

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "")
  const user = ref<UserVO | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(t: string) {
    token.value = t
    localStorage.setItem("token", t)
  }

  async function login(username: string, password: string) {
    const res = await authApi.login({ username, password })
    const data = res.data as any
    setToken(data.token)
    user.value = data.userVO
    return data
  }

  async function register(data: { username: string; password: string; nickname: string; email?: string }) {
    const res = await authApi.register(data)
    return res.data
  }

  async function fetchProfile() {
    const res = await authApi.me()
    user.value = res.data as any
    return user.value
  }

  function logout() {
    token.value = ""
    user.value = null
    localStorage.removeItem("token")
  }

  return { token, user, isLoggedIn, setToken, login, register, fetchProfile, logout }
})
