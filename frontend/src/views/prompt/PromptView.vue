<template>
  <div class="prompt-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><Edit /></el-icon> Prompt 模板管理</span>
          <el-button type="primary" @click="openForm(null)">
            <el-icon><Plus /></el-icon> 新增模板
          </el-button>
        </div>
      </template>

      <!-- 模板列表 -->
      <el-table :data="templateList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="variables" label="变量" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openForm(row)">编辑</el-button>
            <el-button size="small" @click="openRender(row)">测试渲染</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id!)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formData.id ? '编辑模板' : '新增模板'" width="680px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="分类" required>
          <el-input v-model="formData.category" placeholder="如：travel、chat、summary" />
        </el-form-item>
        <el-form-item label="名称" required>
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="变量">
          <el-input v-model="formData.variables" placeholder="逗号分隔：city, days, budget" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="formData.content" type="textarea" :rows="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 渲染测试弹窗 -->
    <el-dialog v-model="renderVisible" title="测试渲染" width="680px">
      <el-form label-width="80px">
        <el-form-item label="模板内容">
          <el-input :modelValue="renderTemplate?.content" type="textarea" :rows="6" disabled />
        </el-form-item>
        <el-form-item v-for="v in renderVars" :key="v" :label="v">
          <el-input v-model="renderValues[v]" :placeholder="`输入 ${v} 的值`" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="rendering" @click="handleRender">渲染</el-button>
        </el-form-item>
        <el-form-item v-if="renderResult" label="结果">
          <div class="render-result">
            <MarkdownRenderer :content="renderResult" />
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue"
import { promptApi, type PromptTemplate } from "@/api/prompt"
import { ElMessage, ElMessageBox } from "element-plus"
import MarkdownRenderer from "@/components/MarkdownRenderer.vue"

const loading = ref(false)
const templateList = ref<PromptTemplate[]>([])
const formVisible = ref(false)
const saving = ref(false)
const renderVisible = ref(false)
const rendering = ref(false)
const renderResult = ref("")
const renderTemplate = ref<PromptTemplate | null>(null)
const renderValues = reactive<Record<string, string>>({})
const renderVars = ref<string[]>([])

const formData = reactive<PromptTemplate>({
  category: "",
  name: "",
  content: "",
  variables: "",
  description: "",
})

onMounted(() => {
  loadTemplates()
})

async function loadTemplates() {
  loading.value = true
  try {
    const res = await promptApi.list()
    templateList.value = (res.data as any) || []
  } finally {
    loading.value = false
  }
}

function openForm(row: PromptTemplate | null) {
  if (row) {
    Object.assign(formData, { ...row })
  } else {
    Object.assign(formData, { category: "", name: "", content: "", variables: "", description: "", id: undefined })
  }
  formVisible.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (formData.id) {
      await promptApi.update({ ...formData })
      ElMessage.success("更新成功")
    } else {
      await promptApi.create({ ...formData })
      ElMessage.success("创建成功")
    }
    formVisible.value = false
    await loadTemplates()
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm("确定删除此模板？", "提示")
    await promptApi.delete(id)
    ElMessage.success("删除成功")
    await loadTemplates()
  } catch {
    // cancelled
  }
}

function openRender(row: PromptTemplate) {
  renderTemplate.value = row
  renderResult.value = ""
  const vars = (row.variables || "").split(",").map((v) => v.trim()).filter(Boolean)
  renderVars.value = vars
  vars.forEach((v) => { renderValues[v] = "" })
  renderVisible.value = true
}

async function handleRender() {
  if (!renderTemplate.value) return
  rendering.value = true
  try {
    const res = await promptApi.render(renderTemplate.value.category, { ...renderValues })
    renderResult.value = (res.data as any) || res
  } finally {
    rendering.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}
.render-result {
  background: #f5f5f9;
  padding: 12px;
  border-radius: 6px;
  max-height: 400px;
  overflow-y: auto;
  width: 100%;
}
</style>
