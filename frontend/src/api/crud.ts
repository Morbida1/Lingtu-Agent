import http from "./index"

export interface CityVO {
  id?: number
  name: string
  province: string
  description: string
  imageUrl: string
  sortOrder: number
  createTime?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pages: number
}

/** 为 crudApi 扩展回收站方法 */
function recycleBinApi(prefix: string) {
  return {
    listDeleted() {
      return http.get(`${prefix}/deleted`)
    },
    restore(id: number) {
      return http.put(`${prefix}/${id}/restore`)
    },
    physicalDelete(id: number) {
      return http.delete(`${prefix}/${id}/physical`)
    },
  }
}

function crudApi(prefix: string) {
  return {
    create(data: any) {
      return http.post(prefix, data)
    },
    update(id: number, data: any) {
      return http.put(`${prefix}/${id}`, data)
    },
    delete(id: number) {
      return http.delete(`${prefix}/${id}`)
    },
    get(id: number) {
      return http.get(`${prefix}/${id}`)
    },
    list() {
      return http.get(`${prefix}/list`)
    },
    page(params: { pageNum: number; pageSize: number; keyword?: string }) {
      return http.get<PageResult<any>>(`${prefix}/page`, { params })
    },
  }
}

export const cityApi = { ...crudApi("/api/city"), ...recycleBinApi("/api/city") }
export const spotApi = {
  ...crudApi("/api/spot"),
  ...recycleBinApi("/api/spot"),
  listByCity(cityId: number) {
    return http.get(`/api/spot/list/city/${cityId}`)
  },
}
export const hotelApi = {
  ...crudApi("/api/hotel"),
  ...recycleBinApi("/api/hotel"),
  listByCity(cityId: number) {
    return http.get(`/api/hotel/list/city/${cityId}`)
  },
}
export const foodApi = {
  ...crudApi("/api/food"),
  ...recycleBinApi("/api/food"),
  listByCity(cityId: number) {
    return http.get(`/api/food/list/city/${cityId}`)
  },
  listByCategory(category: string) {
    return http.get(`/api/food/list/category/${category}`)
  },
}
export const itineraryApi = {
  ...crudApi("/api/itinerary"),
  ...recycleBinApi("/api/itinerary"),
  listByUser(userId: number) {
    return http.get(`/api/itinerary/list/user/${userId}`)
  },
}
export const userRecycleApi = recycleBinApi("/api/user")
