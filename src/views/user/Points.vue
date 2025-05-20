<template>
  <div class="points-page">
    <h2>我的积分</h2>
    <el-card class="points-balance">
      <div>
        <span>当前积分：</span>
        <b style="font-size: 24px; color: #409EFF;">{{ points }}</b>
      </div>
      <div style="margin-top: 8px; color: #888;">
        每消费1元得1积分，100积分可抵1元现金
      </div>
    </el-card>

    <el-card class="points-detail" style="margin-top: 24px;">
      <div style="font-weight: bold; margin-bottom: 10px;">积分明细</div>
      <el-table :data="records" v-if="records.length > 0" style="width: 100%">
        <el-table-column prop="createdTime" label="时间" width="180"/>
        <el-table-column prop="change" label="变动积分" width="120"/>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <span v-if="scope.row.change > 0" style="color: #67C23A;">获得</span>
            <span v-else style="color: #F56C6C;">消费</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"/>
      </el-table>
      <div v-else style="color: #aaa;">暂无积分明细</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const points = ref(0)
const records = ref([])

onMounted(async () => {
  // 获取积分余额
  const res = await request.get('/user/points')
  if (res.success) {
    points.value = res.points
  }
  // 获取积分明细
  const recRes = await request.get('/user/points/records')
  if (recRes.success) {
    records.value = recRes.records
  }
})
</script>

<style scoped>
.points-page {
  max-width: 900px;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
.points-balance {
  margin-bottom: 24px;
}
</style>