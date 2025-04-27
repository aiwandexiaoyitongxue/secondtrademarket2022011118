<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo" />
        <span>Commodity Portal</span>
      </div>
      <div class="nav-right">
        <el-button type="primary" @click="handleLogin">登录</el-button>
      </div>
    </el-header>

    <!-- 轮播图 -->
    <el-carousel :interval="4000" type="card" height="400px" class="carousel">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <img :src="item.image" :alt="item.title" />
        <div class="carousel-content">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类索引 -->
    <div class="category-section">
      <h2>商品分类</h2>
      <el-row :gutter="20">
        <el-col :span="4" v-for="(category, index) in categories" :key="index">
          <el-card class="category-card" shadow="hover" @click="handleCategoryClick(category)">
            <p>{{ category.name }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 商品展示 -->
    <div class="product-section">
      <h2>热门商品</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(product, index) in products" :key="index">
          <el-card class="product-card" shadow="hover" @click="handleProductClick(product)">
            <img :src="product.image" :alt="product.name" />
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">¥{{ product.price }}</p>
              <p class="merchant">{{ product.merchant }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import img1 from '@/assets/carousel/1.jpg'
import img2 from '@/assets/carousel/2.jpg'
import img3 from '@/assets/carousel/3.jpg'
import iphoneImg from '@/assets/products/iphone.jpg'

const router = useRouter()

const carouselItems = ref([
  { image: img1, title: '新品上市', description: '最新商品，限时优惠' },
  { image: img2, title: '特惠活动', description: '全场商品，低至5折' },
  { image: img3, title: '品质保证', description: '正品保障，假一赔十' }
])

const categories = ref([
  { name: '电子产品' },
  { name: '服装服饰' },
  { name: '图书教材' },
  { name: '生活用品' },
  { name: '运动器材' }
])

const products = ref([
  { name: 'iPhone 13 Pro', price: 6999, image: iphoneImg, merchant: 'Apple官方旗舰店' }
  // 其他商品可按需添加
])

function handleLogin() {
  router.push('/login')
}
function handleCategoryClick(category) {
  console.log('Selected category:', category)
}
function handleProductClick(product) {
  console.log('Selected product:', product)
}

const imgUrl = new URL('@/assets/carousel/1.jpg', import.meta.url).href
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.logo span {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.carousel {
  margin: 20px auto;
  width: 90%;
}

.carousel img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 20px;
}

.category-section, .product-section {
  margin: 40px auto;
  width: 90%;
}

.category-section h2, .product-section h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-card img {
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
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