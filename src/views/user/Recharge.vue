<template>
  <div class="recharge-page">
    <h2>账户充值</h2>
    <el-card>
      <el-form :model="form" label-width="80px" class="recharge-form">
        <el-form-item label="充值金额">
          <el-input-number v-model="form.amount" :min="1" :max="100000" :step="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRecharge" :loading="loading">充值</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="recharge-history" style="margin-top: 32px;">
      <div style="font-weight: bold; margin-bottom: 10px;">充值记录</div>
      <el-table :data="records" v-if="records.length > 0" style="width: 100%">
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="createdTime" label="时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">成功</el-tag>
            <el-tag v-else-if="scope.row.status === 0" type="info">待处理</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-else style="color: #aaa;">暂无充值记录</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const form = ref({
  amount: 100
})
const loading = ref(false)
const records = ref([])

const fetchRecords = async () => {
  const res = await request.get('/user/wallet/recharge-records')
  if (res.success) {
    records.value = res.records
  }
}

const handleRecharge = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    ElMessage.warning('请输入正确的充值金额')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/user/wallet/recharge', {
      amount: form.value.amount
    })
    if (res.success) {
      ElMessage.success('充值成功')
      fetchRecords()
      // 你可以在这里加刷新余额的逻辑
    } else {
      ElMessage.error(res.message || '充值失败')
    }
  } catch (e) {
    ElMessage.error('充值失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.recharge-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 32px 0;
}
.recharge-form {
  margin-bottom: 0;
}
.recharge-history {
  margin-top: 32px;
}
</style>