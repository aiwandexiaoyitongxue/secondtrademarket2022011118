<template>
  <el-card>
    <h2>待审核商家列表</h2>
    <el-table :data="merchants" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="userId" label="用户ID"/>
      <el-table-column prop="businessLicense" label="营业执照"/>
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

const merchants = ref([])

const fetchMerchants = async () => {
  const res = await axios.get('/api/admin/merchants/pending', {
    headers: { Authorization: localStorage.getItem('token') }
  })
  merchants.value = res.data
}

onMounted(fetchMerchants)

async function approve(id) {
  await axios.post(`/api/admin/merchants/${id}/approve`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('审核通过')
  fetchMerchants()
}

async function reject(id) {
  await axios.post(`/api/admin/merchants/${id}/reject`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('已驳回')
  fetchMerchants()
}
</script>