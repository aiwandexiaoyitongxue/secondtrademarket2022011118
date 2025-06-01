<template>
  <div class="dashboard-layout">
    <div class="header-bar">
          <el-button
      type="danger"
      plain
      round
      size="small"
      style="font-weight: bold; letter-spacing: 2px;"
      @click="logout"
    >
      <el-icon style="margin-right: 10px;"><SwitchButton /></el-icon>
      退出登录
    </el-button>
    </div>
    <!-- 右侧主内容 -->
    <main class="main-content">
      <!-- 搜索区域 -->
      <div class="search-section">
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

      <!-- 轮播图和公告栏，仅在未搜索时显示 -->
      <template v-if="!isSearching">
        <div class="carousel-wrapper">
          <el-carousel :interval="4000" type="card" height="350px" class="carousel">
            <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
              <img :src="item.image" :alt="item.title" />
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="notice-bar">
          <el-card class="notice-card">
            <div class="notice-title">
              <i class="el-icon-bell"></i>
              <span>平台公告 & 促销活动</span>
            </div>
            <el-scrollbar height="80px">
              <ul class="notice-list">
                <li v-for="(notice, idx) in notices" :key="idx">
                  <el-tag :type="notice.type" size="small" style="margin-right: 8px;">{{ notice.tag }}</el-tag>
                  <span>{{ notice.content }}</span>
                </li>
              </ul>
            </el-scrollbar>
          </el-card>
        </div>
      </template>

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

      <!-- 商品列表 -->
      <el-row :gutter="20" class="product-list" :class="{ 'search-mode': isSearching }">
        <el-col :span="6" v-for="item in sortedProducts" :key="item.id">
          <el-card class="product-card" shadow="hover">
            <img :src="getImageUrl(item.image)" :alt="item.name" class="product-img" @click="goDetail(item.id)" />
            <div class="product-info">
              <h3>{{ item.name }}</h3>
              <div class="product-meta">
                <span class="stock">库存: {{ item.stock }}</span>
                <span class="sales">销量: {{ item.sales }}</span>
              </div>
              <p class="price">¥{{ item.price }}</p>
              <p class="merchant">{{ item.merchant }}</p>
              <div class="rating-container">
                <el-rate
                  v-model="item.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                />
              </div>
              <!-- 数量选择器和加入购物车按钮 -->
              <div class="add-to-cart-controls">
                <el-input-number 
                  v-model="item.buyQuantity" 
                  :min="1" 
                  :max="item.stock > 0 ? item.stock : 1" 
                  size="small" 
                  style="width: 100px;"
                  :disabled="item.stock <= 0"
                ></el-input-number>
                <el-button 
                  type="success" 
                  @click="addToCart(item)" 
                  size="small"
                  :disabled="item.stock <= 0"
                >加入购物车</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

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
    </main>
  </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import img1 from '@/assets/carousel/1.jpg'
import img2 from '@/assets/carousel/2.jpg'
import img3 from '@/assets/carousel/3.jpg'
import { ElMessage } from 'element-plus'
import { Search, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const search = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)
const products = ref([])
const categories = ref([
  { id: 1, name: '电子产品' },
  { id: 2, name: '服装服饰' },
  { id: 3, name: '图书教材' },
  { id: 4, name: '生活用品' },
  { id: 5, name: '运动器材' }
])
const carouselItems = ref([
  { image: img1, title: '新品上市' },
  { image: img2, title: '特惠活动' },
  { image: img3, title: '品质保证' }
])
const isSearching = ref(false)
const sortBy = ref('default')

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  // 清空其他用户信息（如有）
  router.push('/login')
}
const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  // 如果URL以/static开头，直接使用
  if (url.startsWith('/static')) return 'http://localhost:8080' + url;
  // 其他情况，添加/api前缀
  return 'http://localhost:8080/api' + url;
}
const notices = ref([
  { tag: '公告', type: 'info', content: '平台维护：5月20日凌晨2点-4点，期间暂停服务。' },
  { tag: '促销', type: 'success', content: '新用户注册即送100积分！' },
  { tag: '活动', type: 'warning', content: '本周热卖商品限时8折，数量有限，先到先得！' },
  { tag: '公告', type: 'info', content: '请勿相信平台外的任何交易信息，谨防诈骗。' },
  { tag: '促销', type: 'danger', content: '满200减20，满500减60，快来选购吧！' }
])

