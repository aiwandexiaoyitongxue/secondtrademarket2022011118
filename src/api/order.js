import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/api/merchant/order/list',
    method: 'get',
    params
  })
}

// 获取订单统计信息
export function getOrderStatistics(merchantId) {
  return request({
    url: '/merchant/order/statistics',
    method: 'get',
    params: { merchantId }
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/api/merchant/order/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取退款申请列表
export function getRefundApplyOrders() {
  return request({
    url: '/merchant/order/refund/apply',
    method: 'get'
  })
}

// 获取已退款订单列表
export function getRefundedOrders() {
  return request({
    url: '/merchant/order/refund/refunded',
    method: 'get'
  })
}

// 处理退款申请
export function processRefund(orderId, approved, reason) {
  return request({
    url: `/merchant/order/refund/${orderId}/process`,
    method: 'post',
    params: { approved, reason }
  })
}

// 驳回退款申请
export function rejectRefund(orderId, reason) {
  return processRefund(orderId, false, reason)
}

// 删除驳回原因
export function deleteRejectReason(orderId) {
  return request({
    url: `/merchant/order/refund/${orderId}/reject-reason`,
    method: 'delete'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'post'
  })
}

// 确认收货
export function confirmReceipt(id) {
  return request({
    url: `/orders/${id}/confirm`,
    method: 'post'
  })
}

// 获取用户订单列表
export function getUserOrders(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
} 