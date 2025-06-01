<template>
  <div class="merchant-wallet-container">
    <h2>商家钱包</h2>

    <el-card class="balance-card">
      <h3>当前余额</h3>
      <div class="balance-info">
        <span>¥ {{ (balance || 0).toFixed(2) }}</span>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="wallet-tabs">
      <el-tab-pane label="全部流水" name="all">
        <el-table :data="allRecords" v-loading="loading" style="width: 100%">
          <el-table-column prop="createdTime" label="时间" width="180" />
          <el-table-column prop="type" label="类型" width="120">
            <template #default="{ row }">
              <span>{{ recordTypeText(row.type) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="{ row }">
              <span :style="{ color: row.type === 1 || row.type === 5 || row.type === 6 ? 'green' : 'red' }">{{ (row.type === 1 || row.type === 5 || row.type === 6 ? '+' : '-') + row.amount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="balance" label="变动后余额" width="120" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="订单收入" name="income">
        <el-table :data="incomeRecords" v-loading="loading" style="width: 100%">
          <el-table-column prop="createdTime" label="时间" width="180" />
           <el-table-column prop="amount" label="收入金额" width="120">
            <template #default="{ row }">
               <span style="color: green;">+{{ row.amount.toFixed(2) }}</span>
            </template>
          </el-table-column>
           <el-table-column prop="balance" label="变动后余额" width="120" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-empty v-if="!loading && allRecords.length === 0" description="暂无钱包流水记录" />
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const allRecords = ref([])
const loading = ref(false)
const activeTab = ref('all')

const balance = computed(() =>
  allRecords.value
    .filter(record => [1, 5, 6].includes(record.type))
    .reduce((sum, record) => sum + Number(record.amount || 0), 0)
)

const incomeRecords = computed(() => allRecords.value.filter(record => record.type === 6))

const fetchWalletData = async () => {
  loading.value = true
  try {
    // 只获取所有钱包流水记录
    const res = await request.get('/seller/wallet/records/all')
    if (res.success && Array.isArray(res.data.records)) {
      allRecords.value = res.data.records.sort((a, b) => new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime());
    } else {
      allRecords.value = [];
    }
  } catch (e) {
    allRecords.value = [];
  } finally {
    loading.value = false;
  }
}

const recordTypeText = (type) => {
  switch (type) {
    case 1: return '充值';
    case 2: return '支付';
    case 3: return '提现';
    case 4: return '退款';
    case 5: return '管理员加款';
    case 6: return '订单收入';
    default: return '未知';
  }
}

onMounted(fetchWalletData);
onActivated(fetchWalletData);

</script>

<style scoped>
.merchant-wallet-container {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.balance-card {
  margin-bottom: 24px;
}

.balance-info span {
  font-size: 24px;
  font-weight: bold;
  color: #67c23a; /* Element Plus Success color */
}

.wallet-tabs :deep(.el-tabs__header) {
  margin-bottom: 24px;
}
</style> 