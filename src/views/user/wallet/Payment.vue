<template>
  <div class="wallet-container">
    <h3>支付记录</h3>
    <el-table :data="records" style="width: 100%">
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="balance" label="变动后余额" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="created_time" label="时间" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const records = ref([])

onMounted(async () => {
  const res = await request.get('/user/wallet/payment')
  if (res.success) {
    records.value = res.records
  }
})
</script>
.wallet-container {
  max-width: 80%;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}