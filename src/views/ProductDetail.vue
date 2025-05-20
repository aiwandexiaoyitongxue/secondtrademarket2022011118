<template>
  <div class="product-detail">
    <el-card v-if="product">
    <img :src="getImageUrl(product.image)" :alt="product.name" />
      <div class="product-info">
        <h2>{{ product.name }}</h2>
        <p class="price">¥{{ product.price }}</p>
        <p class="merchant">商家：{{ product.merchant }}</p>
        <p class="description">{{ product.description }}</p>
        <el-button type="primary">立即购买</el-button>
      </div>
    </el-card>
    <div v-else-if="loading" class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    <div v-else class="error">
      <el-result
        icon="error"
        title="商品不存在"
        sub-title="抱歉，您访问的商品不存在或已被下架"
      >
        <template #extra>
          <el-button type="primary" @click="router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(true)

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return 'http://localhost:8080' + url;
}
onMounted(async () => {
  try {
    const id = route.params.id
    if (!id) {
      throw new Error('商品ID不存在')
    }
    const res = await axios.get(`/api/products/${id}`)
    product.value = res.data
  } catch (error) {
    console.error('获取商品详情失败:', error)
    product.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.product-detail {
  max-width: 800px;
  margin: 40px auto;
}
.product-image {
  width: 100%;
  max-width: 400px;
  display: block;
  margin: 0 auto 20px auto;
}
.product-info {
  padding: 20px;
}
.price {
  color: #f56c6c;
  font-size: 22px;
  font-weight: bold;
  margin: 10px 0;
}
.merchant {
  color: #909399;
  font-size: 16px;
  margin-bottom: 10px;
}
.description {
  color: #333;
  margin-bottom: 20px;
}
.loading {
  padding: 20px;
}
.error {
  text-align: center;
  padding: 40px;
}
</style>