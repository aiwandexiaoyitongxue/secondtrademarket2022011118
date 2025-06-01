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
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const merchants = ref([])

const fetchMerchants = async () => {
  try {
    const res = await request.get('/admin/merchant/pending')
    console.log('获取到的商家数据:', res)
    merchants.value = Array.isArray(res) ? res : []
  } catch (error) {
    console.error('获取商家数据失败:', error)
    ElMessage.error('获取商家数据失败')
    merchants.value = []
  }
}

onMounted(fetchMerchants)
async function approve(id) {
  try {
    await request.post(`/admin/merchant/${id}/approve`)
    ElMessage.success('审核通过')
    fetchMerchants()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败')
  }
}

async function reject(id) {
  try {
    await request.post(`/admin/merchant/${id}/reject`)
    ElMessage.success('已驳回')
    fetchMerchants()
  } catch (error) {
    console.error('驳回失败:', error)
    ElMessage.error('驳回失败')
  }
}
</script>