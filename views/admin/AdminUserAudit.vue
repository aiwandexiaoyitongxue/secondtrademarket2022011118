<template>
  <el-card>
    <h2>待审核用户列表</h2>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="username" label="用户名"/>
      <el-table-column prop="realName" label="真实姓名"/>
      <el-table-column prop="phone" label="手机号"/>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <span v-if="scope.row.status === 0">待审核</span>
          <span v-else-if="scope.row.status === 1">正常</span>
          <span v-else-if="scope.row.status === 2">禁用</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="success" size="small" @click="approve(scope.row.id)">通过</el-button>
          <el-button type="danger" size="small" @click="reject(scope.row.id)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])

const fetchUsers = async () => {
  const res = await axios.get('/api/admin/users/pending', {
    headers: { Authorization: localStorage.getItem('token') }
  })
  users.value = res.data
}

onMounted(fetchUsers)

async function approve(id) {
  await axios.post(`/api/admin/users/${id}/approve`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('审核通过')
  fetchUsers()
}

async function reject(id) {
  await axios.post(`/api/admin/users/${id}/reject`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('已驳回')
  fetchUsers()
}
</script>