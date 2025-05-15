<template>
  <el-card>
    <h2>平台交易费率管理</h2>
    <el-table :data="rates" style="width: 100%">
      <el-table-column prop="level" label="商家等级" width="100"/>
      <el-table-column prop="rate" label="费率" width="120">
        <template #default="scope">
          <el-input v-model="scope.row.rate" size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"/>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" size="small" @click="updateRate(scope.row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const rates = ref([])

// 添加获取费率列表的函数
const fetchRates = async () => {
  const res = await request.get('/admin/merchant-level-rate')
  rates.value = res
}

const updateRate = async (row) => {
  await request.post('/admin/merchant-level-rate/update', row)
  ElMessage.success('保存成功')
  fetchRates()  // 更新后重新获取列表
}

onMounted(fetchRates)  // 页面加载时获取费率列表
</script>