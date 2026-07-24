<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Finished /></el-icon> 美食管理</span>
          <el-button type="primary" @click="openForm(null)"><el-icon><Plus /></el-icon> 新增美食</el-button>
        </div>
      </template>
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索美食名称" clearable style="width:200px" @clear="handleSearch" @keydown.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="140" />
        <el-table-column prop="cityId" label="城市ID" width="80" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="rating" label="评分" width="70" />
        <el-table-column prop="price" label="价格" width="80"><template #default="{ row }">￥{{ row.price }}</template></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openForm(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id!)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="loadData" />
      </div>
    </el-card>
    <el-dialog v-model="formVisible" :title="formData.id ? '编辑美食' : '新增美食'" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="城市ID" required><el-input-number v-model="formData.cityId" :min="1" /></el-form-item>
        <el-form-item label="名称" required><el-input v-model="formData.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="formData.imageUrl" /></el-form-item>
        <el-form-item label="评分"><el-input-number v-model="formData.rating" :min="0" :max="5" :precision="1" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="formData.price" :min="0" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="formData.category" placeholder="中餐/西餐/小吃..." /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { foodApi } from "@/api/crud"
import { ElMessage, ElMessageBox } from "element-plus"

const loading = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const total = ref(0)
const pageNum = ref(1); const pageSize = ref(10); const keyword = ref("")
const formVisible = ref(false)
const formData = reactive({ cityId: 1, name: "", description: "", imageUrl: "", rating: 0, price: 0, category: "" })

onMounted(() => loadData())
async function loadData() {
  loading.value = true
  try {
    const res = await foodApi.page({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined })
    const d = res.data as any; tableData.value = d.records || []; total.value = d.total || 0
  } finally { loading.value = false }
}
function handleSearch() { pageNum.value = 1; loadData() }
function openForm(row: any) {
  if (row) Object.assign(formData, { ...row })
  else Object.assign(formData, { id: undefined, cityId: 1, name: "", description: "", imageUrl: "", rating: 0, price: 0, category: "" })
  formVisible.value = true
}
async function handleSave() {
  saving.value = true
  try {
    if (formData.id) { await foodApi.update(formData.id, formData); ElMessage.success("更新成功") }
    else { await foodApi.create(formData); ElMessage.success("创建成功") }
    formVisible.value = false; await loadData()
  } finally { saving.value = false }
}
async function handleDelete(id: number) {
  try { await ElMessageBox.confirm("确定删除？", "提示"); await foodApi.delete(id); ElMessage.success("删除成功"); await loadData() } catch { }
}
</script>
<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; font-size: 16px; font-weight: 600; }
.search-bar { margin-bottom: 16px; display: flex; gap: 8px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
