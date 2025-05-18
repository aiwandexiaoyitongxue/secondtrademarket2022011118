<template>
  <el-container style="height: 100vh;">
    <!-- 侧边导航 -->
    <el-aside width="220px" style="background: #fff; border-right: 1px solid #eee;">
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" @select="handleMenuSelect">
        <el-menu-item index="overview"><el-icon><HomeFilled /></el-icon>首页</el-menu-item>
        <el-sub-menu index="product">
          <template #title>
            <div @click.stop="handleProductClick">
              <el-icon><GoodsFilled /></el-icon>商品橱窗
            </div>
          </template>
          <el-menu-item index="publish">发布商品</el-menu-item>
          <el-menu-item index="unpublish">已下架商品</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="order">
          <template #title><el-icon><List /></el-icon>订单交易</template>
          <el-menu-item index="ship">发货</el-menu-item>
          <el-menu-item index="receive">已卖出宝贝</el-menu-item>
          <el-menu-item index="refund">售后服务</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="review"><el-icon><StarFilled /></el-icon>买家交易的评价</el-menu-item>
        <el-menu-item index="level"><el-icon><Medal /></el-icon>卖家等级</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-main style="background: #f5f7fa;">
      <component :is="currentView" @update:activeMenu="handleMenuSelect" />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, reactive, defineComponent, provide, h } from 'vue'
import { HomeFilled, GoodsFilled, List, StarFilled, Medal, Plus } from '@element-plus/icons-vue'
import PublishProduct from './seller/PublishProduct.vue'
import ProductShowcase from './seller/ProductShowcase.vue'

// 导航状态
const activeMenu = ref('overview')
const currentView = computed(() => {
  switch (activeMenu.value) {
    case 'overview': return Overview
    case 'product': return ProductShowcase
    case 'publish': return PublishProduct
    case 'unpublish': return UnpublishProduct
    case 'ship': return ShipOrder
    case 'receive': return ReceivePayment
    case 'refund': return ReviewRefund
    case 'review': return BuyerReview
    case 'level': return SellerLevel
    default: return Overview
  }
})

function handleMenuSelect(index) {
  activeMenu.value = index
}

// 商品橱窗菜单点击处理
function handleProductClick() {
  activeMenu.value = 'product'
}

// 以下为各功能区组件（可拆分到单独文件）
const Overview = {
  template: `<el-card><h2>欢迎来到商家中心</h2><p>这里可以管理您的商品、订单、评价和等级。</p></el-card>`
}

// 已下架商品组件 - 复用ProductShowcase但只显示已下架商品
const UnpublishProduct = {
  setup() {
    // 通过provide向下传递预设的筛选条件，只显示已下架状态
    provide('defaultStatus', 2); // 2表示已下架状态
    provide('showTitle', '已下架商品'); // 自定义标题
    provide('hideStatusFilter', true); // 隐藏状态过滤标签
    return () => h(ProductShowcase);
  }
}

const ShipOrder = {
  template: `<el-card><h3>发货</h3><p>买家付款后，进行发货操作。</p></el-card>`
}

const ReceivePayment = {
  template: `<el-card><h3>已卖出宝贝</h3><p>买家点击收货后，卖家收到货款（扣除平台费用）。</p></el-card>`
}
const ReviewRefund = {
  template: `<el-card><h3>售后服务</h3><p>审核退货请求，驳回或同意退货。</p></el-card>`
}
const BuyerReview = {
  template: `<el-card><h3>买家交易的评价</h3><p>查看买家对交易的评价，形成商家好评率。</p></el-card>`
}
const SellerLevel = {
  template: `<el-card><h3>卖家等级</h3><p>根据交易额和数量，展示不同等级及权益。</p></el-card>`
}
</script>

<style scoped>
.el-menu-vertical-demo {
  border-right: none;
}

.publish-product-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.publish-form {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-form {
  margin-top: 20px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  line-height: 120px;
}

:deep(.el-form-item__content) {
  max-width: 600px;
}
</style>