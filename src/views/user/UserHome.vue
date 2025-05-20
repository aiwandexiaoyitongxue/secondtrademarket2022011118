<template>
  <div class="dashboard-layout">

    <!-- 右侧主内容 -->
    <main class="main-content">
      <!-- 顶部筛选区 -->
     <el-row :gutter="8" class="top-bar">
  <el-col :span="4">
    <el-select v-model="category" placeholder="全部分类" clearable @change="fetchProducts" class="category-select">
      <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
    </el-select>
  </el-col>
  <el-col :span="4">
    <el-select v-model="brand" placeholder="品牌" clearable @change="fetchProducts" class="filter-select">
      <el-option v-for="brand in brands" :key="brand.id" :label="brand.name" :value="brand.id" />
    </el-select>
  </el-col>
  <el-col :span="4">
    <el-select v-model="paymentMethod" placeholder="支付方式" clearable @change="fetchProducts" class="filter-select">
      <el-option v-for="method in paymentMethods" :key="method.value" :label="method.label" :value="method.value" />
    </el-select>
  </el-col>
  <el-col :span="4">
    <el-select v-model="condition" placeholder="商品成色" clearable @change="fetchProducts" class="filter-select">
      <el-option v-for="cond in conditions" :key="cond.value" :label="cond.label" :value="cond.value" />
    </el-select>
  </el-col>
  <el-col :span="3">
    <el-input-number v-model="minPrice" :min="0" placeholder="最低价" class="price-input" />
  </el-col>
  <el-col :span="1" style="text-align: center; line-height: 32px;">-</el-col>
  <el-col :span="3">
    <el-input-number v-model="maxPrice" :min="0" placeholder="最高价" class="price-input" />
  </el-col>
  <el-col :span="4">
    <el-select v-model="sortBy" placeholder="排序方式" clearable @change="fetchProducts" class="filter-select">
      <el-option v-for="sort in sortOptions" :key="sort.value" :label="sort.label" :value="sort.value" />
    </el-select>
  </el-col>
  <el-col :span="2">
    <el-button @click="fetchProducts" type="primary" style="width: 100%;">筛选</el-button>
  </el-col>
  <el-col :span="2">
    <el-button @click="resetFilters" style="width: 100%;">重置</el-button>
  </el-col>
  <el-col :span="6">
    <el-input
      v-model="search"
      placeholder="搜索商品"
      class="search-input"
      @keyup.enter="fetchProducts"
      clearable
    >
      <template #append>
        <el-button @click="fetchProducts" type="primary">搜索</el-button>
      </template>
    </el-input>
  </el-col>
</el-row>
     <div class="carousel-wrapper">
      <el-carousel :interval="4000" type="card" height="350px" class="carousel">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <img :src="item.image" :alt="item.title" />
        </el-carousel-item>
      </el-carousel>
    </div>
<!-- 公告栏（多条信息，可滚动） -->
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

      <!-- 商品列表 -->
      <el-row :gutter="20" class="product-list">
        <el-col :span="6" v-for="item in products" :key="item.id">
          <el-card class="product-card" shadow="hover">
            <img :src="getImageUrl(item.image)" :alt="item.name" class="product-img" @click="goDetail(item.id)" />
            <div class="product-info">
              <h3>{{ item.name }}</h3>
              <p class="price">¥{{ item.price }}</p>
              <p class="merchant">{{ item.merchant }}</p>
                    <el-rate
                  :model-value="item.rating || 0"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value} 分"
                  style="font-size: 14px;"
                />
                    <el-button type="success" @click="addToCart(item.id)" size="small" style="margin-top: 8px;">加入购物车</el-button>
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
      <router-view />
    </main>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import img1 from '@/assets/carousel/1.jpg'
import img2 from '@/assets/carousel/2.jpg'
import img3 from '@/assets/carousel/3.jpg'

const router = useRouter()
const search = ref('')
const category = ref(null)
const brand = ref(null)
const paymentMethod = ref(null)
const condition = ref(null)
const sortBy = ref(null)
const minPrice = ref(null)
const maxPrice = ref(null)
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

const getImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return 'http://localhost:8080' + url;
}
const notices = ref([
  { tag: '公告', type: 'info', content: '平台维护：5月20日凌晨2点-4点，期间暂停服务。' },
  { tag: '促销', type: 'success', content: '新用户注册即送100积分！' },
  { tag: '活动', type: 'warning', content: '本周热卖商品限时8折，数量有限，先到先得！' },
  { tag: '公告', type: 'info', content: '请勿相信平台外的任何交易信息，谨防诈骗。' },
  { tag: '促销', type: 'danger', content: '满200减20，满500减60，快来选购吧！' }
])
// 品牌列表
const brands = ref([
  { id: 1, name: 'Apple' },
  { id: 2, name: 'Samsung' },
  { id: 3, name: 'Huawei' },
  { id: 4, name: 'Xiaomi' },
  { id: 5, name: 'Nike' },
  { id: 6, name: 'Adidas' }
])

// 支付方式
const paymentMethods = ref([
  { value: 'alipay', label: '支付宝' },
  { value: 'wechat', label: '微信支付' },
  { value: 'credit_card', label: '信用卡' },
  { value: 'cash', label: '货到付款' }
])

// 商品成色
const conditions = ref([
  { value: 'new', label: '全新' },
  { value: 'like_new', label: '几乎全新' },
  { value: 'good', label: '良好' },
  { value: 'fair', label: '一般' }
])

// 排序选项
const sortOptions = ref([
  { value: 'price_asc', label: '价格从低到高' },
  { value: 'price_desc', label: '价格从高到低' },
  { value: 'rating_desc', label: '评分最高' },
  { value: 'sales_desc', label: '销量最高' },
  { value: 'newest', label: '最新上架' }
])

// 重置所有筛选条件
const resetFilters = () => {
  search.value = ''
  category.value = null
  brand.value = null
  paymentMethod.value = null
  condition.value = null
  sortBy.value = null
  minPrice.value = null
  maxPrice.value = null
  page.value = 1
  fetchProducts()
}

const fetchProducts = async () => {
  const res = await request.get('/products', {
    params: {
      search: search.value,
      categoryId: category.value,
      brandId: brand.value,
      paymentMethod: paymentMethod.value,
      condition: condition.value,
      sortBy: sortBy.value,
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      page: page.value,
      pageSize
    }
  })
  console.log('res:', res);
  // 直接用 res.records，不要用 res.data.records
  let data = res.records || [];
  if (!Array.isArray(data)) {
    console.error('products is not array:', data);
    data = [];
  }
  products.value = res.records || [];
  total.value = res.total || products.value.length;
}
function goDetail(id) {
  router.push(`/product/${id}`)
}
function addToCart(productId) {
  // 实际应调用后端接口
  alert('已加入购物车: ' + productId)
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
  padding: 30px 40px 40px 40px;
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
  margin: 0 auto 35px auto;
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
  cursor: pointer;
  transition: transform 0.3s;
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
}
.product-info {
  padding: 10px;
}
.product-info h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}
.merchant {
  color: #909399;
  font-size: 14px;
}
</style>