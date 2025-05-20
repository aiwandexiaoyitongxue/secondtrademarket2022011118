<template>
  <div class="merchant-comment-list">
    <h2>商家对我的评价</h2>
    <el-table
      :data="comments"
      v-loading="loading"
      style="width: 100%; margin-top: 24px;"
      border
      :header-cell-style="{ background: '#f5f7fa', color: '#333', fontWeight: 'bold' }"
    >
      <el-table-column label="商家" width="180">
        <template #default="{ row }">
          <div class="merchant-info">
            <el-avatar :src="row.merchantAvatar || defaultAvatar" size="small" />
            <span class="merchant-name">{{ row.merchantName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="orderNo" label="订单编号" width="160" />
      <el-table-column prop="rating" label="服务评分" width="120">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="200" />
      <el-table-column prop="createdTime" label="评价时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="reply" label="我的回复" min-width="180" v-if="showReply">
        <template #default="{ row }">
          <span v-if="row.reply">{{ row.reply }}</span>
          <span v-else class="no-reply">--</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total > pageSize"
      style="margin-top: 24px; text-align: right;"
      background
      layout="prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      :current-page="page"
      @current-change="handlePageChange"
    />
    <el-empty v-if="!loading && comments.length === 0" description="暂无商家对你的评价" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const comments = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const showReply = ref(false) // 如果你有“我的回复”功能可以设为true
const defaultAvatar = '/static/images/merchant-default.png' // 你可以换成自己的默认头像

const fetchComments = async () => {
  loading.value = true
  try {
    // 假设后端支持分页
    const res = await request.get('/user/merchant-comments', {
      params: { page: page.value, pageSize }
    })
    if (res.success) {
      comments.value = res.data.records || res.data || []
      total.value = res.data.total || comments.value.length
    } else {
      ElMessage.error(res.message || '获取商家评价失败')
    }
  } catch (e) {
    ElMessage.error('获取商家评价失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  page.value = val
  fetchComments()
}

const formatTime = (time) => {
  if (!time) return '--'
  // 你可以用 dayjs/Date等格式化
  return new Date(time).toLocaleString()
}

onMounted(() => {
  fetchComments()
})
</script>
<style scoped>
.merchant-comment-list {
  background: #fff;
  padding: 32px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px #f0f1f2;
}
.merchant-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.merchant-name {
  font-weight: 500;
  color: #333;
}
.no-reply {
  color: #bbb;
}
</style>