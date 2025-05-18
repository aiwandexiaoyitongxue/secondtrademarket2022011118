<template>
  <div class="product-showcase-container">
    <el-card class="search-card">
      <div class="search-header">
        <h3>商品搜索</h3>
      </div>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option 
              v-for="category in categories" 
              :key="category.value" 
              :label="category.label" 
              :value="category.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" :value="-1"></el-option>
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="在售" :value="1"></el-option>
            <el-option label="已下架" :value="2"></el-option>
            <el-option label="已售罄" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 状态标签快速筛选 -->
      <div class="status-filter" v-if="!hideStatusFilter">
        <el-tag 
          :type="searchForm.status === -1 ? 'primary' : 'info'" 
          @click="filterByStatus(-1)" 
          class="status-tag"
          effect="plain"
        >
          全部 <span class="tag-count">({{ getTotalCount() }})</span>
        </el-tag>
        <el-tag 
          type="info" 
          @click="filterByStatus(0)" 
          class="status-tag" 
          :effect="searchForm.status === 0 ? 'dark' : 'plain'"
        >
          待审核 <span class="tag-count">({{ getStatusCount(0) }})</span>
        </el-tag>
        <el-tag 
          type="success" 
          @click="filterByStatus(1)" 
          class="status-tag" 
          :effect="searchForm.status === 1 ? 'dark' : 'plain'"
        >
          在售 <span class="tag-count">({{ getStatusCount(1) }})</span>
        </el-tag>
        <el-tag 
          type="danger" 
          @click="filterByStatus(3)" 
          class="status-tag" 
          :effect="searchForm.status === 3 ? 'dark' : 'plain'"
        >
          已售罄 <span class="tag-count">({{ getStatusCount(3) }})</span>
        </el-tag>
      </div>
    </el-card>

    <el-card class="product-list-card">
      <div class="list-header">
        <h3>{{ showTitle }}</h3>
        <div class="header-buttons">
          <template v-if="defaultStatus === 2">
            <el-button type="primary" @click="goToPublish">发布新商品</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="goToPublish">发布新商品</el-button>
          </template>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>
      
      <div v-else-if="productList.length === 0" class="empty-container">
        <el-empty description="暂无商品数据" />
        <el-button type="primary" @click="goToPublish">立即发布商品</el-button>
      </div>
      
      <div v-else class="product-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="product in productList" :key="product.id" class="product-col">
            <el-card class="product-card" shadow="hover" @click="handleProductClick(product)">
              <div class="product-img-container">
                <el-image 
                  :src="getProductImageUrl(product.imageUrl)"
                  fit="cover"
                  lazy
                  class="product-img"
                  :preview-src-list="[getProductImageUrl(product.imageUrl)]"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>加载失败</span>
                    </div>
                  </template>
                </el-image>
                <div class="product-status" :class="'status-' + product.status">
                  {{ getStatusText(product.status) }}
                </div>
              </div>
              
              <div class="product-info">
                <h4 class="product-name">{{ product.name }}</h4>
                <div class="product-price">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="original-price">¥{{ product.originalPrice }}</span>
                </div>
                <div class="product-meta">
                  <span class="stock">库存: {{ product.stock }}</span>
                  <span class="sales">销量: {{ product.sales }}</span>
                </div>
              </div>
              
              <div class="product-actions">
                <template v-if="defaultStatus === 2">
                  <div class="button-group">
                    <el-button 
                      type="success" 
                      size="small" 
                      @click.stop="toggleProductStatus(product)"
                    >
                      上架
                    </el-button>
                  </div>
                </template>
                <template v-else>
                  <template v-if="product.status === 0">
                    <el-button type="danger" size="small" @click.stop="handleDeleteProduct(product)">删除</el-button>
                  </template>
                  <template v-else-if="product.status === 3">
                    <el-button type="danger" size="small" @click.stop="handleDeleteProduct(product)">删除</el-button>
                  </template>
                  <template v-else>
                    <div class="button-group">
                      <el-button 
                        :type="product.status === 1 ? 'warning' : 'success'" 
                        size="small" 
                        @click.stop="toggleProductStatus(product)"
                      >
                        {{ product.status === 1 ? '下架' : '上架' }}
                      </el-button>
                      <el-button type="danger" size="small" @click.stop="handleDeleteProduct(product)">删除</el-button>
                    </div>
                  </template>
                </template>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-if="total > 0"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getProductList, updateProduct, deleteProduct } from '@/api/product'

const router = useRouter()
const emit = defineEmits(['update:activeMenu'])
const loading = ref(false)
const productList = ref([])
const allProducts = ref([]) // 存储所有商品，用于状态统计
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

// 接收来自父组件的默认筛选状态
const defaultStatus = inject('defaultStatus', null)
const showTitle = inject('showTitle', '商品列表') // 自定义标题
const hideStatusFilter = inject('hideStatusFilter', false) // 是否隐藏状态过滤标签

