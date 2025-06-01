<template>
  <div class="wallet-container">
    <h3>账户充值</h3>
    <el-card class="recharge-card">
      <el-form :model="form" label-width="80px" class="recharge-form" @submit.prevent="handleRecharge">
        <el-form-item label="充值金额">
          <el-input-number v-model="form.amount" :min="1" :max="100000" :step="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRecharge" :loading="loading">充值</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <h3>充值记录</h3>
    <el-table :data="records" style="width: 100%">
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="balance" label="变动后余额" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createdTime" label="时间" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const form = ref({
  amount: 100
})
const loading = ref(false)
const records = ref([])

const fetchRecords = async () => {
  try {
    const res = await request.get('/user/wallet/recharge/records')
    if (res.success) {
      records.value = res.records
    }
  } catch (error) {
    console.error('获取充值记录失败:', error)
    ElMessage.error('获取充值记录失败')
  }
}

const handleRecharge = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    ElMessage.warning('请输入正确的充值金额')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/user/wallet/recharge/do', {
      amount: form.value.amount
    })
    if (res.success) {
      ElMessage.success('充值成功')
      fetchRecords()
    } else {
      ElMessage.error(res.message || '充值失败')
    }
  } catch (e) {
    ElMessage.error('充值失败')
  } finally {
    loading.value = false
  }
}

onMounted(fetchRecords)
onActivated(fetchRecords)
</script>
<style scoped>
.wallet-container {
  max-width: 100%;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
.recharge-card {
  margin-bottom: 24px;
}
.recharge-form {
  display: flex;
  align-items: center;
}
</style>