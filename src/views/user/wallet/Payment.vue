<template>
  <div class="wallet-container">
    <h3>支付记录</h3>
    <el-table :data="records" style="width: 100%">
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="balance" label="变动后余额" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createdTime" label="时间" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const records = ref([])

const fetchRecords = async () => {
  try {
    const res = await request.get('/user/wallet/payment/records')
    if (res.success) {
      records.value = res.records
    }
  } catch (error) {
    console.error('获取支付记录失败:', error)
    ElMessage.error('获取支付记录失败')
  }
}

onMounted(fetchRecords)
</script>

<style scoped>
.wallet-container {
  max-width: 100%;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
</style>