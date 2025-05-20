<template>
  <div class="wallet-balance-page">
    <el-card class="balance-card">
      <div class="balance-header">
        <span class="balance-title">账户余额</span>
        <el-tag type="info" effect="plain">暂不支持直接支付</el-tag>
      </div>
      <div class="balance-amount">￥{{ balance }}</div>
      <div class="balance-actions">
        <el-button type="primary" disabled>充值</el-button>
        <el-button type="success" disabled>提现</el-button>
      </div>
    </el-card>

    <el-card class="summary-card">
      <div>
        <span>当前积分：</span>
        <b>{{ points }}</b>
        <span style="margin-left: 16px; color: #999;">每100积分可抵1元</span>
      </div>
      <div style="margin-top: 12px;">
        <span>最近一条钱包记录：</span>
        <span v-if="lastRecord">
          {{ lastRecord.createdTime }}，
          <span v-if="lastRecord.type === 1">充值</span>
          <span v-else-if="lastRecord.type === 2">支付</span>
          <span v-else>其他</span>
          <span>￥{{ lastRecord.amount }}，余额：￥{{ lastRecord.balance }}</span>
          <span v-if="lastRecord.remark">（{{ lastRecord.remark }}）</span>
        </span>
        <span v-else>暂无记录</span>
      </div>
    </el-card>

    <el-card class="desc-card">
      <div>
        <b>余额说明：</b>
        <ul>
          <li>账户余额仅用于平台内消费和提现，暂不支持直接支付。</li>
          <li>充值、支付、提现等操作均会记录在钱包流水中。</li>
          <li>如有疑问请联系客服。</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const balance = ref('0.00')
const points = ref(0)
const lastRecord = ref(null)

onMounted(async () => {
  // 查询余额
  const res = await request.get('/user/wallet/balance')
  if (res.success) {
    balance.value = res.balance
  }
  // 查询积分
  const userRes = await request.get('/user/info')
  if (userRes.success) {
    points.value = userRes.data.points
  }
  // 查询最近一条钱包记录
  const recordRes = await request.get('/user/wallet/recharge') // 或/payment，或合并接口
  if (recordRes.success && recordRes.records.length > 0) {
    lastRecord.value = recordRes.records[0]
  }
})
</script>

<style scoped>
.wallet-balance-page {
  max-width: 900px;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
.balance-card {
  margin-bottom: 24px;
}
.balance-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.balance-title {
  font-size: 22px;
  font-weight: bold;
}
.balance-amount {
  font-size: 38px;
  color: #409EFF;
  margin: 18px 0 10px 0;
  font-weight: bold;
}
.balance-actions {
  margin-top: 10px;
}
.summary-card {
  margin-bottom: 24px;
}
.desc-card {
  background: #f9f9f9;
}
</style>