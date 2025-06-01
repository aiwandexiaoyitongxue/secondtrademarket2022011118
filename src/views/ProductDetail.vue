<template>
  <div class="product-detail">
    <el-card v-if="product">
      <el-row :gutter="40">
        <!-- 商品图片 -->
        <el-col :span="10">
          <div v-if="product.imageList && product.imageList.length">
            <el-carousel v-if="product.imageList.length > 1" height="350px">
              <el-carousel-item v-for="(img, idx) in product.imageList" :key="idx">
                <el-image
                  :src="getImageUrl(img)"
                  fit="contain"
                  style="width: 100%; height: 350px; background: #f5f5f5"
                />
              </el-carousel-item>
            </el-carousel>
            <el-image
              v-else
              :src="getImageUrl(product.imageList[0])"
              fit="contain"
              style="width: 100%; height: 350px; background: #f5f5f5"
            />
          </div>
          <el-empty v-else description="暂无图片" />
        </el-col>
        <!-- 商品信息 -->
        <el-col :span="14">
          <h2>{{ product.name }}</h2>
          <p class="price">¥{{ product.price }}</p>
          <p>类别：{{ categoryName }}</p>
          <p>尺寸：{{ product.size }}</p>
          <p>在售数量：{{ product.stock }}</p>
          <p>销量：{{ product.sales }}</p>
          <p>评价分数：<el-rate :model-value="avgRating" disabled /></p>
          <p>描述：{{ product.description }}</p>
          <div class="actions" v-if="!readonly">
            <el-button type="primary" @click="addToCart(product.id)">加入购物车</el-button>
            <el-button type="success" @click="buyNow(product.id)">立即购买</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
    <div v-else class="loading">
      <el-skeleton :rows="3" animated />
    </div>
    <!-- 评价列表 -->
    <div class="review-section">
      <h3>商品评价</h3>
      <el-empty v-if="reviews.length === 0" description="暂无评价" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="review in reviews"
          :key="review.id"
          :timestamp="review.created_time"
        >
          <div>
            <b>{{ review.username }}</b>
            <el-rate :model-value="review.rating" disabled />
          </div>
          <div>{{ review.content }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { ElMessageBox } from 'element-plus'
const route = useRoute()
const router = useRouter()
const product = ref(null)
const reviews = ref([])
const avgRating = ref(0)
const categoryName = ref('')
const readonly = route.query.readonly === 'true'

const getImageUrl = (url) => {
  if (!url) return 'https://placehold.co/200x200/e2e8f0/475569?text=No+Image'
  if (url.startsWith('http')) return url
  // 处理相对路径
  if (url.startsWith('/static/upload/')) {
    return `http://localhost:8080${url}`
  }
  // 处理其他情况
  return `http://localhost:8080/static/upload/${url}`
}

function isLogin() {
  return !!localStorage.getItem('token')
}
function addToCart(productId) {
  if (!isLogin()) {
    // 跳转到登录页并带上原操作信息
    router.push({
      path: '/login',
      query: {
        redirect: route.fullPath, // 返回商品详情页
        action: 'addToCart',
        productId
      }
    })
    return
  }
  // 已登录，直接加购
  doAddToCart(productId)
}

function buyNow(productId) {
  if (!isLogin()) {
    router.push({
      path: '/login',
      query: {
        redirect: '/order/checkout',
        action: 'buyNow',
        productId,
        quantity: 1
      }
    })
    return
  }
  // 构造结算商品信息
  const currentProduct = product.value
  const checkoutItem = {
    productId: currentProduct.id,
    name: currentProduct.name,
    price: currentProduct.price,
    quantity: 1,
    image: currentProduct.image,
    merchantId: currentProduct.merchantId
    // 可根据结算页需要补充其他字段
  }
  localStorage.setItem('checkoutItems', JSON.stringify([checkoutItem]))
  // 跳转到结算页
  router.push({
    path: '/order/checkout',
    query: {
      productId: currentProduct.id,
      quantity: 1
    }
  })
}

function doAddToCart(productId) {
  // 获取当前商品对象
  const currentProduct = product.value
  request.post('/user/cart/add', {
    productId,
    quantity: 1,
    merchantId: currentProduct.merchantId
  })
    .then(res => {
      if (res.code === 200 || res.success) {
        ElMessageBox.confirm(
          '已加入购物车，是否前往用户中心？',
          '操作成功',
          {
            confirmButtonText: '去用户中心',
            cancelButtonText: '留在当前页',
            type: 'success'
          }
        ).then(() => {
          router.push('/user/cart')
        })
      }
    })
    .catch(error => {
      console.error('加入购物车失败:', error)
      ElMessage.error(error.response?.data?.message || '加入购物车失败')
    })
}

// 在组件挂载时检查是否需要执行登录后的操作
onMounted(async () => {
  const id = route.query.id || route.params.id
  if (!id) return

  // 检查是否是登录后的重定向
  const { action, productId, redirect } = route.query
  
  // 如果是加购操作，且是从商品详情页重定向回来的
  if (action === 'addToCart' && productId && redirect === route.fullPath) {
    // 登录后自动执行加购
    await doAddToCart(productId)
    // 清除 query 参数
    router.replace({ path: route.path, query: {} })
  }
  // 如果是立即购买操作，且重定向到结算页
  else if (action === 'buyNow' && productId && redirect === '/checkout') {
    // 直接跳转到结算页，不需要在这里处理
    // 登录页面会处理这个重定向
  }

  try {
    // 获取商品详情
    const res = await request.get(`/products/${id}`)
    if (res.code === 200 || res.success) {
      product.value = res.data
      console.log('商品详情数据:', res.data)
      
      // 使用后端返回的图片列表
      if (res.imageList && Array.isArray(res.imageList)) {
        product.value.imageList = res.imageList
        console.log('商品图片列表:', res.imageList)
      } else if (product.value.image && typeof product.value.image === 'string' && product.value.image.includes(',')) {
        product.value.imageList = product.value.image.split(',')
      } else if (product.value.image) {
        product.value.imageList = [product.value.image]
      } else {
        product.value.imageList = []
      }
      console.log('处理后的图片列表:', product.value.imageList)

      // 获取分类信息
      if (product.value.categoryId) {
        try {
          const categoryRes = await request.get(`/categories/all`)
          if (categoryRes.code === 200 && categoryRes.data) {
            const category = categoryRes.data.find(c => c.id === product.value.categoryId)
            if (category) {
              categoryName.value = category.name
            }
          }
        } catch (error) {
          console.error('获取分类信息失败:', error)
        }
      }
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败，请稍后重试')
  }

  try {
    // 获取商品评价
    const reviewRes = await request.get(`/products/${id}/reviews`)
    if (reviewRes.code === 200 || reviewRes.success) {
      reviews.value = reviewRes.data
      // 计算平均分
      if (reviews.value.length > 0) {
        const sum = reviews.value.reduce((acc, cur) => acc + (cur.rating || 0), 0)
        avgRating.value = sum / reviews.value.length
      } else {
        avgRating.value = 0
      }
    }
  } catch (error) {
    console.error('获取商品信息失败:', error)
    ElMessage.error('获取商品信息失败')
  }
})
</script>

<style scoped>
.product-detail {
  max-width: 1100px;
  margin: 40px auto;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}
.price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0 20px 0;
}
.actions {
  margin-top: 30px;
}
.review-section {
  margin-top: 50px;
  background: #fafafa;
  padding: 20px 30px;
  border-radius: 8px;
}
.loading {
  padding: 40px;
}
</style>