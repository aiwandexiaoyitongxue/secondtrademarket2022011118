<template>
  <div class="cart-container">
    <h3>购物车</h3>
    <el-table
      :data="cartItems"
      v-loading="loading"
      style="width: 100%; margin-top: 16px;"
      @selection-change="handleSelectionChange"
      ref="cartTable"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="price" label="单价" width="100" />
      <el-table-column prop="quantity" label="数量" width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" @change="updateQuantity(row)" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120">
        <template #default="{ row }">
          {{ (row.price * row.quantity).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="removeItem(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && cartItems.length === 0" description="购物车为空" />

    <div style="margin-top: 20px; text-align: right;">
      <span style="font-size: 18px; font-weight: bold;">总计：￥{{ totalAmount }}</span>
      <el-button
        type="primary"
        :disabled="selectedItems.length === 0"
        @click="checkout"
        style="margin-left: 20px;"
      >去结算</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const cartItems = ref([])
const loading = ref(false)
const selectedItems = ref([])

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/cart')
    if (res.success) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取购物车失败')
    }
  } catch (e) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (row) => {
  try {
    await request.post('/user/cart/update', { id: row.id, quantity: row.quantity })
    ElMessage.success('数量已更新')
    fetchCart()
  } catch (e) {
    ElMessage.error('更新数量失败')
  }
}

const removeItem = async (id) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await request.post('/user/cart/delete', { id })
    ElMessage.success('已删除')
    fetchCart()
  }).catch(() => {})
}

const handleSelectionChange = (val) => {
  selectedItems.value = val
}

const checkout = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }
  try {
    const ids = selectedItems.value.map(item => item.id)
    await request.post('/user/cart/checkout', { ids })
    ElMessage.success('结算成功，订单已生成')
    fetchCart()
  } catch (e) {
    ElMessage.error('结算失败')
  }
}

const totalAmount = computed(() =>
  selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
)

onMounted(() => {
  fetchCart()
})
</script>
<style scoped>
.cart-container {
  max-width: 80%;
  margin-left: 10px;
  margin-right: auto;
  padding: 24px 0;
}
</style>