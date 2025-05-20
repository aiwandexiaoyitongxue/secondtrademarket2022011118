<template>
  <div class="dashboard-layout">
    <!-- 左侧功能导航 -->
    <aside class="side-nav">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
        router
      >
        <el-menu-item index="/user/dashboard">
          <i class="el-icon-s-home"></i>
          <span>商城首页</span>
        </el-menu-item>
        <el-menu-item index="/user/personal">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item index="/user/cart">
          <i class="el-icon-shopping-cart-full"></i>
          <span>购物车</span>
        </el-menu-item>
        <el-menu-item index="/user/orders">
          <i class="el-icon-tickets"></i>
          <span>我的订单</span>
        </el-menu-item>
        <!-- 这里是钱包的下拉菜单 -->
        <el-sub-menu index="/user/wallet">
          <template #title>
            <i class="el-icon-money"></i>
            <span>我的钱包</span>
          </template>
          <el-menu-item index="/user/wallet/balance">账户余额</el-menu-item>
          <el-menu-item index="/user/wallet/recharge">充值记录</el-menu-item>
          <el-menu-item index="/user/wallet/payment">支付记录</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/user/points">
          <i class="el-icon-star-on"></i>
          <span>我的积分</span>
        </el-menu-item>
        <el-menu-item index="/user/coupons">
          <i class="el-icon-tickets"></i>
          <span>优惠券</span>
        </el-menu-item>
         <!-- 新增：我的评价 -->
        <el-menu-item index="/user/comments">
          <i class="el-icon-chat-dot-round"></i>
          <span>我的评价</span>
        </el-menu-item>
        <!-- 新增：商家对我的评价 -->
        <el-menu-item index="/user/merchant-comments">
          <i class="el-icon-user-solid"></i>
          <span>商家对我的评价</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <!-- 右侧主内容区 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const activeMenu = ref(route.path)

watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
  }
)

function handleMenuSelect(index) {
  router.push(index)
}
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}
:deep(.el-menu-vertical-demo .el-sub-menu__title) {
  padding-left: 0 !important;
  font-weight: normal !important;
  line-height: 48px !important; /* 和 el-menu-item 一致 */
  height: 48px !important;
  display: flex;
  align-items: center;
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
</style>