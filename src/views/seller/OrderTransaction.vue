<template>
  <div class="order-transaction-container">
    <el-row :gutter="20" class="top-row">
      <el-col :span="12">
        <el-card class="top-card">
          <h3 class="top-title">订单筛选</h3>
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="订单状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 180px">
                <el-option label="全部" :value="-1"></el-option>
                <el-option label="待付款" :value="0"></el-option>
                <el-option label="待发货" :value="1"></el-option>
                <el-option label="待收货" :value="2"></el-option>
                <el-option label="已完成" :value="3"></el-option>
                <el-option label="已取消" :value="4"></el-option>
                <el-option label="已退款" :value="5"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="订单编号">
              <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="top-card">
          <h3 class="top-title">订单状态统计</h3>
          <el-row :gutter="0">
            <el-col :span="8" v-for="(count, key, idx) in orderStatusStats" :key="key">
              <div class="status-stat-item">
                <span class="stat-label">{{ getStatusTextByKey(key) }}</span>
                <span class="stat-count">{{ count }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="order-list-card">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>
      <div v-else-if="orderList.length === 0" class="empty-container">
        <el-empty description="暂无订单数据" />
      </div>
      <div v-else class="order-list">
        <el-table :data="orderList" style="width: 100%" v-loading="loading">
          <el-table-column prop="orderNo" label="订单编号" width="180" />
          <el-table-column prop="createdTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="订单金额" width="120">
            <template #default="scope">
              ¥{{ scope.row.totalAmount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="订单状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                @click="handleViewOrder(scope.row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-if="total > 0"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-card class="hot-product-card" style="margin-top: 20px;">
      <div class="stats-title">热销商品排行榜</div>
      <el-table :data="hotProductStats" size="small" border>
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
      </el-table>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px" :close-on-click-modal="false">
      <div v-if="currentOrder">
        <div style="margin-bottom: 10px;">
          <strong>订单编号：</strong>{{ currentOrder.orderNo }}<br>
          <strong>创建时间：</strong>{{ formatDate(currentOrder.createdTime) }}<br>
          <strong>订单金额：</strong>¥{{ currentOrder.totalAmount?.toFixed(2) }}<br>
          <strong>订单状态：</strong>{{ getStatusText(currentOrder.status) }}
        </div>
        <el-table :data="currentOrder.items || []" size="small" border style="margin-bottom: 10px;">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="数量" width="60" />
          <el-table-column prop="price" label="单价" width="80">
            <template #default="scope">¥{{ scope.row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="小计" width="80">
            <template #default="scope">¥{{ scope.row.totalAmount?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, inject } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderList, getOrderDetail, updateOrderStatus, getOrderStatistics } from '@/api/order'

const route = useRoute()
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 使用注入的 currentMenu
const currentMenu = inject('currentMenu', 'ship')

// 根据当前菜单确定页面标题
const pageTitle = computed(() => {
  switch (currentMenu) {
    case 'ship': return '待发货订单'
    case 'order-manage': return '订单管理'
    case 'refund': return '售后服务'
    default: return '订单列表'
  }
})

// 搜索表单
const searchForm = reactive({ status: -1, orderNo: '', merchantId: null })

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 0: return '待付款'
    case 1: return '待发货'
    case 2: return '待收货'
    case 3: return '已完成'
    case 4: return '已取消'
    case 5: return '已退款'
    default: return '未知状态'
  }
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case 0: return 'warning'
    case 1: return 'primary'
    case 2: return 'success'
    case 3: return 'info'
    case 4: return 'danger'
    case 5: return 'danger'
    default: return 'info'
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (searchForm.status !== -1) {
      params.status = searchForm.status
    }
    if (searchForm.orderNo) {
      params.orderNo = searchForm.orderNo
    }
    const res = await getOrderList(params)
    if (res.success) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
      console.log('orderList:', orderList.value)
    } else {
      ElMessage.error(res.message || '获取订单列表失败')
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOrderList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.status = -1
  searchForm.orderNo = ''
  currentPage.value = 1
  loadOrderList()
}

// 分页
const handlePageChange = (page) => {
  currentPage.value = page
  loadOrderList()
}

// 查看订单详情
const handleViewOrder = async (order) => {
  try {
    loading.value = true
    const res = await getOrderDetail(order.id)
    if (res.success) {
      currentOrder.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取订单详情失败')
    }
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 获取订单状态统计
const loadOrderStats = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.id) return
  const res = await getOrderStatistics(userInfo.id)
  if (res.success) {
    orderStatusStats.value = res.data.statusCounts || {}
  }
}

// 预留：获取热销商品统计
const loadHotProductStats = async () => {
  // TODO: 调用后端接口获取热销商品统计
  // hotProductStats.value = ...
}

onMounted(() => {
  loadOrderStats()
  loadHotProductStats()
  loadOrderList()
})

function getStatusTextByKey(key) {
  const map = {
    status0: '待付款',
    status1: '待发货',
    status2: '待收货',
    status3: '已完成',
    status4: '已取消',
    status5: '已退款'
  }
  return map[key] || key
}

const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const orderStatusStats = ref({})
const hotProductStats = ref([])
</script>

<style scoped>
.order-transaction-container {
  padding: 20px;
}

.top-row {
  margin-bottom: 20px;
}

.top-card {
  min-height: 120px;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.top-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
  text-align: left;
}

.filter-header,
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-header h3,
.list-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.loading-container,
.empty-container {
  padding: 30px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.order-list {
  margin-top: 20px;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-table .cell) {
  white-space: nowrap;
}

.separator {
  margin: 0 8px;
  color: #909399;
}

:deep(.el-date-editor--daterange) {
  width: 360px;
}

:deep(.el-input-number) {
  width: 130px;
}

.search-form .el-form-item {
  min-width: 180px;
}

.status-stats-col {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}
.status-stats-card {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}
.stats-title {
  font-weight: bold;
  margin-bottom: 10px;
}
.status-stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}
.stat-label {
  font-size: 13px;
  color: #666;
}
.stat-count {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
  margin-left: 2px;
}
.hot-product-card {
  margin-top: 20px;
}
</style> 