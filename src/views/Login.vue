<!-- 登录页面 -->
<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>用户登录</h2>
        </div>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
          <el-button @click="goToRegister">注册账号</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 清除旧的用户信息
        localStorage.clear()
        
        const res = await login(loginForm)
        if (res.success) {
          // 从响应中获取角色值并确保它是数字类型
          const roleValue = res.data.role
          let roleNumber
          
          if (typeof roleValue === 'string') {
            // 如果角色是字符串格式（如 "ROLE_0"），提取数字部分
            if (roleValue.startsWith('ROLE_')) {
              roleNumber = parseInt(roleValue.replace('ROLE_', ''), 10)
            } else {
              roleNumber = parseInt(roleValue, 10)
            }
          } else {
            roleNumber = Number(roleValue)
          }
          
          // 存储用户信息
          const userInfo = {
            ...res.data,
            role: roleNumber
          }
          
          // 保存token，确保不包含 Bearer 前缀
          const token = res.data.token.replace(/^Bearer\s+/i, '')
          localStorage.setItem('token', token)
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          localStorage.setItem('userRole', String(roleNumber))
          localStorage.setItem('username', res.data.username)
          
          // 如果是商家用户，先获取商家信息
          if (roleNumber === 1) {
            try {
              const merchantRes = await request.get('/merchant/info')
              if (merchantRes.success && merchantRes.data) {
                localStorage.setItem('merchantId', String(merchantRes.data.id))
              }
            } catch (error) {
              console.error('获取商家信息失败:', error)
              ElMessage.error('获取商家信息失败，请稍后重试')
              return
            }
          }
          
          ElMessage.success(res.message || '登录成功')
          
          // 获取重定向信息
          const { redirect, action, productId, quantity } = route.query
          
          // 根据用户角色和重定向信息决定跳转路径
          if (roleNumber === 1) {
            router.replace('/seller')
          } else if (roleNumber === 2) {
            router.replace('/admin')
          } else if (action === 'buyNow' && redirect === '/checkout') {
            router.replace({
              path: redirect,
              query: {
                productId: productId,
                quantity: quantity || 1
              }
            })
          } else if (redirect) {
            router.replace(redirect)
          } else {
            router.replace('/user/dashboard')
          }
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.response?.data?.message || error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 480px;
}

.card-header {
  text-align: center;
}

.el-button {
  width: 100%;
  margin-top: 10px;
}
</style>