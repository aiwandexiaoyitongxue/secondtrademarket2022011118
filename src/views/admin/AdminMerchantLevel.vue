<template>
  <el-card>
    <h2>卖家等级调整</h2>
    <el-table :data="merchants" style="width: 100%">
      <el-table-column prop="id" label="商家ID" width="100"/>
      <el-table-column prop="userId" label="用户ID" width="100"/>
      <el-table-column prop="level" label="当前等级" width="100"/>
      <el-table-column prop="satisfactionRate" label="满意度" width="100"/>
      <el-table-column prop="totalSales" label="总交易额" width="120"/>
      <!-- 如果有好评率等字段也可以加 -->
      <el-table-column label="调整等级" width="180">
        <template #default="scope">
          <el-select v-model="scope.row.level" placeholder="选择等级" size="small">
            <el-option v-for="n in 5" :key="n" :label="n" :value="n"/>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" size="small" @click="updateLevel(scope.row)">保存</el-button>
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
  const res = await request.get('/api/admin/merchant/all')
  merchants.value = res
}

const updateLevel = async (row) => {
  await request.post(`/api/admin/merchant/${row.id}/level`, { level: row.level })
  ElMessage.success('等级已更新')
  fetchMerchants()
}

onMounted(fetchMerchants)
</script>