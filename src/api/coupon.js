import request from '@/utils/request'

// 获取可领取的优惠券模板
export function getAvailableToClaimCoupons() {
  return request.get('/api/user/coupons/available-to-claim')
}

// 领取优惠券
export function claimCoupon(templateId) {
  return request.post('/api/user/coupons/claim', { templateId })
}

// 获取用户的优惠券列表
export function getUserCoupons() {
  return request.get('/api/user/coupons/list')
}