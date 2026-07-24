<template>
  <div class="knowledge-page">
    <el-row :gutter="20">
      <!-- 左侧：上传与文档列表 -->
      <el-col :span="10">
        <el-card class="upload-card">
          <template #header>
            <span><el-icon><Upload /></el-icon> 上传文档</span>
          </template>
          <el-upload
            drag
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept=".pdf,.docx,.md,.txt"
          >
            <el-icon class="upload-icon" :size="40"><UploadFilled /></el-icon>
            <div class="upload-text">拖拽或点击上传文档</div>
            <template #tip>
              <div class="upload-tip">支持 PDF、DOCX、MD、TXT 格式</div>
            </template>
          </el-upload>
          <div v-if="selectedFile" class="file-info">
            <el-tag closable @close="selectedFile = null">{{ selectedFile.name }}</el-tag>
            <el-input v-model="docTitle" placeholder="文档标题（选填）" style="margin-top:8px" />
            <el-button type="primary" :loading="uploading" @click="handleUpload" style="margin-top:8px;width:100%">
              开始上传
            </el-button>
          </div>
        </el-card>

        <el-card class="doc-list-card">
          <template #header>
            <span><el-icon><Folder /></el-icon> 文档列表</span>
          </template>
          <div v-for="doc in docList" :key="doc.id" class="doc-item">
            <div class="doc-info">
              <el-icon><Document /></el-icon>
              <div class="doc-text">
                <span class="doc-title">{{ doc.title }}</span>
                <span class="doc-meta">
                  <el-tag :type="doc.status === 1 ? 'success' : doc.status === 2 ? 'danger' : 'warning'" size="small">
                    {{ doc.status === 1 ? "已处理" : doc.status === 2 ? "失败" : "处理中" }}
                  </el-tag>
                  {{ doc.fileType }} · {{ doc.chunkCount }} 分块
                </span>
              </div>
            </div>
            <el-button text type="danger" :icon="Delete" @click="handleDeleteDoc(doc.id)" />
          </div>
          <el-empty v-if="!docList.length" description="暂无文档" :image-size="60" />
        </el-card>
      </el-col>

      <!-- 右侧：知识库问答 -->
      <el-col :span="14">
        <el-card class="qa-card">
          <template #header>
            <span><el-icon><Search /></el-icon> 知识库问答</span>
          </template>
          <div class="qa-messages" ref="qaRef">
            <div v-for="(qa, idx) in qaList" :key="idx" class="qa-item">
              <div class="qa-question">
                <el-avatar :size="28" icon="UserFilled" style="background:#3699ff" />
                <div class="qa-text q">{{ qa.question }}</div>
              </div>
              <div class="qa-answer">
                <el-avatar :size="28" icon="MagicStick" style="background:#13c2c2" />
                <div class="qa-text a">
                  <MarkdownRenderer :content="qa.answer" />
                </div>
              </div>
            </div>
          </div>
          <div class="qa-input">
            <el-input v-model="qaQuestion" placeholder="输入问题..." @keydown.enter="handleQuery">
              <template #append>
                <el-button :loading="qaLoading" @click="handleQuery" icon="Search">查询</el-button>
              </template>
            </el-input>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { knowledgeApi, type KnowledgeDocVO } from "@/api/knowledge"
import { ElMessage, ElMessageBox } from "element-plus"
import MarkdownRenderer from "@/components/MarkdownRenderer.vue"
import {Delete} from "@element-plus/icons-vue";

const selectedFile = ref<File | null>(null)
const docTitle = ref("")
const uploading = ref(false)
const docList = ref<KnowledgeDocVO[]>([])
const qaQuestion = ref("")
const qaLoading = ref(false)
const qaList = ref<{ question: string; answer: string }[]>([])
const qaRef = ref<HTMLElement>()

onMounted(() => {
  loadDocs()
})

async function loadDocs() {
  try {
    const res = await knowledgeApi.list()
    docList.value = (res.data as any) || []
  } catch {
    // ignore
  }
}

function handleFileChange(file: any) {
  selectedFile.value = file.raw || file
}

async function handleUpload() {
  if (!selectedFile.value) return
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append("file", selectedFile.value)
    if (docTitle.value) formData.append("title", docTitle.value)
    else formData.append("title", selectedFile.value.name)
    await knowledgeApi.upload(formData)
    ElMessage.success("上传成功")
    selectedFile.value = null
    docTitle.value = ""
    await loadDocs()
  } catch {
    // error shown by interceptor
  } finally {
    uploading.value = false
  }
}

async function handleDeleteDoc(id: number) {
  try {
    await ElMessageBox.confirm("确定删除此文档？", "提示")
    await knowledgeApi.delete(id)
    ElMessage.success("删除成功")
    await loadDocs()
  } catch {
    // cancelled
  }
}

async function handleQuery() {
  const q = qaQuestion.value.trim()
  if (!q) return
  qaLoading.value = true
  try {
    const res = await knowledgeApi.query(q)
    const answer = (res.data as any) || res
    qaList.value.push({ question: q, answer })
    qaQuestion.value = ""
    setTimeout(() => {
      qaRef.value?.scrollTo({ top: qaRef.value.scrollHeight, behavior: "smooth" })
    }, 50)
  } catch {
    // ignore
  } finally {
    qaLoading.value = false
  }
}
</script>

<style scoped>
.upload-card, .doc-list-card {
  margin-bottom: 16px;
}
.upload-icon {
  margin-bottom: 8px;
}
.upload-text {
  color: #606266;
  font-size: 14px;
}
.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
.file-info {
  margin-top: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}
.doc-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.doc-item:last-child {
  border-bottom: none;
}
.doc-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}
.doc-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.doc-title {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.doc-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 6px;
  align-items: center;
}
.qa-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.qa-messages {
  flex: 1;
  overflow-y: auto;
  min-height: 400px;
  margin-bottom: 12px;
}
.qa-item {
  margin-bottom: 16px;
}
.qa-question, .qa-answer {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
.qa-text {
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
}
.qa-text.q {
  background: #e6f0ff;
  color: #303133;
}
.qa-text.a {
  background: #f0f0f5;
  color: #303133;
}
.qa-input {
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}
</style>