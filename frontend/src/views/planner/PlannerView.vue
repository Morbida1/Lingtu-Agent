<template>
  <div class="planner-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span><el-icon><MapLocation /></el-icon> 智能旅行规划</span>
        </div>
      </template>

      <div class="planner-input">
        <el-input
          v-model="requestText"
          type="textarea"
          :rows="3"
          placeholder="请输入您的旅行需求，例如：&quot;去重庆3天，预算3000元，喜欢美食和自然风光&quot;"
        />
        <div class="planner-actions">
          <el-button type="primary" :loading="loading" @click="handlePlan" size="large">
            <el-icon><MagicStick /></el-icon> 开始规划
          </el-button>
        </div>
      </div>

      <div v-if="result" class="planner-result">
        <el-divider />
        <div class="result-content">
          <MarkdownRenderer :content="result" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { plannerApi } from "@/api/knowledge"
import { ElMessage } from "element-plus"
import MarkdownRenderer from "@/components/MarkdownRenderer.vue"

const requestText = ref("")
const loading = ref(false)
const result = ref("")

async function handlePlan() {
  if (!requestText.value.trim()) {
    ElMessage.warning("请输入旅行需求")
    return
  }
  loading.value = true
  result.value = ""
  try {
    const res = await plannerApi.plan(requestText.value.trim())
    result.value = res.data as any || res
  } catch (e: any) {
    ElMessage.error("规划失败: " + (e.message || "未知错误"))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.planner-page {
  max-width: 900px;
  margin: 0 auto;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}
.planner-input {
  margin-bottom: 8px;
}
.planner-actions {
  margin-top: 12px;
  text-align: center;
}
.result-content {
  padding: 8px 0;
}
</style>
