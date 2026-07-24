import http from "./index"
import type { UserVO } from "./auth"

export const userApi = {
  profile() {
    return http.get<UserVO>("/api/user/profile")
  },
  updateProfile(data: { nickname: string }) {
    return http.put("/api/user/profile", data)
  },
  updatePassword(data: { oldPassword: string; newPassword: string }) {
    return http.put("/api/user/password", data)
  },
  page(params: { pageNum: number; pageSize: number; keyword?: string }) {
    return http.get<{ records: UserVO[]; total: number; pages: number }>("/api/user/page", { params })
  },
}