// 分类数据（应该从后端获取，这里先模拟）
const categories = [
  { value: 1, label: '电子产品' },
  { value: 2, label: '服装服饰' },
  { value: 3, label: '图书教材' },
  { value: 4, label: '生活用品' },
  { value: 5, label: '运动器材' },
  { value: 6, label: '手机' },
  { value: 7, label: '电脑' },
  { value: 8, label: '平板' },
  { value: 9, label: '耳机' }
]

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: -1, // 使用-1代替null表示全部
  merchantId: null // 会在组件加载时从localStorage获取当前商家ID
})

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 0: return '待审核'
    case 1: return '在售'
    case 2: return '已下架'
    case 3: return '已售罄'
    default: return '未知状态'
  }
}

// 获取商品图片URL
const getProductImageUrl = (url) => {
  if (!url) {
    // 使用一个占位符图片
    return 'https://via.placeholder.com/200x200?text=No+Image'
  }
  
  // 1. 如果是完整URL，直接返回
  if (url.startsWith('http')) {
    return url
  }
  
  // 2. 如果是/uploads/开头的路径，直接拼接基础URL
  if (url.startsWith('/uploads/')) {
    return `http://localhost:8081${url}`
  }
  
  // 3. 如果是相对路径，拼接服务器地址和uploads路径
  return `http://localhost:8081/uploads/${url}`
}

