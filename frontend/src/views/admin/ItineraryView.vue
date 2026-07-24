<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Calendar /></el-icon> 行程管理</span>
          <el-button type="primary" @click="openForm(null)"><el-icon><Plus /></el-icon> 新增行程</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="160" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="cityId" label="城市ID" width="80" />
        <el-table-column prop="days" label="天数" width="60" />
        <el-table-column prop="budget" label="预算" width="100"><template #default="{ row }">￥{{ row.budget }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "已发布" : "草稿" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
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
    <el-dialog v-model="formVisible" :title="formData.id ? '编辑行程' : '新增行程'" width="700px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="用户ID" required><el-input-number v-model="formData.userId" :min="1" /></el-form-item>
        <el-form-item label="城市ID" required><el-input-number v-model="formData.cityId" :min="1" /></el-form-item>
        <el-form-item label="标题" required><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" type="textarea" :rows="2" /></el-form-item>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="开始日期"><el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束日期"><el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="天数"><el-input-number v-model="formData.days" :min="1" /></el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="预算"><el-input-number v-model="formData.budget" :min="0" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">已发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="行程内容">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="Markdown 格式的详细行程" />
        </el-form-item>
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
import { itineraryApi } from "@/api/crud"
import { ElMessage, ElMessageBox } from "element-plus"

const loading = ref(false); const saving = ref(false)
const tableData = ref<any[]>([]); const total = ref(0)
const pageNum = ref(1); const pageSize = ref(10)
const formVisible = ref(false)
const formData = reactive({ userId: 1, cityId: 1, title: "", description: "", startDate: "", endDate: "", days: 1, budget: 0, status: 0, content: "" })

onMounted(() => loadData())
async function loadData() {
  loading.value = true
  try {
    const res = await itineraryApi.page({ pageNum: pageNum.value, pageSize: pageSize.value })
    const d = res.data as any; tableData.value = d.records || []; total.value = d.total || 0
  } finally { loading.value = false }
}
function openForm(row: any) {
  if (row) Object.assign(formData, { ...row })
  else Object.assign(formData, { id: undefined, userId: 1, cityId: 1, title: "", description: "", startDate: "", endDate: "", days: 1, budget: 0, status: 0, content: "" })
  formVisible.value = true
}
async function handleSave() {
  saving.value = true
  try {
    if (formData.id) { await itineraryApi.update(formData.id, formData); ElMessage.success("更新成功") }
    else { await itineraryApi.create(formData); ElMessage.success("创建成功") }
    formVisible.value = false; await loadData()
  } finally { saving.value = false }
}
async function handleDelete(id: number) {
  try { await ElMessageBox.confirm("确定删除？", "提示"); await itineraryApi.delete(id); ElMessage.success("删除成功"); await loadData() } catch { }
}
</script>
<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; font-size: 16px; font-weight: 600; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
