<template>
  <div class="checkout-page">
    <h2>订单结算</h2>
    <el-card>
      <div style="margin-bottom: 16px;">
        <span>订单金额：</span>
        <b style="font-size: 20px; color: #409EFF;">￥{{ orderAmount }}</b>
      </div>
      <el-select v-model="selectedCouponId" placeholder="选择优惠券" style="width: 320px;">
        <el-option
          v-for="coupon in coupons"
          :key="coupon.id"
          :label="`满${coupon.minAmount}减${coupon.amount}（${coupon.startTime}~${coupon.endTime}）`"
          :value="coupon.id"
          :disabled="orderAmount < coupon.minAmount"
        />
      </el-select>
      <div v-if="selectedCoupon" style="margin: 12px 0; color: #67C23A;">
        已选优惠券：满{{ selectedCoupon.minAmount }}减{{ selectedCoupon.amount }}
      </div>
      <div style="margin: 16px 0;">
        <span>应付金额：</span>
        <b style="font-size: 20px; color: #f56c6c;">
          ￥{{ payAmount }}
        </b>
      </div>
      <el-button type="primary" @click="payOrder">立即支付</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

const coupons = ref([])
const selectedCouponId = ref(null)
const orderAmount = ref(100) // 假设订单金额100元

const selectedCoupon = computed(() =>
  coupons.value.find(c => c.id === selectedCouponId.value)
)

const payAmount = computed(() => {
  if (!selectedCoupon.value) return orderAmount.value
  return Math.max(0, orderAmount.value - selectedCoupon.value.amount)
})

onMounted(async () => {
  const res = await request.get('/user/coupons/available')
  if (res.success) {
    coupons.value = res.coupons
  }
})

async function payOrder() {
  const res = await request.post('/user/order/pay', {
    orderId: 123, // 实际订单ID
    couponId: selectedCouponId.value,
    orderAmount: orderAmount.value
  })
  if (res.success) {
    // 支付成功逻辑
    alert('支付成功！')
  }
}
</script>

<style scoped>
.checkout-page {
  max-width: 900px;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
</style>