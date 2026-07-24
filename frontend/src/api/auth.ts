import http from "./index"

export interface LoginReq {
  username: string
  password: string
}

export interface RegisterReq {
  username: string
  password: string
  nickname: string
  email?: string
}

export interface UserVO {
  id: number
  username: string
  nickname: string
  email: string
  avatar: string
  phone: string
  role?: string
  status: number
  createTime: string
}

export interface LoginResp {
  token: string
  userVO: UserVO
}

export const authApi = {
  login(data: LoginReq) {
    return http.post<LoginResp>("/auth/login", data)
  },
  register(data: RegisterReq) {
    return http.post<UserVO>("/auth/register", data)
  },
  me() {
    return http.get<UserVO>("/auth/me")
  },
}
