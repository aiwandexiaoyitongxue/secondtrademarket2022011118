<template>
  <div class="coupons-page">
    <h2>我的优惠券</h2>
    <el-table :data="coupons" style="width: 100%">
      <el-table-column prop="amount" label="面额" width="100">
        <template #default="scope">
          <span style="color: #f56c6c; font-weight: bold;">￥{{ scope.row.amount }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="minAmount" label="最低消费" width="120">
        <template #default="scope">
          满{{ scope.row.minAmount }}元
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="生效时间" width="180"/>
      <el-table-column prop="endTime" label="失效时间" width="180"/>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">未使用</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="info">已使用</el-tag>
          <el-tag v-else type="warning">已过期</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="coupons.length === 0" style="color: #aaa; margin-top: 20px;">暂无优惠券</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const coupons = ref([])

onMounted(async () => {
  const res = await request.get('/user/coupons/available')
  if (res.success) {
    coupons.value = res.coupons
  }
})
</script>

<style scoped>
.coupons-page {
  max-width: 80%;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
</style>