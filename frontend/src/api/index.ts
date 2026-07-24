import axios from "axios"
import { ElMessage } from "element-plus"
import { useAuthStore } from "@/stores/auth"
import router from "@/router"

const http = axios.create({
  baseURL: "/",
  timeout: 60000,
})

http.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== undefined && res.code !== 200) {
      ElMessage.error(res.message || "请求失败")
      if (res.code === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push("/login")
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push("/login")
    }
    ElMessage.error(error.message || "网络错误")
    return Promise.reject(error)
  }
)

export default http
