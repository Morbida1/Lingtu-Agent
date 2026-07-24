<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="avatar-card">
          <div class="avatar-section">
            <el-avatar :size="80" icon="UserFilled" />
            <h3>{{ userInfo?.nickname || userInfo?.username || "用户" }}</h3>
            <p>{{ userInfo?.email || "未设置邮箱" }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="info-card">
          <template #header>
            <span><el-icon><User /></el-icon> 基本信息</span>
          </template>
          <el-form :model="form" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" disabled />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="pwd-card">
          <template #header>
            <span><el-icon><Lock /></el-icon> 修改密码</span>
          </template>
          <el-form :model="pwdForm" label-width="100px">
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdSaving" @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { useAuthStore } from "@/stores/auth"
import { userApi } from "@/api/user"
import { ElMessage } from "element-plus"

const authStore = useAuthStore()
const userInfo = ref(authStore.user)
const saving = ref(false)
const pwdSaving = ref(false)

const form = reactive({ username: "", nickname: "", email: "" })
const pwdForm = reactive({ oldPassword: "", newPassword: "" })

onMounted(async () => {
  try {
    const res = await authStore.fetchProfile()
    if (res) {
      form.username = res.username
      form.nickname = res.nickname || ""
      form.email = res.email || ""
      userInfo.value = res
    }
  } catch {
    // ignore
  }
})

async function handleSave() {
  saving.value = true
  try {
    await userApi.updateProfile({ nickname: form.nickname })
    ElMessage.success("保存成功")
    await authStore.fetchProfile()
  } finally {
    saving.value = false
  }
}

async function handleChangePwd() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) {
    ElMessage.warning("请填写完整")
    return
  }
  pwdSaving.value = true
  try {
    await userApi.updatePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success("密码修改成功")
    pwdForm.oldPassword = ""
    pwdForm.newPassword = ""
  } finally {
    pwdSaving.value = false
  }
}
</script>

<style scoped>
.avatar-card {
  text-align: center;
}
.avatar-section {
  padding: 20px 0;
}
.avatar-section h3 {
  margin-top: 12px;
  font-size: 18px;
}
.avatar-section p {
  color: #909399;
  margin-top: 4px;
  font-size: 13px;
}
.info-card, .pwd-card {
  margin-bottom: 16px;
}
</style>
