<template>
  <div class="order-transaction-container">
    <el-card class="filter-card">
      <div class="filter-header">
        <h3>订单筛选</h3>
      </div>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <!-- 发货页面只显示订单编号搜索 -->
        <template v-if="currentMenu === 'ship'">
          <el-form-item label="订单编号">
            <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable></el-input>
          </el-form-item>
        </template>
        
        <!-- 订单管理页面显示状态筛选和订单编号搜索 -->
        <template v-else-if="currentMenu === 'order-manage'">
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
        </template>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="order-list-card">
      <div class="list-header">
        <h3>{{ pageTitle }}</h3>
      </div>

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

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px" :close-on-click-modal="false">
      <div v-if="currentOrder">
        <div style="margin-bottom: 10px;">
          <strong>订单编号：</strong>{{ currentOrder.orderNo }}<br>
          <strong>创建时间：</strong>{{ formatDate(currentOrder.createdTime) }}<br>
          <strong>订单金额：</strong>¥{{ currentOrder.totalAmount?.toFixed(2) }}<br>
          <strong>订单状态：</strong>{{ getStatusText(currentOrder.status) }}
        </div>
        <el-table :data="currentOrder.orderItems || []" size="small" border style="margin-bottom: 10px;">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="数量" width="60" />
          <el-table-column prop="price" label="单价" width="80">
            <template #default="scope">¥{{ scope.row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="小计" width="80">
            <template #default="scope">¥{{ scope.row.totalAmount?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        <!-- 仅待发货状态下显示发货按钮 -->
        <div v-if="currentOrder.status === 1">
          <el-button type="primary" @click="confirmDelivery">确认发货</el-button>
        </div>
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
import { getOrderList, getOrderDetail, updateOrderStatus } from '@/api/order'

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
const searchForm = reactive({
  status: -1,
  orderNo: '',
  merchantId: null
})

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
    // 从localStorage获取当前商家ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    searchForm.merchantId = userInfo.id
    
    if (!searchForm.merchantId) {
      ElMessage.warning('未检测到商家信息，请重新登录')
      loading.value = false
      return
    }
    
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      merchantId: searchForm.merchantId
    }
    
    // 根据当前菜单设置查询参数
    if (currentMenu === 'ship') {
      // 发货页面固定状态为待发货，添加订单编号搜索
      params.status = 1
      if (searchForm.orderNo) {
        params.orderNo = searchForm.orderNo
      }
    } else if (currentMenu === 'order-manage') {
      // 订单管理页面使用状态筛选
      if (searchForm.status !== -1) {
        params.status = searchForm.status
      }
      // 添加订单编号搜索
      if (searchForm.orderNo) {
        params.orderNo = searchForm.orderNo
      }
    }
    
    console.log('订单查询参数:', params)
    const res = await getOrderList(params)
    if (res.code === 200) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
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
  if (currentMenu === 'ship') {
    searchForm.orderNo = ''
  } else if (currentMenu === 'order-manage') {
    searchForm.status = -1
    searchForm.orderNo = ''
  }
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
    if (res.code === 200) {
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

// 确认发货
const confirmDelivery = async () => {
  try {
    loading.value = true
    const res = await updateOrderStatus(currentOrder.value.id, 2)
    if (res.code === 200) {
      ElMessage.success('发货成功，订单状态已更新为待收货')
      detailDialogVisible.value = false
      loadOrderList()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch (e) {
    ElMessage.error('发货失败')
  } finally {
    loading.value = false
  }
}

// 组件加载时获取订单列表
onMounted(() => {
  loadOrderList()
})

const detailDialogVisible = ref(false)
const currentOrder = ref(null)
</script>

<style scoped>
.order-transaction-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
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
</style> 