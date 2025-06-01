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
      <el-table-column label="商品" min-width="300">
        <template #default="{ row }">
          <div class="product-info">
            <el-image
              :src="getImageUrl(row.productImage)"
              style="width: 80px; height: 80px; margin-right: 10px;"
              fit="cover"
            />
            <span>{{ row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="单价" width="100">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" :max="row.stock" @change="updateQuantity(row)" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120">
        <template #default="{ row }">
          ¥{{ (row.price * row.quantity).toFixed(2) }}
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
      <span style="font-size: 18px; font-weight: bold;">总计：¥{{ totalAmount }}</span>
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
import { useRouter } from 'vue-router'

const cartItems = ref([])
const loading = ref(false)
const selectedItems = ref([])
const router = useRouter()

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return 'http://localhost:8080' + url;
}

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/user/cart')
    console.log('购物车响应:', res)
    if (res.success) {
      cartItems.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取购物车失败')
    }
  } catch (e) {
    console.error('获取购物车失败:', e)
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const updateQuantity = async (row) => {
  try {
    await request.post('/api/user/cart/update', { id: row.id, quantity: row.quantity })
    await fetchCart()
  } catch (e) {
    console.error('更新数量失败:', e)
  }
}

const removeItem = async (id) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await request.post('/api/user/cart/delete', { id })
    await fetchCart()
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
  
  // 将选中的商品信息存储到localStorage
  const checkoutItems = selectedItems.value.map(item => ({
    id: item.id,
    productId: item.productId,
    quantity: item.quantity,
    price: item.price,
    productName: item.productName,
    productImage: item.productImage
  }))
  
  localStorage.setItem('checkoutItems', JSON.stringify(checkoutItems))
  
  // 跳转到结算页面
  router.push('/order/checkout')
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
  max-width: 100%;
  margin-left: 10px;
  margin-right: auto;
  padding: 20px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-info span {
  margin-left: 10px;
}
</style>