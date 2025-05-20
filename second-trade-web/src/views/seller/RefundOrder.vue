<template>
  <div class="refund-order-container">
    <el-card class="refund-section">
      <h3 class="refund-title">退款申请处理</h3>
      <el-table :data="refundApplyList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="scope">{{ formatDate(scope.row.createdTime) }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">¥{{ scope.row.totalAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="showDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-card class="refund-section" style="margin-top: 30px;">
      <h3 class="refund-title">已退款订单</h3>
      <el-table :data="refundedList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="scope">{{ formatDate(scope.row.createdTime) }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="scope">¥{{ scope.row.totalAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="showDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px" :close-on-click-modal="false">
      <div v-if="currentOrder">
        <div style="margin-bottom: 10px;">
          <strong>订单编号：</strong>{{ currentOrder.orderNo }}<br>
          <strong>创建时间：</strong>{{ formatDate(currentOrder.createdTime) }}<br>
          <strong>订单金额：</strong>¥{{ currentOrder.totalAmount?.toFixed(2) }}<br>
          <strong>订单状态：</strong>{{ getStatusText(currentOrder.status) }}<br>
          <strong>退款原因：</strong>{{ currentOrder.refundReason || '无' }}
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
        <div v-if="currentOrder.status !== 5" class="refund-actions">
          <el-button type="success" @click="handleApprove(currentOrder)">同意退款</el-button>
          <el-button type="danger" @click="handleReject(currentOrder)">驳回</el-button>
        </div>
        <div v-if="currentOrder.rejectReason" class="reject-reason-row">
          <el-icon color="#f56c6c" style="vertical-align: middle;"><CircleCloseFilled /></el-icon>
          <span class="reject-reason-label">驳回原因：</span>
          <span class="reject-reason-content">{{ currentOrder.rejectReason }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    <!-- 新增：填写驳回原因的对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="填写驳回原因" width="400px" :close-on-click-modal="false">
      <el-input
        v-model="rejectReason"
        type="textarea"
        placeholder="请输入驳回原因"
        rows="4"
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRefundApplyOrders, getRefundedOrders, getOrderDetail, rejectRefund, deleteRejectReason } from '@/api/order'
import { CircleCloseFilled } from '@element-plus/icons-vue'
// import { getRefundApplyOrders, getRefundedOrders, getOrderDetail, approveRefund, rejectRefund } from '@/api/order'
const loading = ref(false)
const refundApplyList = ref([])
const refundedList = ref([])
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const formatDate = (date) => date ? new Date(date).toLocaleString() : ''
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
const showDetail = async (order) => {
  loading.value = true
  try {
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
const handleApprove = (order) => {
  // TODO: 调用后端接口同意退款
  ElMessage.success('已同意退款（模拟）')
  detailDialogVisible.value = false
}
const handleReject = (order) => {
  currentOrder.value = order
  rejectDialogVisible.value = true
}
const handleDeleteRejectReason = async (orderId) => {
  loading.value = true
  try {
    const res = await deleteRejectReason(orderId)
    if (res.code === 200) {
      ElMessage.success('驳回原因已删除')
      // 重新拉取订单详情
      const detailRes = await getOrderDetail(orderId)
      if (detailRes.code === 200) {
        currentOrder.value = detailRes.data
      }
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } finally {
    loading.value = false
  }
}
const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  loading.value = true
  try {
    const res = await rejectRefund(currentOrder.value.id, rejectReason.value)
    if (res.code === 200) {
      ElMessage.success('已驳回退款')
      rejectDialogVisible.value = false
      rejectReason.value = ''
      // 重新拉取订单详情，确保显示数据库最新的驳回原因
      const detailRes = await getOrderDetail(currentOrder.value.id)
      if (detailRes.code === 200) {
        currentOrder.value = detailRes.data
      }
    } else {
      ElMessage.error(res.message || '驳回退款失败')
    }
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  loading.value = true
  try {
    const applyRes = await getRefundApplyOrders()
    if (applyRes.code === 200) refundApplyList.value = applyRes.data || []
    const refundedRes = await getRefundedOrders()
    if (refundedRes.code === 200) refundedList.value = refundedRes.data || []
  } finally {
    loading.value = false
  }
})
</script>
<style scoped>
.refund-order-container {
  padding: 20px;
}
.refund-section {
  margin-bottom: 30px;
}
.refund-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}
.refund-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
.reject-reason-row {
  color: #f56c6c;
  font-weight: 500;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}
.reject-reason-label {
  margin-left: 4px;
  margin-right: 4px;
}
.reject-reason-content {
  word-break: break-all;
}
</style> 