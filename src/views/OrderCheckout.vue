<template>
  <div class="checkout-container">
    <el-card>
      <h2>订单结算</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <!-- 交易方式 -->
        <el-form-item label="交易方式" prop="tradeType">
          <el-radio-group v-model="form.tradeType">
            <el-radio :value="'express'">快递</el-radio>
            <el-radio :value="'offline'">线下交易</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 收货地址 -->
        <el-form-item label="收货地址" prop="addressId">
          <el-select v-model="form.addressId" placeholder="请选择收货地址" style="width: 300px;">
            <el-option
              v-for="addr in addresses"
              :key="addr.id"
              :label="`${addr.receiverName} ${addr.receiverPhone} ${addr.detail}`"
              :value="addr.id"
            />
          </el-select>
          <el-button type="primary" @click="showAddAddress = true" size="small" style="margin-left: 10px;">新增地址</el-button>
        </el-form-item>
        <!-- 新增地址弹窗 -->
        <el-dialog v-model="showAddAddress" title="新增收货地址">
          <el-form :model="newAddress" label-width="80px">
            <el-form-item label="收货人">
              <el-input v-model="newAddress.receiverName" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="newAddress.receiverPhone" />
            </el-form-item>
            <el-form-item label="详细地址">
              <el-input v-model="newAddress.detail" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showAddAddress = false">取消</el-button>
            <el-button type="primary" @click="addAddress">保存</el-button>
          </template>
        </el-dialog>
        <!-- 支付方式 -->
        <el-form-item label="支付方式" prop="payMethod">
          <el-radio-group v-model="form.payMethod">
            <el-radio :value="'balance'">账户余额</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 优惠券 -->
        <el-form-item label="优惠券" v-if="coupons && coupons.length > 0">
          <div class="coupon-section">
            <el-select 
              v-model="form.couponId" 
              placeholder="请选择优惠券" 
              style="width: 200px;"
              :disabled="!form.useCoupon"
            >
              <el-option
                v-for="coupon in availableCoupons"
                :key="coupon.id"
                :label="`满${coupon.minAmount}减${coupon.amount}`"
                :value="coupon.id"
              >
                <div class="coupon-option">
                  <div>满{{ coupon.minAmount }}减{{ coupon.amount }}</div>
                  <div class="coupon-date">有效期至: {{ formatDate(coupon.endTime) }}</div>
                </div>
              </el-option>
            </el-select>
            <el-checkbox v-model="form.useCoupon">使用优惠券</el-checkbox>
            <div v-if="selectedCoupon" class="coupon-info">
              优惠金额: ¥{{ selectedCoupon.amount }}
            </div>
          </div>
        </el-form-item>
        <!-- 积分抵扣 -->
        <el-form-item label="积分抵扣">
          <span>可用积分：{{ user.points }}（100积分=1元）</span>
          <el-checkbox v-model="form.usePoints">使用积分抵扣</el-checkbox>
        </el-form-item>
        <!-- 订单金额 -->
        <el-form-item label="应付金额">
          <span style="color: #f56c6c; font-size: 20px;">¥{{ payAmount }}</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitOrder" :loading="submitting">确认支付</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const checkoutItems = ref([])
const totalAmount = ref(0)

const user = ref({ points: 0, walletBalance: 0 })
const addresses = ref([])
const coupons = ref([])
const form = ref({
  tradeType: 'express',
  addressId: null,
  payMethod: 'balance',
  couponId: null,
  useCoupon: false,
  usePoints: false
})
const showAddAddress = ref(false)
const newAddress = ref({
  receiverName: '',
  receiverPhone: '',
  detail: ''
})
const submitting = ref(false)

