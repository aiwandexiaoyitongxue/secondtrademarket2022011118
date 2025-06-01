<template>
<div class="header-bar">
  <el-button type="danger" @click="logout" size="small">退出登录</el-button>
</div>
  <el-container style="height: 100vh;">
    <!-- 侧边导航 -->
    <el-aside width="220px" style="background: #fff; border-right: 1px solid #eee;">
      <el-menu 
        :default-active="activeMenu" 
        class="el-menu-vertical-demo" 
        @select="handleMenuSelect"
      >
        <el-menu-item index="overview">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="product">
          <template #title>
            <el-icon><GoodsFilled /></el-icon>
            <span>商品橱窗</span>
          </template>
          <el-menu-item index="product-showcase">商品列表</el-menu-item>
          <el-menu-item index="publish">发布商品</el-menu-item>
          <el-menu-item index="unpublish">已下架商品</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order">
          <template #title>
            <el-icon><List /></el-icon>
            <span>订单交易</span>
          </template>
          <el-menu-item index="ship">发货</el-menu-item>
          <el-menu-item index="order-manage">订单管理</el-menu-item>
          <el-menu-item index="refund">售后服务</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="review">
          <el-icon><StarFilled /></el-icon>
          <span>交易评价</span>
        </el-menu-item>
        
        <el-menu-item index="wallet">
          <el-icon><Wallet /></el-icon>
          <span>商家钱包</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-main style="background: #f5f7fa;">
      <component 
        :is="currentView" 
        :key="activeMenu"
        @update:activeMenu="handleMenuSelect" 
      />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, provide, h, defineComponent } from 'vue'
import { HomeFilled, GoodsFilled, List, StarFilled, Medal, Wallet } from '@element-plus/icons-vue'
import PublishProduct from './PublishProduct.vue'
import ProductShowcase from './ProductShowcase.vue'
import OrderTransaction from './OrderTransaction.vue'
import ShipOrder from './ShipOrder.vue'
import RefundOrder from './RefundOrder.vue'
import SellerComment from './comment.vue'
import MerchantWallet from './MerchantWallet.vue'
import { useRouter } from 'vue-router'
import SellerCenter from './SellerCenter.vue'
const router = useRouter()

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  // 清空其他用户信息（如有）
  router.push('/login')
}
// 导航状态
const activeMenu = ref('overview')

// 修改 currentView 的计算逻辑
const currentView = computed(() => {
  console.log('Current menu:', activeMenu.value)
  switch (activeMenu.value) {
    case 'overview': return SellerCenter
    case 'product-showcase': return ProductShowcase
    case 'publish': return PublishProduct
    case 'unpublish': return UnpublishProduct
    case 'ship': return ShipOrder
    case 'order-manage': return OrderTransaction
    case 'refund': return RefundOrder
    case 'review': return SellerComment
    case 'wallet': return MerchantWallet
    default: return SellerCenter
  }
})

// 修改菜单选择处理函数
function handleMenuSelect(index) {
  console.log('Menu selected:', index)
  activeMenu.value = index
}

// 使用 defineComponent 定义组件
const Overview = defineComponent({
  name: 'Overview',
  setup() {
    return () => h('div', [
      h('el-card', [
        h('h2', '欢迎来到商家中心'),
        h('p', '这里可以管理您的商品、订单、评价和等级。')
      ])
    ])
  }
})

// 已下架商品组件
const UnpublishProduct = defineComponent({
  name: 'UnpublishProduct',
  setup() {
    provide('defaultStatus', 2)
    provide('showTitle', '已下架商品')
    provide('hideStatusFilter', true)
    return () => h(ProductShowcase)
  }
})
</script>

<style scoped>
.el-menu-vertical-demo {
  border-right: none;
}

.el-menu-item [class^="el-icon-"],
.el-sub-menu [class^="el-icon-"] {
  margin-right: 5px;
  width: 24px;
  text-align: center;
  font-size: 18px;
}

.el-sub-menu .el-sub-menu__title {
  display: flex;
  align-items: center;
}

.el-menu-item {
  display: flex;
  align-items: center;
}
.header-bar {
  position: absolute;
  top: 20px;
  right: 40px;
  z-index: 10;
}
</style> 