// 加载商品列表
const loadProductList = async () => {
  loading.value = true
  try {
    // 从localStorage获取当前商家ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    searchForm.merchantId = userInfo.id
    
    if (!searchForm.merchantId) {
      ElMessage.warning('未检测到商家信息，请重新登录')
      loading.value = false
      return
    }
    
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      merchantId: searchForm.merchantId,
      keyword: searchForm.keyword || undefined,
      categoryId: searchForm.categoryId || undefined,
      status: searchForm.status === -1 ? undefined : searchForm.status
    }
    
    // 在常规视图中（不是已下架专区），自动排除已下架商品
    if (searchForm.status === -1 && !defaultStatus) {
      params.excludeStatus = 2; // 排除已下架商品
    }
    
    // 如果当前在已下架商品页面，则只显示已下架商品
    if (defaultStatus === 2) {
      params.status = 2;
    }
    
    console.log('请求商品列表参数:', params)
    
    const res = await getProductList(params)
    if (res.code === 200) {
      console.log('获取商品列表成功:', res.data)
      
      // 处理返回数据
      if (Array.isArray(res.data)) {
        // 直接返回的是数组
        productList.value = res.data.map(processProductData)
        total.value = res.data.length
      } else if (res.data && res.data.records) {
        // 返回的是分页对象
        productList.value = res.data.records.map(processProductData)
        total.value = res.data.total
      } else {
        // 兜底处理
        productList.value = []
        total.value = 0
      }
    } else {
      ElMessage.error(res.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 处理商品数据，添加图片URL等信息
const processProductData = (item) => {
  // 查找主图
  let imageUrl = ''
  
  // 处理商品图片关联
  if (item.productImages && item.productImages.length > 0) {
    const mainImage = item.productImages.find(img => img.isMain) || item.productImages[0]
    imageUrl = mainImage.url
  }
  
  // 添加调试信息
  console.log('处理商品数据:', {
    id: item.id,
    name: item.name,
    status: item.status,
    statusText: getStatusText(item.status)
  })
  
  return {
    ...item,
    imageUrl
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadProductList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.status = -1
  currentPage.value = 1
  loadProductList()
}

// 分页
const handlePageChange = (page) => {
  currentPage.value = page
  loadProductList()
}

// 编辑商品
const handleEdit = (product) => {
  router.push({
    path: '/seller/edit-product',
    query: { id: product.id }
  })
}

// 上架/下架商品
const toggleProductStatus = (product) => {
  // 如果商品是待审核状态，不允许操作
  if (product.status === 0) {
    ElMessage.warning('商品正在审核中，暂时无法上架或下架')
    return
  }
  
  // 如果商品已售罄，不允许操作
  if (product.status === 3) {
    ElMessage.warning('商品已售罄，如需重新销售请编辑更新库存')
    return
  }
  
  const action = product.status === 1 ? '下架' : '上架'
  const newStatus = product.status === 1 ? 2 : 1
  
  ElMessageBox.confirm(
    `确定要${action}商品 "${product.name}" 吗？`, 
    `${action}确认`, 
    {
      confirmButtonText: `确定${action}`,
      cancelButtonText: '取消',
      type: product.status === 1 ? 'warning' : 'success',
      confirmButtonClass: product.status === 1 ? 'el-button--danger' : 'el-button--success'
    }
  ).then(async () => {
    try {
      loading.value = true
      const res = await updateProduct(product.id, { status: newStatus })
      
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: `${action}成功！商品已${product.status === 1 ? '下架，买家将无法看到此商品' : '重新上架，买家可以购买此商品'}`,
          duration: 2000
        })
        
        // 重新加载商品列表和统计数据
        await loadProductList()
        await loadAllProducts()
      } else {
        ElMessage.error(res.message || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败，请稍后重试`)
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: `已取消${action}操作`
    })
  })
}

// 点击商品
const handleProductClick = (product) => {
  // 跳转到商品详情页
  router.push({
    path: '/seller/product-detail',
    query: { id: product.id }
  })
}

// 跳转到发布页
const goToPublish = () => {
  emit('update:activeMenu', 'publish')
}

// 组件加载时获取商品列表
onMounted(() => {
  // 如果是通过"已下架商品"菜单进入，自动应用下架状态筛选
  const route = router.currentRoute.value;
  if (route.params.filter === 'unpublish') {
    searchForm.status = 2; // 已下架状态
  }
  
  loadProductList()
  loadAllProducts()  // 加载所有商品用于统计
})

// 状态过滤
const filterByStatus = (status) => {
  searchForm.status = status
  currentPage.value = 1
  loadProductList()
}

// 加载所有商品（不分页，用于统计）
const loadAllProducts = async () => {
  try {
    // 从localStorage获取当前商家ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const merchantId = userInfo.id
    
    if (!merchantId) return
    
    const params = {
      merchantId: merchantId,
      page: 1,
      size: 1000 // 设置较大的size以获取所有商品
    }
    
    const res = await getProductList(params)
    if (res.code === 200) {
      // 处理返回数据
      if (Array.isArray(res.data)) {
        allProducts.value = res.data.map(processProductData)
      } else if (res.data && res.data.records) {
        allProducts.value = res.data.records.map(processProductData)
      }
      console.log('已加载所有商品数据用于统计，总数:', allProducts.value.length)
    }
  } catch (error) {
    console.error('获取所有商品数据失败:', error)
  }
}

// 获取状态统计
const getTotalCount = () => {
  return allProducts.value.length
}

const getStatusCount = (status) => {
  return allProducts.value.filter(product => product.status === status).length
}

// 添加删除商品的函数
const handleDeleteProduct = (product) => {
  ElMessageBox.confirm(
    `确定要删除商品"${product.name}"吗？删除后无法恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(async () => {
    try {
      loading.value = true
      const res = await deleteProduct(product.id)
      
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: '商品删除成功！',
          duration: 2000
        })
        
        // 从列表中移除该商品
        const index = productList.value.findIndex(item => item.id === product.id)
        if (index !== -1) {
          productList.value.splice(index, 1)
        }
        
        // 从统计数据中移除
        const allIndex = allProducts.value.findIndex(item => item.id === product.id)
        if (allIndex !== -1) {
          allProducts.value.splice(allIndex, 1)
        }
        
        // 更新总数，如果删除后列表为空，重新加载数据
        if (productList.value.length === 0 && total.value > 0) {
          // 如果是最后一页的最后一条被删除，返回上一页
          if (currentPage.value > 1) {
            currentPage.value--
          }
          loadProductList()
        } else {
          total.value--
        }
      } else {
        ElMessage.error(res.message || '删除商品失败')
      }
    } catch (error) {
      console.error('删除商品失败:', error)
      ElMessage.error('删除商品失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除操作'
    })
  })
}
</script>

<style scoped>
.product-showcase-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-header,
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-header h3,
.list-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.product-list {
  margin-top: 20px;
}

.product-col {
  margin-bottom: 20px;
}

.product-card {
  height: 100%;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.product-img-container {
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: 4px 4px 0 0;
}

.product-img {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.product-card:hover .product-img {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.product-status {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  z-index: 2;
}

.status-0 {
  background-color: #909399; /* 待审核 - 灰色 */
}

.status-1 {
  background-color: #67C23A; /* 在售 - 绿色 */
}

.status-2 {
  background-color: #E6A23C; /* 已下架 - 黄色 */
}

.status-3 {
  background-color: #F56C6C; /* 已售罄 - 红色 */
}

.product-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 600;
  height: 44px;
  line-height: 22px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: #303133;
}

.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #F56C6C;
  margin-right: 10px;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #606266;
}

.product-actions {
  padding: 0 15px 15px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.product-actions .el-button {
  margin-bottom: 8px;
}

.button-group {
  display: flex;
  gap: 8px;
}

.loading-container,
.empty-container {
  padding: 30px 0;
  text-align: center;
}

.empty-container .el-button {
  margin-top: 15px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.status-filter {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-tag {
  cursor: pointer;
  transition: all 0.3s;
  padding: 6px 12px;
}

.status-tag:hover {
  transform: translateY(-2px);
}

.tag-count {
  margin-left: 5px;
  font-size: 12px;
  opacity: 0.8;
}

.header-buttons {
  display: flex;
  gap: 10px;
}
</style> 