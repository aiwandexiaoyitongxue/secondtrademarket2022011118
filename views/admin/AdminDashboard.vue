<template>
  <el-container style="height: 100vh;">
    <el-aside width="220px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
      >
        <el-menu-item index="product-audit">审核发布商品</el-menu-item>
        <el-menu-item index="user-audit">审核用户注册</el-menu-item>
        <el-menu-item index="merchant-audit">审核商家注册</el-menu-item>
        <el-menu-item index="pending-list">待审核信息列表</el-menu-item>
        <el-menu-item index="user-manage">管理用户</el-menu-item>
        <el-menu-item index="fee-manage">平台交易费用</el-menu-item>
        <el-menu-item index="merchant-level">卖家等级调整</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeMenu = ref('')

function handleMenuSelect(index) {
  activeMenu.value = index
  // 跳转到对应路由
  switch (index) {
    case 'product-audit':
      router.push('/admin/product-audit')
      break
    case 'user-audit':
      router.push('/admin/user-audit')
      break
    case 'merchant-audit':
      router.push('/admin/merchant-audit')
      break
    case 'pending-list':
      router.push('/admin/pending-list')
      break
    case 'user-manage':
      router.push('/admin/user-manage')
      break
    case 'fee-manage':
      router.push('/admin/fee-manage')
      break
    case 'merchant-level':
      router.push('/admin/merchant-level')
      break
  }
}
</script>

<style scoped>
.el-aside {
  background: #f5f7fa;
  min-height: 100vh;
}
.el-main {
  background: #fff;
  padding: 24px;
}
</style>