const fetchProducts = async () => {
  try {
    // 检查是否有 token
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    console.log('开始搜索，关键词：', search.value)
    const res = await request.get('/products', {
      params: {
        keyword: search.value,
        status: 1,
        page: page.value,
        size: pageSize
      }
    })

    console.log('搜索结果：', res)

    if (res.code === 200 && res.data) {
      // 获取商品列表后，为每个商品获取评分
      const productsWithRatings = await Promise.all(
        (res.data.records || []).map(async (item) => {
          try {
            const ratingRes = await request.get(`/products/${item.id}/average-rating`)
            return {
              ...item,
              buyQuantity: 1,
              rating: ratingRes.code === 200 ? ratingRes.data || 0 : 0
            }
          } catch (error) {
            console.error(`获取商品 ${item.id} 评分失败:`, error)
            return {
              ...item,
              buyQuantity: 1,
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
    if (error.response?.status === 401) {
      // 清除所有用户信息
      localStorage.clear()
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('搜索失败，请稍后重试')
    }
    products.value = []
    total.value = 0
    isSearching.value = false
  }
}

const handleClear = () => {
  search.value = ''
  isSearching.value = false
  fetchProducts()
}

const handleSearch = () => {
  if (!search.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  page.value = 1
  isSearching.value = true
  fetchProducts()
}

function goDetail(id) {
  router.push(`/product/${id}`)
}
// 修改 addToCart 方法
async function addToCart(product) {
  console.log('准备添加到购物车，商品信息:', product);
  
  // 检查购买数量是否有效
  if (!product.buyQuantity || product.buyQuantity <= 0) {
    ElMessage.warning('请选择有效的购买数量');
    return;
  }
  if (product.buyQuantity > product.stock) {
    ElMessage.warning('购买数量不能超过库存');
    return;
  }

  // 检查必要字段
  if (!product.id) {
    ElMessage.error('商品ID不能为空');
    return;
  }
  if (!product.merchantId) {
    ElMessage.error('商品信息不完整，请刷新页面重试');
    return;
  }

  const requestData = {
    productId: product.id,
    quantity: product.buyQuantity,
    merchantId: product.merchantId,
    selected: 1
  };
  
  console.log('发送到购物车的请求数据:', requestData);

  try {
    const res = await request.post('/user/cart/add', requestData);
    console.log('添加到购物车响应:', res);
    
    if (res.code === 200) {
      ElMessage.success('已成功加入购物车');
    } else {
      ElMessage.error(res.message || '加入购物车失败');
    }
  } catch (error) {
    console.error('加入购物车失败:', error);
    console.error('错误详情:', error.response?.data);
    ElMessage.error('加入购物车失败，请稍后再试');
  }
}

// 修改回退处理函数
const handleBack = () => {
  search.value = ''
  isSearching.value = false
  page.value = 1
  fetchProducts()
}

// 计算排序后的商品列表
const sortedProducts = computed(() => {
  const productsList = [...products.value]
  
  switch (sortBy.value) {
    case 'price_asc':
      return productsList.sort((a, b) => a.price - b.price)
    case 'price_desc':
      return productsList.sort((a, b) => b.price - a.price)
    case 'rating_desc':
      return productsList.sort((a, b) => (b.rating || 0) - (a.rating || 0))
    case 'sales_desc':
      return productsList.sort((a, b) => (b.sales || 0) - (a.sales || 0))
    default:
      return productsList
  }
})

// 处理排序变化
const handleSort = () => {
  // 可以在这里添加额外的排序逻辑或动画效果
  console.log('排序方式变更为：', sortBy.value)
}

onMounted(() => {
  fetchProducts()
})
</script>
<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}
.side-nav {
  width: 200px;
  background: #fff;
  box-shadow: 2px 0 8px #f0f1f2;
  min-height: 100vh;
  padding-top: 20px;
}
.main-content {
  flex: 1;
  padding: 20px 40px 40px 40px;
  transition: all 0.3s ease;
}
.top-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  gap: 32px;
  flex-wrap: wrap;
}
.category-select {
  width: 180px;
}
.search-input {
  width: 320px;
}
.price-input {
  width: 120px;
}
.carousel-wrapper {
  width: 10;         /* 你可以改成 1000px 或 90% 等 */
  margin: 0 auto 34px auto;
}
.carousel img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.carousel-wrapper {
  width: 100%;
  margin-bottom: 16px;
}
.carousel img {
  width: 100%;
  height: 350px;
  object-fit: cover;
}
.notice-bar {
  width: 100%;
  max-width: 2000px;
  margin: 0 auto 35px auto; /* 调整下边距 */
}
.notice-card {
  padding: 10px 20px;
  border-radius: 8px;
}
.notice-title {
  font-size: 16px;
  color: #409EFF;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.notice-list li {
  margin-bottom: 6px;
  font-size: 14px;
  display: flex;
  align-items: center;
}
.product-card {
  margin-bottom: 20px;
  cursor: pointer; /* 添加指针，表示可点击查看详情 */
  transition: transform 0.3s;
  height: 100%; /* 保证高度一致 */
  display: flex; /* 启用flex布局 */
  flex-direction: column; /* 子元素垂直排列 */
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.product-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0; /* 图片不收缩 */
}
.product-info {
  padding: 10px;
  flex-grow: 1; /* 信息区填充剩余空间 */
  display: flex;
  flex-direction: column;
}
.product-info h3 {
  margin: 0 0 5px 0; /* 调整标题下边距 */
  font-size: 16px;
  font-weight: bold; /* 加粗 */
  color: #333;
  /* 限制名称行数 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 显示最多两行 */
  -webkit-box-orient: vertical;
  min-height: 40px; /* 保证至少两行的高度 */
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin: 5px 0; /* 调整价格边距 */
}
.merchant {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px; /* 调整商家信息下边距 */
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px; /* 调整meta信息下边距 */
}

/* 样式调整 - 将销量和库存放在一行 */
.product-meta span {
    margin-right: 10px; /* 增加间距 */
}

.add-to-cart-controls {
  display: flex;
  align-items: center;
  gap: 10px; /* 数量选择器和按钮之间的间距 */
  margin-top: auto; /* 将控制区域推到底部 */
  padding-top: 10px; /* 与上方信息的间距 */
  border-top: 1px solid #ebeef5; /* 添加分割线 */
}

.header-bar {
  position: absolute;
  top: 10px;
  right: 5px;
  z-index: 10;
}

.search-section {
  margin: -20px 0 20px 0;
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

.back-arrow .el-icon {
  font-size: 18px;
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
  font-size: 20px;
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
  margin-left: 2px; /* 你可以调整为你想要的间距 */
}

.search-input :deep(.el-input-group__append .el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  background: linear-gradient(45deg, #b3d7ff, #409EFF);
}

.search-input :deep(.el-input-group__append .el-button .el-icon) {
  margin-right: 5px;
  font-size: 16px;
}

.search-button {
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-result-info {
  flex: 1;
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

.product-list {
  margin-top: 20px;
}

.product-list.search-mode {
  margin-top: 0;
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
</style>