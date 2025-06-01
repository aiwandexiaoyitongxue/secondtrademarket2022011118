<template>
  <div>
    <h3>我的评价</h3>
    <el-table :data="reviews" v-loading="loading" style="width: 100%; margin-top: 16px;">
      <el-table-column prop="productName" label="商品名称" width="160"/>
      <el-table-column prop="rating" label="商品评分" width="150">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled />
        </template>
        </el-table-column>
        <el-table-column prop="serviceRating" label="服务态度评分" width="150">
          <template #default="{ row }">
            <el-rate :model-value="row.serviceRating" disabled />
          </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容"width="300" />
      
      <el-table-column prop="createdTime" label="评价时间" width="200" />
    </el-table>
    <el-empty v-if="!loading && reviews.length === 0" description="暂无评价" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const reviews = ref([])
const loading = ref(false)

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/user/review')
    if (res.success) {
      reviews.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取评价失败')
    }
  } catch (e) {
    ElMessage.error('获取评价失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchReviews()
})
</script>