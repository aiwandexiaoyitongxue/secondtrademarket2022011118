<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            {{ getRoleName(scope.row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            {{ getStatusName(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="普通用户" :value="1" />
            <el-option label="商家" :value="2" />
            <el-option label="管理员" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

// 表格数据
const tableData = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const form = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 1
})

// 获取用户列表
const getList = async () => {
  try {
    const res = await userApi.getList()
    tableData.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    role: 1
  }
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', {
      type: 'warning'
    })
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (dialogType.value === 'add') {
      await userApi.create(form.value)
      ElMessage.success('新增成功')
    } else {
      await userApi.update(form.value.id, form.value)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  }
}

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    1: '普通用户',
    2: '商家',
    3: '管理员'
  }
  return roleMap[role] || '未知'
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '禁用',
    1: '正常'
  }
  return statusMap[status] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 