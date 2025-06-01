<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <!-- <el-header class="header">
      <div class="logo">
        <el-button 
            class="back-arrow"
            circle
            plain
            @click="handleBack"
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
      </div>
      <div class="nav-right">
        <el-button v-if="!isLoggedIn" type="primary" @click="handleLogin">登录</el-button>
        <el-button v-else type="danger" @click="handleLogout" style="margin-left: 10px">退出登录</el-button>
      </div>
    </el-header> -->

    <div class="nav-buttons-fixed">
      <el-button v-if="!isLoggedIn" type="primary" @click="handleLogin">登录</el-button>
      <el-button v-else type="danger" @click="handleLogout">退出登录</el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section" style="margin-top: 20px;">
      <div class="search-content">
        <div class="search-header">
          <h2>商品搜索</h2>
          <p>找到您心仪的商品</p>
        </div>
        <div class="search-container">
          <el-button 
            v-if="isSearching || search" 
            @click="handleBack" 
            class="back-arrow"
            circle
            plain
          >
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <el-input
            v-model="search"
            placeholder="搜索商品名称"
            class="search-input"
            clearable
            @clear="handleClear"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch" type="primary" class="search-button">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <!-- 轮播图，仅在未搜索时显示 -->
    <el-carousel v-if="!isSearching" :interval="4000" type="card" height="400px" class="carousel">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <img :src="item.image" :alt="item.title" />
        <div class="carousel-content">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 搜索结果提示和排序选项 -->
    <div v-if="isSearching" class="search-result-header">
      <div class="search-result-info">
        <h3>搜索结果：{{ search }}</h3>
        <p>共找到 {{ total }} 个商品</p>
      </div>
      <div class="sort-options">
        <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort">
          <el-option label="默认排序" value="default" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="好评度优先" value="rating_desc" />
          <el-option label="销量优先" value="sales_desc" />
        </el-select>
      </div>
    </div>

    <!-- 商品展示 -->
    <div class="product-section">
      <h2 v-if="!isSearching">热门商品</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in sortedProducts" :key="product.id">
          <el-card class="product-card" shadow="hover" @click="handleProductClick(product)">
            <img :src="getImageUrl(product.image)" :alt="product.name" />
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <div class="product-meta">
                <span class="stock">库存: {{ product.stock }}</span>
                <span class="sales">销量: {{ product.sales }}</span>
              </div>
              <p class="price">¥{{ product.price }}</p>
              <p class="merchant">{{ product.merchant }}</p>
              <div class="rating-container">
                <el-rate
                  v-model="product.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                />
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      style="margin: 24px 0; text-align: center;"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      v-model:current-page="page"
      @current-change="fetchProducts"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, ArrowLeft } from '@element-plus/icons-vue'
import request from '@/utils/request'

import img1 from '@/assets/carousel/1.jpg'
import img2 from '@/assets/carousel/2.jpg'
import img3 from '@/assets/carousel/3.jpg'

const router = useRouter()
const search = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)
const products = ref([])
const isSearching = ref(false)
const sortBy = ref('default')

const carouselItems = ref([
  { image: img1, title: '新品上市', description: '最新商品，限时优惠' },
  { image: img2, title: '特惠活动', description: '全场商品，低至5折' },
  { image: img3, title: '品质保证', description: '正品保障，假一赔十' }
])

// 添加登录状态检查
const isLoggedIn = ref(false)

// 检查登录状态
function checkLoginStatus() {
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
}

function handleLogin() {
  router.push('/login')
}

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('userRole')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  ElMessage.success('已退出登录')
  window.location.reload()
}

function handleProductClick(product) {
  if (!product || !product.id) {
    ElMessage.warning('商品信息不完整')
    return
  }
  if (!isLoggedIn.value) {
    ElMessage.warning('您还未登录，请先登录')
    router.push('/login')
    return
  }
  router.push({ name: 'ProductDetail', params: { id: product.id } })
}

function getImageUrl(url) {
  if (!url) return 'https://placehold.co/200x200/e2e8f0/475569?text=No+Image'
  if (url.startsWith('http')) return url
  // 处理相对路径
  if (url.startsWith('/static/upload/')) {
    return `http://localhost:8080${url}`
  }
  // 处理其他情况
  return `http://localhost:8080/static/upload/images/${url}`
}

// 搜索相关函数
const handleSearch = () => {
  page.value = 1
  fetchProducts()
}

const handleClear = () => {
  search.value = ''
  isSearching.value = false
  fetchProducts()
}

const handleBack = () => {
  search.value = ''
  isSearching.value = false
  fetchProducts()
}

const handleSort = () => {
  // 排序逻辑已通过计算属性实现
}

