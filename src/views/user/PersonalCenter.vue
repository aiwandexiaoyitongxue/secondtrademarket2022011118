<template>
  <div class="personal-center">
    <el-card v-loading="loading" class="user-info-card">
      <div class="user-info-header">
        <span>个人信息</span>
        <el-tag v-if="user.status === 0" type="warning">待审核</el-tag>
        <el-tag v-else-if="user.status === 1" type="success">已审核</el-tag>
        <el-tag v-else type="danger">审核未通过</el-tag>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ user.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          {{ user.role === 1 ? '管理员' : user.role === 2 ? '商家' : '普通用户' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="wallet-card" style="margin-top: 20px;">
      <div>钱包余额：<b>{{ wallet.balance }} 元</b>　积分：<b>{{ wallet.points }}</b></div>
    </el-card>

    <el-tabs v-model="activeTab" style="margin-top: 20px;">
      <el-tab-pane label="收货地址" name="address">
        <address-list :userId="user.id" />
      </el-tab-pane>
      <el-tab-pane label="我的订单" name="orders">
        <order-list :userId="user.id" />
      </el-tab-pane>
      <el-tab-pane label="购物车" name="cart">
        <cart-list :userId="user.id" />
      </el-tab-pane>
      <el-tab-pane label="我的评价" name="comments">
        <comment-list :userId="user.id" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import AddressList from './AddressList.vue'
import OrderList from './OrderList.vue'
import CartList from './CartList.vue'
import CommentList from './CommentList.vue'

const user = ref({})
const wallet = ref({ balance: 0, points: 0 })
const activeTab = ref('address')
const loading = ref(false)

const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/info')
    if (res.success) {
      user.value = res.data
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const fetchWalletInfo = async () => {
  try {
    const res = await request.get('/user/wallet')
    if (res.success) {
      wallet.value = res.data
    } else {
      ElMessage.error(res.message || '获取钱包信息失败')
    }
  } catch (error) {
    console.error('获取钱包信息失败:', error)
    ElMessage.error('获取钱包信息失败，请稍后重试')
  }
}

onMounted(async () => {
  await fetchUserInfo()
  await fetchWalletInfo()
})
</script>

<style scoped>
.personal-center {
  max-width: 80%;
margin-left: 10px;   /* 距离侧边栏10px */
  margin-right: auto;
  padding: 24px 0;
}
.user-info-card {
  margin-bottom: 16px;
}
.user-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
}
.wallet-card {
  font-size: 16px;
}
</style>