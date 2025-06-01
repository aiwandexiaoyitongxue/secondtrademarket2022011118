<template>
  <el-card>
    <h2>用户管理</h2>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="username" label="用户名"/>
      <el-table-column prop="realName" label="真实姓名"/>
      <el-table-column prop="phone" label="手机号"/>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <span v-if="scope.row.status === 1">正常</span>
          <span v-else-if="scope.row.status === 2">禁用</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button type="primary" size="small" @click="edit(scope.row)">修改</el-button>
          <el-button type="danger" size="small" @click="disable(scope.row.id)">禁用</el-button>
          <el-button size="small" @click="view(scope.row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户信息"
      width="500px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled/>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName"/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option :value="1" label="正常"/>
            <el-option :value="2" label="禁用"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="用户详细信息"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ viewForm.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ viewForm.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewForm.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <span v-if="viewForm.status === 1">正常</span>
          <span v-else-if="viewForm.status === 2">禁用</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const users = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)

// 编辑表单数据
const editForm = ref({
  id: '',
  username: '',
  realName: '',
  phone: '',
  status: 1
})

// 查看表单数据
const viewForm = ref({
  id: '',
  username: '',
  realName: '',
  phone: '',
  status: 1
})

// 获取用户列表
const fetchUsers = async () => {
  const res = await request.get('/admin/users/all')
  users.value = res
}

// 禁用用户
async function disable(id) {
  try {
    console.log('开始禁用用户:', id)
    const res = await request.post(`/admin/users/${id}/disable`)
    console.log('禁用用户响应:', res)
    
    if (res.success) {
      ElMessage.success('已禁用')
      fetchUsers()
    } else {
      console.error('禁用用户失败，响应:', res)
      ElMessage.error(res.message || '禁用失败')
    }
  } catch (error) {
    console.error('禁用用户失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status
    })
    
    if (error.response?.status === 500) {
      ElMessage.error(error.response?.data?.message || '服务器内部错误，请稍后重试')
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '禁用失败')
    }
  }
}

// 编辑用户
function edit(user) {
  editForm.value = { ...user }
  dialogVisible.value = true
}

// 保存编辑
async function handleSave() {
  try {
    await request.put(`/admin/users/${editForm.value.id}`, {
      username: editForm.value.username,
      realName: editForm.value.realName,
      phone: editForm.value.phone,
      email: editForm.value.email,
      city: editForm.value.city,
      gender: editForm.value.gender,
      role: editForm.value.role,
      status: editForm.value.status
    })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('保存失败:', error)
  }
}

// 查看用户
function view(user) {
  viewForm.value = { ...user }
  viewDialogVisible.value = true
}

onMounted(fetchUsers)
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>