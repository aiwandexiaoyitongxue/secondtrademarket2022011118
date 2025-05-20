<template>
  <div>
    <h3>我的订单</h3>
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="order-search-form" @submit.prevent="fetchOrders">
      <el-form-item label="订单编号">
        <el-input v-model="searchForm.orderNo" placeholder="请输入订单编号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
          <el-option label="全部" :value="''" />
          <el-option label="待付款" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="待收货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
          <el-option label="已退款" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchOrders">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 订单表格 -->
    <el-table :data="orders" v-loading="loading" style="width: 100%; margin-top: 16px;">
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column prop="createdTime" label="下单时间" width="180" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column prop="payAmount" label="实付金额" width="120" />
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row)">详情</el-button>
          <el-button size="small" v-if="row.status === 0" type="primary" @click="payOrder(row)">去支付</el-button>
          <el-button size="small" v-if="row.status === 0" type="danger" @click="cancelOrder(row)">取消</el-button>
          <el-button size="small" v-if="row.status === 2" type="success" @click="confirmReceipt(row)">确认收货</el-button>
          
          <el-button size="small" v-if="row.status === 2 || row.status === 3" @click="applyRefund(row)">申请退货</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <div v-if="currentOrder">
        <p>订单编号：{{ currentOrder.orderNo }}</p>
        <p>下单时间：{{ currentOrder.createdTime }}</p>
        <p>状态：{{ statusText(currentOrder.status) }}</p>
        <p>总金额：{{ currentOrder.totalAmount }}</p>
        <p>实付金额：{{ currentOrder.payAmount }}</p>
        <p>收货地址：{{ currentOrder.address }}</p>
        <el-table :data="currentOrder.items" style="width: 100%; margin-top: 10px;">
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column label="操作" width="120">
            <template #default="{ row: item }">
            <el-button
              size="small"
              v-if="currentOrder.status === 3 && !item.commented"
              type="warning"
              @click="commentOrder(currentOrder, item)"
            >评价</el-button>
        
          </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
     <el-dialog v-model="commentDialogVisible" title="订单评价" width="500px">
    <el-form :model="commentForm" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="commentForm.rating" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="commentForm.content" type="textarea" :rows="4" maxlength="200" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="commentDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitComment">提交</el-button>
    </template>
  </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted , computed} from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const searchForm = ref({
  orderNo: '',
  status: null,
  dateRange: []
})

const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.value.orderNo) params.orderNo = searchForm.value.orderNo
    if (searchForm.value.status !== null && searchForm.value.status !== undefined) params.status = searchForm.value.status
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      params.startDate = searchForm.value.dateRange[0]
      params.endDate = searchForm.value.dateRange[1]
    }
    const res = await request.get('/user/orders', { params })
    if (res.success) {
      orders.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取订单失败')
    }
  } catch (e) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.value = {
    orderNo: '',
    status: null,
    dateRange: []
  }
  fetchOrders()
}

const statusText = (status) => {
  switch (status) {
    case 0: return '待付款'
    case 1: return '待发货'
    case 2: return '待收货'
    case 3: return '已完成'
    case 4: return '已取消'
    case 5: return '已退款'
    default: return '未知'
  }
}
const statusTagType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'info'
    case 2: return 'primary'
    case 3: return 'success'
    case 4: return 'danger'
    case 5: return 'danger'
    default: return ''
  }
}

const viewDetail = async (row) => {
  try {
    const res = await request.get('/user/order/detail', { params: { id: row.id } })
    if (res.success) {
      currentOrder.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取订单详情失败')
    }
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  }
}

const cancelOrder = (row) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    // 调用后端取消订单接口
    await request.post('/user/order/cancel', { orderId: row.id })
    ElMessage.success('订单已取消')
    fetchOrders()
  }).catch(() => {})
}

const commentDialogVisible = ref(false)
const commentForm = ref({
  orderId: null,
  orderItemId: null, // 新增
  rating: 5,
  content: '',
});

// 提交评论
const submitComment = async () => {
  try {
    if (!commentForm.value.content) {
      ElMessage.warning('请输入评价内容');
      return;
    }
    const data = {
      orderId: commentForm.value.orderId,
      orderItemId: commentForm.value.orderItemId, // 新增
      rating: commentForm.value.rating,
      content: commentForm.value.content
    };
    const response = await request.post('/user/review/add', data);
    if (response.success) {
      ElMessage.success('评价成功');
      commentDialogVisible.value = false;
      fetchOrders();
      resetForm();
    } else {
      ElMessage.error(response.message || '评价失败');
    }
  } catch (error) {
    ElMessage.error('评价失败：' + (error.response?.data?.message || error.message));
  }
};

// 重置表单时也要重置文件列表
const resetForm = () => {
  commentForm.value = {
    orderId: null,
    rating: 5,
    content: '',
  };
};
const commentOrder = (order, item) => {
  commentForm.value = {
    orderId: order.id,
    orderItemId: item.id,
    rating: 5,
    content: ''
  }
  commentDialogVisible.value = true
}


const applyRefund = async (row) => {
  try {
    await ElMessageBox.confirm('确定要申请退货吗？', '提示', { type: 'warning' });
    
    // 先获取订单详情
    const detailRes = await request.get('/user/order/detail', { params: { id: row.id } });
    if (!detailRes.success) {
      ElMessage.error('获取订单详情失败');
      return;
    }
    
    const orderDetail = detailRes.data;
    if (!orderDetail.items || orderDetail.items.length === 0) {
      ElMessage.error('订单商品信息不存在');
      return;
    }
    
    // 如果只有一个商品，直接使用；如果有多个商品，需要让用户选择
    const orderItemId = orderDetail.items.length === 1 ? orderDetail.items[0].id : null;
    
    console.log('发送的数据:', {
      orderId: row.id,
      orderItemId: orderItemId
    });
  
    const params = new URLSearchParams();
    params.append('orderId', row.id);
    params.append('orderItemId', orderItemId);
    
    const res = await request.post('/user/order/return', params);
    if (res.success) {
      ElMessage.success('退货申请已提交');
      fetchOrders();
    } else {
      ElMessage.error(res.message || '退货申请失败');
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('退货申请失败');
  }
};
const confirmReceipt = (row) => {
  ElMessageBox.confirm('确认已收到货物？', '提示', {
    type: 'success'
  }).then(async () => {
    // 用 URLSearchParams 传参
    const params = new URLSearchParams();
    params.append('orderId', row.id);
    const res = await request.post('/user/order/confirm', params);
    if (res.success) {
      ElMessage.success('已确认收货');
      fetchOrders();
    } else {
      ElMessage.error(res.message || '确认收货失败');
    }
  }).catch(() => {});
}
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-search-form {
  margin-bottom: 16px;
}
</style>