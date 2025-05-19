import request from '@/utils/request'

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/api/orders/list',
    method: 'get',
    params
  })
}

// 获取订单统计信息
export function getOrderStatistics(merchantId) {
  return request({
    url: '/api/orders/statistics',
    method: 'get',
    params: { merchantId }
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/api/orders/${id}`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/api/orders/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取退款申请订单列表
export function getRefundApplyOrders() {
  return request({
    url: '/api/orders/refund/apply',
    method: 'get'
  })
}

// 获取已退款订单列表
export function getRefundedOrders() {
  return request({
    url: '/api/orders/refund/refunded',
    method: 'get'
  })
} 