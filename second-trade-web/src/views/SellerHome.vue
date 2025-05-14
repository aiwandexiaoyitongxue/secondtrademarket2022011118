<template>
  <el-container style="height: 100vh;">
    <!-- 侧边导航 -->
    <el-aside width="220px" style="background: #fff; border-right: 1px solid #eee;">
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" @select="handleMenuSelect">
        <el-menu-item index="overview"><el-icon><HomeFilled /></el-icon>首页概览</el-menu-item>
        <el-sub-menu index="product">
          <template #title><el-icon><GoodsFilled /></el-icon>商品管理</template>
          <el-menu-item index="publish">发布商品</el-menu-item>
          <el-menu-item index="showcase">商品橱窗</el-menu-item>
          <el-menu-item index="browse">浏览商品</el-menu-item>
          <el-menu-item index="unpublish">下架商品</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="order">
          <template #title><el-icon><List /></el-icon>订单管理</template>
          <el-menu-item index="ship">发货</el-menu-item>
          <el-menu-item index="receive">收到货款</el-menu-item>
          <el-menu-item index="refund">审核退货申请</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="review"><el-icon><StarFilled /></el-icon>买家交易的评价</el-menu-item>
        <el-menu-item index="level"><el-icon><Medal /></el-icon>卖家等级</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-main style="background: #f5f7fa;">
      <component :is="currentView" />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { HomeFilled, GoodsFilled, List, StarFilled, Medal } from '@element-plus/icons-vue'

// 导航状态
const activeMenu = ref('overview')
const currentView = computed(() => {
  switch (activeMenu.value) {
    case 'overview': return Overview
    case 'publish': return PublishProduct
    case 'showcase': return ProductShowcase
    case 'browse': return BrowseProduct
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

// 以下为各功能区组件（可拆分到单独文件）
const Overview = {
  template: `<el-card><h2>欢迎来到商家中心</h2><p>这里可以管理您的商品、订单、评价和等级。</p></el-card>`
}
const PublishProduct = {
  template: `<el-card><h3>发布商品</h3><p>填写商品信息，上传图片，设置价格等。</p></el-card>`
}
const ProductShowcase = {
  template: `<el-card><h3>商品橱窗</h3><p>统一展示已发布商品，支持库存、销量、评价分数等。</p></el-card>`
}
const BrowseProduct = {
  template: `<el-card><h3>浏览商品</h3><p>查看所有已上架商品。</p></el-card>`
}
const UnpublishProduct = {
  template: `<el-card><h3>下架商品</h3><p>管理下架操作，普通用户无法浏览。</p></el-card>`
}
const ShipOrder = {
  template: `<el-card><h3>发货</h3><p>买家付款后，进行发货操作。</p></el-card>`
}
const ReceivePayment = {
  template: `<el-card><h3>收到货款</h3><p>买家点击收货后，卖家收到货款（扣除平台费用）。</p></el-card>`
}
const ReviewRefund = {
  template: `<el-card><h3>审核退货申请</h3><p>审核退货请求，驳回或同意退货。</p></el-card>`
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
</style>