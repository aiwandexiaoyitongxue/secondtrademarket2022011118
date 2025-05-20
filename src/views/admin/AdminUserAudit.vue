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
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const users = ref([])

const fetchUsers = async () => {
  const res = await request.get('/admin/users/pending')
  console.log('接口返回:', res)  // 看接口返回内容
  console.log('res 类型:', typeof res)  // 看 res 的类型
  console.log('res 是数组吗:', Array.isArray(res))  // 看 res 是不是数组
  users.value = res
}

onMounted(fetchUsers)

async function approve(id) {
  await request.post(`/admin/users/${id}/approve`)
  ElMessage.success('审核通过')
  fetchUsers()
}

async function reject(id) {
  await request.post(`/admin/users/${id}/reject`)
  ElMessage.success('已驳回')
  fetchUsers()
}
</script>