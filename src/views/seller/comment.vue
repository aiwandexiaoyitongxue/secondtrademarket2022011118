<template>
  <div class="comment-container">
    <el-card class="score-summary">
      <h3>评分汇总</h3>
      <div class="score-info">
        <span>平均分：{{ avgScore }}</span>
        <span>评价总数：{{ totalCount }}</span>
      </div>
    </el-card>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="商品评价" name="product">
        <el-table :data="productReviews" v-loading="loadingProduct">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="username" label="买家" />
          <el-table-column prop="rating" label="评分">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="serviceRating" label="服务分">
            <template #default="scope">
              <el-rate v-model="scope.row.serviceRating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="createdTime" label="评价时间" />
          <el-table-column prop="reply" label="我的回复" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="replyProductReview(scope.row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="商家评价" name="merchant">
        <el-table :data="merchantReviews" v-loading="loadingMerchant">
          <el-table-column prop="username" label="买家" />
          <el-table-column prop="rating" label="评分">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="createdTime" label="评价时间" />
          <el-table-column prop="reply" label="我的回复" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="replyMerchantReview(scope.row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="未回复" name="unreplied">
        <el-table :data="unrepliedComments" v-loading="loading">
          <el-table-column prop="orderNo" label="订单编号" />
          <el-table-column prop="buyerName" label="买家" />
          <el-table-column prop="rating" label="评分">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="createdTime" label="评价时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="replyComment(scope.row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:currentPage="currentPage"
            v-model:pageSize="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-tab-pane>
      <el-tab-pane label="已回复" name="replied">
        <el-table :data="repliedComments" v-loading="loading">
          <el-table-column prop="orderNo" label="订单编号" />
          <el-table-column prop="buyerName" label="买家" />
          <el-table-column prop="rating" label="评分">
            <template #default="scope">
              <el-rate v-model="scope.row.rating" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="reply" label="我的回复" />
          <el-table-column prop="replyTime" label="回复时间" />
        </el-table>
        <div class="pagination">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
    <el-dialog v-model="replyDialogVisible" title="回复评价" width="400px">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
        <el-form-item label="用户评分">
          <el-rate v-model="replyForm.merchantRating" :texts="['很差', '较差', '一般', '较好', '很好']" show-text />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('product')
const replyDialogVisible = ref(false)
const replyForm = ref({
  reply: '',
  merchantRating: 5
})
const unrepliedComments = ref([])
const repliedComments = ref([])
const avgScore = ref(0)
const totalCount = ref(0)
const loading = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentReview = ref(null)
const productReviews = ref([])
const merchantReviews = ref([])
const loadingProduct = ref(false)
const loadingMerchant = ref(false)

// 获取评价列表
const fetchReviews = async () => {
  loading.value = true
  try {
    const hasReply = activeTab.value === 'replied'
    console.log('Fetching reviews with params:', {
      page: currentPage.value,
      size: pageSize.value,
      hasReply
    })
    const res = await request.get('/api/merchant/review/list-merchant-reviews', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        hasReply
      }
    })
    console.log('Reviews response:', res)
    if (res.success) {
      const { records, total: totalCount } = res.data
      if (hasReply) {
        repliedComments.value = records
      } else {
        unrepliedComments.value = records
      }
      total.value = totalCount
    } else {
      ElMessage.error(res.message || '获取评价列表失败')
    }
  } catch (error) {
    console.error('Error fetching reviews:', error)
    ElMessage.error(error.response?.data?.message || '获取评价列表失败')
  } finally {
    loading.value = false
  }
}

// 获取评价统计
const fetchStatistics = async () => {
  try {
    const res = await request.get('/api/merchant/review/statistics')
    if (res.success) {
      const { avgRating, totalReviews } = res.data
      avgScore.value = avgRating
      totalCount.value = totalReviews
    }
  } catch (error) {
    ElMessage.error('获取评价统计失败')
  }
}

// 回复评价
const replyComment = (row) => {
  currentReview.value = row
  replyDialogVisible.value = true
  replyForm.value = {
    reply: '',
    merchantRating: 5
  }
}

const submitReply = async () => {
  if (!replyForm.value.reply.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  submitting.value = true
  try {
    const res = await request.post(`/user/review/${currentReview.value.id}/reply`, {
      reply: replyForm.value.reply,
      merchantRating: replyForm.value.merchantRating
    })
    if (res.success) {
      ElMessage.success('回复成功')
      replyDialogVisible.value = false
      if (activeTab.value === 'product') {
        fetchProductReviews()
      } else if (activeTab.value === 'merchant') {
        fetchMerchantReviews()
      } else {
        fetchReviews()
      }
      fetchStatistics()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '回复失败')
  } finally {
    submitting.value = false
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchReviews()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchReviews()
}

watch(activeTab, (tab) => {
  if (tab === 'product') {
    fetchProductReviews()
  } else if (tab === 'merchant') {
    fetchMerchantReviews()
  } else {
    fetchReviews()
  }
})

const fetchProductReviews = async () => {
  loadingProduct.value = true
  try {
    const res = await request.get('/user/review/list-by-merchant')
    if (res.success) {
      productReviews.value = res.data
    }
  } catch (e) {
    ElMessage.error('获取商品评价失败')
  } finally {
    loadingProduct.value = false
  }
}

const fetchMerchantReviews = async () => {
  loadingMerchant.value = true
  try {
    const res = await request.get('/merchant/review/list-merchant-reviews')
    if (res.success) {
      merchantReviews.value = res.data
    }
  } catch (e) {
    ElMessage.error('获取商家评价失败')
  } finally {
    loadingMerchant.value = false
  }
}

const replyProductReview = (row) => {
  currentReview.value = row
  replyDialogVisible.value = true
  replyForm.value = {
    reply: row.reply || '',
    merchantRating: row.merchantRating || 5
  }
}

const replyMerchantReview = (row) => {
  currentReview.value = row
  replyDialogVisible.value = true
  replyForm.value = {
    reply: row.reply || '',
    merchantRating: row.merchantRating || 5
  }
}

onMounted(() => {
  fetchProductReviews()
  fetchMerchantReviews()
  fetchStatistics()
})
</script>

<style scoped>
.comment-container { 
  padding: 20px; 
}
.score-summary { 
  margin-bottom: 20px; 
}
.score-info { 
  display: flex; 
  gap: 30px; 
  font-size: 16px; 
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 