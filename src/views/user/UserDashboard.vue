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
          <el-icon><HomeFilled /></el-icon>
          <span>商城首页</span>
        </el-menu-item>
        <el-menu-item index="/user/personal">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item index="/user/cart">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </el-menu-item>
        <el-menu-item index="/user/orders">
          <el-icon><Tickets /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        <!-- 这里是钱包的下拉菜单 -->
        <el-sub-menu index="/user/wallet">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>我的钱包</span>
          </template>
          <el-menu-item index="/user/wallet/balance">
            <el-icon><Wallet /></el-icon>
            <span>账户余额</span>
          </el-menu-item>
          <el-menu-item index="/user/wallet/recharge">
            <el-icon><Plus /></el-icon>
            <span>充值</span>
          </el-menu-item>
          <el-menu-item index="/user/wallet/payment">
            <el-icon><CreditCard /></el-icon>
            <span>支付记录</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/user/points">
          <el-icon><Star /></el-icon>
          <span>我的积分</span>
        </el-menu-item>
        <el-menu-item index="/user/coupons">
          <el-icon><Ticket /></el-icon>
          <span>优惠券</span>
        </el-menu-item>
        <!-- 新增：我的评价 -->
        <el-menu-item index="/user/comments">
          <el-icon><ChatDotRound /></el-icon>
          <span>我的评价</span>
        </el-menu-item>
        <!-- 新增：商家对我的评价 -->
        <el-menu-item index="/user/merchant-comments">
          <el-icon><UserFilled /></el-icon>
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
import { 
  HomeFilled, 
  User, 
  ShoppingCart, 
  Tickets, 
  Money, 
  Wallet, 
  Plus, 
  CreditCard, 
  Star, 
  Ticket, 
  ChatDotRound, 
  UserFilled 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

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
  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 检查token是否有效
  const tokenValue = token.startsWith('Bearer ') ? token : `Bearer ${token}`
  request.get('/api/user/info', {
    headers: {
      Authorization: tokenValue
    }
  }).then((res) => {
    if (res.success) {
      // 保存用户信息
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      // 如果是商家,保存商家ID和状态
      if (res.data.role === 'MERCHANT') {
        localStorage.setItem('merchantId', res.data.merchantId)
        localStorage.setItem('merchantStatus', res.data.merchantStatus)
      }
      // token有效，允许导航
      router.push(index)
    }
  }).catch(() => {
    // token无效，清除用户信息并重定向到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userRole')
    localStorage.removeItem('username')
    localStorage.removeItem('merchantId')
    localStorage.removeItem('merchantStatus')
    ElMessage.error('登录已过期，请重新登录')
    router.push('/login')
  })
}
</script>

<style scoped>
.dashboard-layout {
  max-width: 100%;
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}
:deep(.el-menu-vertical-demo .el-sub-menu__title) {
  padding-left: 0 !important;
  font-weight: normal !important;
  line-height: 48px !important;
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
:deep(.el-menu-item) {
  display: flex;
  align-items: center;
}
:deep(.el-icon) {
  margin-right: 8px;
  font-size: 18px;
}
</style>