// 表单验证规则
const rules = {
  tradeType: [
    { required: true, message: '请选择交易方式', trigger: 'change' }
  ],
  addressId: [
    { required: true, message: '请选择收货地址', trigger: 'change' }
  ],
  payMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 计算可用优惠券
const availableCoupons = computed(() => {
  return coupons.value.filter(coupon => {
    return totalAmount.value >= coupon.minAmount
  })
})

// 获取选中的优惠券
const selectedCoupon = computed(() => {
  if (!form.value.useCoupon || !form.value.couponId) return null
  return coupons.value.find(c => c.id === form.value.couponId)
})

// 监听优惠券选择变化
watch(() => form.value.useCoupon, (newVal) => {
  if (!newVal) {
    form.value.couponId = null
  }
})

// 监听优惠券ID变化
watch(() => form.value.couponId, (newVal) => {
  if (newVal) {
    form.value.useCoupon = true
  }
})

// 计算应付金额
const payAmount = computed(() => {
  let total = totalAmount.value
  
  // 优惠券抵扣
  if (form.value.useCoupon && selectedCoupon.value) {
    total -= selectedCoupon.value.amount
  }
  
  // 积分抵扣
  if (form.value.usePoints && user.value.points >= 100) {
    const maxDeduct = Math.floor(user.value.points / 100)
    total -= maxDeduct
    if (total < 0) total = 0
  }
  
  return total.toFixed(2)
})

// 获取用户信息、地址、优惠券、商品信息
onMounted(async () => {
  try {
    // 从localStorage获取结算商品信息
    const items = localStorage.getItem('checkoutItems')
    if (items) {
      checkoutItems.value = JSON.parse(items)
      // 计算总金额
      totalAmount.value = checkoutItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
    } else {
      ElMessage.error('未找到结算商品信息')
      router.push('/user/cart')
      return
    }

    // 获取用户信息
    const userRes = await request.get('/user/info')
    if (userRes.code === 200 || userRes.success) {
      user.value = userRes.data
    }
    // 获取地址
    const addrRes = await request.get('/user/addresses')
    if (addrRes.code === 200 || addrRes.success) {
      addresses.value = addrRes.data
      // 默认地址
      const def = addresses.value.find(a => a.isDefault)
      if (def) form.value.addressId = def.id
    }
    // 获取优惠券
    const couponRes = await request.get('/user/coupons/available')
    if (couponRes.code === 200 || couponRes.success) {
      coupons.value = couponRes.coupons
    }
  } catch (error) {
    ElMessage.error('获取订单信息失败')
    console.error('获取订单信息失败:', error)
  }
})

// 新增地址
async function addAddress() {
  const res = await request.post('/user/address/add', newAddress.value)
  if (res.code === 200 || res.success) {
    addresses.value.push(res.data)
    form.value.addressId = res.data.id
    showAddAddress.value = false
    ElMessage.success('新增地址成功')
  }
}

// 提交订单
async function submitOrder() {
  if (!formRef.value) return
  
  try {
    // 表单验证
    await formRef.value.validate()
    
    submitting.value = true
    
    // 构建订单数据
    const orderData = {
      items: checkoutItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      })),
      tradeType: form.value.tradeType,
      addressId: form.value.addressId,
      payMethod: form.value.payMethod,
      usePoints: form.value.usePoints,
      couponId: form.value.useCoupon ? form.value.couponId : null
    }
    
    const res = await request.post('/api/orders/checkout', orderData)
    if (res.success) {
      // 清除localStorage中的结算商品信息
      localStorage.removeItem('checkoutItems')
      
      ElMessageBox.confirm(
        '下单成功！是否前往用户中心？',
        '操作成功',
        {
          confirmButtonText: '去用户中心',
          cancelButtonText: '留在当前页',
          type: 'success'
        }
      ).then(() => {
        router.push('/user/orders')
      })
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('提交订单失败，请检查表单信息')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.checkout-container {
  max-width: 700px;
  margin: 40px auto;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}

.coupon-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.coupon-option {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.coupon-date {
  font-size: 12px;
  color: #999;
}

.coupon-info {
  color: #67c23a;
  font-weight: bold;
}
</style>