const fetchProducts = async () => {
  try {
    console.log('开始搜索，关键词：', search.value)
    const res = await request.get('/products', {
      params: {
        keyword: search.value,
        status: 1,
        page: page.value,
        size: pageSize,
        merchantId: null
      }
    })

    console.log('搜索结果：', res)

    if (res.code === 200 && res.data) {
      // 获取商品列表后，为每个商品获取评分
      const productsWithRatings = await Promise.all(
        (res.data.records || []).map(async (item) => {
          try {
            // 处理商品图片
            let imageUrl = item.image
            if (imageUrl && typeof imageUrl === 'string') {
              if (imageUrl.includes(',')) {
                imageUrl = imageUrl.split(',')[0] // 只取第一张图片
              }
              // 确保图片URL是完整的
              imageUrl = getImageUrl(imageUrl)
            }
            
            // 获取商品评分
            let rating = 0
            try {
              const ratingRes = await request.get(`/products/${item.id}/average-rating`)
              if (ratingRes && (ratingRes.code === 200 || ratingRes.success)) {
                rating = ratingRes.data || 0
              }
            } catch (ratingError) {
              console.warn(`获取商品 ${item.id} 评分失败:`, ratingError)
            }
            
            return {
              ...item,
              image: imageUrl,
              rating: rating
            }
          } catch (error) {
            console.error(`处理商品 ${item.id} 失败:`, error)
            return {
              ...item,
              image: getImageUrl(item.image),
              rating: 0
            }
          }
        })
      )
      
      products.value = productsWithRatings
      total.value = res.data.total || 0
      isSearching.value = !!search.value
    } else {
      ElMessage.error('获取商品列表失败')
      products.value = []
      total.value = 0
      isSearching.value = false
    }
  } catch (error) {
    console.error('搜索出错：', error)
    ElMessage.error('搜索失败，请稍后重试')
    products.value = []
    total.value = 0
    isSearching.value = false
  }
}

// 排序后的商品列表
const sortedProducts = computed(() => {
  const productsList = [...products.value]
  switch (sortBy.value) {
    case 'price_asc':
      return productsList.sort((a, b) => a.price - b.price)
    case 'price_desc':
      return productsList.sort((a, b) => b.price - a.price)
    case 'rating_desc':
      return productsList.sort((a, b) => b.rating - a.rating)
    case 'sales_desc':
      return productsList.sort((a, b) => b.sales - a.sales)
    default:
      return productsList
  }
})

// 在组件挂载时检查登录状态并获取商品列表
onMounted(() => {
  checkLoginStatus()
  fetchProducts()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

.nav-buttons-fixed {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

/* 搜索区域样式 */
.search-section {
  margin: 80px 0 20px 0;
  padding: 25px 40px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.search-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.search-header {
  position: absolute;
  left: 0;
  text-align: left;
  min-width: 160px;
}

.search-header h2 {
  color: #409EFF;
  font-size: 24px;
  margin: 0 0 6px 0;
  font-weight: 600;
}

.search-header p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.search-container {
  width: 100%;
  max-width: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.back-arrow {
  position: absolute;
  left: -40px;
  padding: 8px;
  border: none;
  background: transparent;
  color: #409EFF;
  transition: all 0.3s ease;
  z-index: 1;
}

.back-arrow:hover {
  transform: translateX(-2px);
  color: #66b1ff;
  background: rgba(64, 158, 255, 0.1);
}

.search-input {
  width: 600px;
  flex-shrink: 0;
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  border-radius: 20px;
  padding: 0 15px;
  background: white;
}

.search-input :deep(.el-input__inner) {
  height: 44px;
  font-size: 16px;
}

.search-input :deep(.el-input-group__append) {
  background-color: transparent;
  border: none;
  padding: 0;
}

.search-input :deep(.el-input-group__append .el-button) {
  border: none;
  color: white;
  font-size: 16px;
  height: 44px;
  padding: 0 30px;
  border-radius: 20px;
  background: linear-gradient(45deg, #a0cfff, #409EFF);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input-group__append .el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  background: linear-gradient(45deg, #b3d7ff, #409EFF);
}

.carousel {
  margin: 20px auto;
  width: 90%;
  max-width: 1200px;
}

.carousel img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 20px;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.product-section {
  margin: 40px auto;
  width: 90%;
  max-width: 1200px;
}

.product-section h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
}

.product-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.product-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 40px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
  font-size: 12px;
  color: #606266;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  margin: 10px 0;
}

.merchant {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.rating-container {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.rating-container :deep(.el-rate) {
  display: inline-flex;
  align-items: center;
}

.rating-container :deep(.el-rate__icon) {
  font-size: 16px;
  margin-right: 4px;
}

.rating-container :deep(.el-rate__text) {
  margin-left: 8px;
  color: #ff9900;
  font-size: 14px;
}

.search-result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px auto;
  max-width: 1200px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-result-info h3 {
  color: #409EFF;
  font-size: 20px;
  margin: 0 0 8px 0;
}

.search-result-info p {
  color: #606266;
  margin: 0;
}

.sort-options {
  margin-left: 20px;
}

.sort-options :deep(.el-select) {
  width: 140px;
}

.sort-options :deep(.el-input__wrapper) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.sort-options :deep(.el-input__inner) {
  height: 36px;
}
</style> 