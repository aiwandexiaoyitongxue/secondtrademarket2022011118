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
    <!-- 这里可以加弹窗做用户信息编辑和查看 -->
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])

const fetchUsers = async () => {
  const res = await axios.get('/api/admin/users', {
    headers: { Authorization: localStorage.getItem('token') }
  })
  users.value = res.data
}

onMounted(fetchUsers)

async function disable(id) {
  await axios.post(`/api/admin/users/${id}/disable`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('已禁用')
  fetchUsers()
}

function edit(user) {
  // 打开弹窗，编辑用户信息，保存后调用PUT /api/admin/users/{id}
  ElMessage.info('请实现编辑弹窗')
}

function view(user) {
  // 打开弹窗，展示用户详细信息
  ElMessage.info('请实现查看弹窗')
}
</script>