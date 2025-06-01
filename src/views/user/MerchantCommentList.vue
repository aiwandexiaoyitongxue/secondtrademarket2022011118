<template>
  <div class="merchant-comment-list">
    <h2>我的商品评价与商家回复</h2>
    <el-table
      :data="comments"
      v-loading="loading"
      style="width: 100%; margin-top: 24px;"
      border
      :header-cell-style="{ background: '#f5f7fa', color: '#333', fontWeight: 'bold' }"
    >
      <el-table-column prop="productName" label="商品名称" width="200" />
      <el-table-column prop="rating" label="商品评分" width="120">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="serviceRating" label="服务评分" width="120">
        <template #default="{ row }">
          <el-rate :model-value="row.serviceRating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="merchantRating" label="商家对我的评分" width="150">
        <template #default="{ row }">
          <el-rate :model-value="row.merchantRating" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="我的评价" min-width="200">
        <template #default="{ row }">
          <span v-if="row.content">{{ row.content }}</span>
          <span v-else class="no-content">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="reply" label="商家回复" min-width="200">
        <template #default="{ row }">
          <span v-if="row.reply">{{ row.reply }}</span>
          <span v-else class="no-reply">暂无回复</span>
        </template>
      </el-table-column>
      <el-table-column prop="replyTime" label="回复时间" width="180">
        <template #default="{ row }">
          {{ row.replyTime ? formatTime(row.replyTime) : '--' }}
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="评价时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createdTime) }}
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
    <el-empty v-if="!loading && comments.length === 0" description="暂无评价记录" />
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

const fetchComments = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/review/list-by-user')
    if (res.success) {
      comments.value = res.data.records || res.data || []
      total.value = res.data.total || comments.value.length
    } else {
      ElMessage.error(res.message || '获取评价失败')
    }
  } catch (e) {
    ElMessage.error('获取评价失败')
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
.no-reply {
  color: #909399;
  font-style: italic;
}
.no-content {
  color: #909399;
  font-style: italic;
}
</style>