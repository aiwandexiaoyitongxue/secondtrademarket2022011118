<!-- 注册页面 -->
<template>
    <div class="register-container">
      <el-card class="register-card">
        <template #header>
          <div class="card-header">
            <h2>用户注册</h2>
          </div>
        </template>
        
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码"></el-input>
          </el-form-item>
  
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
  
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
            <el-button @click="goToLogin">返回登录</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { register } from '@/api/user'
  
  const router = useRouter()
  const loading = ref(false)
  const registerFormRef = ref()
  
  const registerForm = reactive({
    username: '',
    password: '',
    confirmPassword: '',
    phone: '',
    realName: ''
  })
  
  const validatePass2 = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.password) {
      callback(new Error('两次输入密码不一致!'))
    } else {
      callback()
    }
  }
  
  const validatePhone = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请输入手机号'))
    } else if (!/^1[3-9]\d{9}$/.test(value)) {
      callback(new Error('请输入正确的手机号格式'))
    } else {
      callback()
    }
  }
  
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, validator: validatePass2, trigger: 'blur' }
    ],
    phone: [
      { required: true, validator: validatePhone, trigger: 'blur' }
    ],
    realName: [
      { required: true, message: '请输入真实姓名', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ]
  }
  
  const handleRegister = async () => {
    if (!registerFormRef.value) return
    
    await registerFormRef.value.validate(async (valid) => {
      if (valid) {
        loading.value = true
        try {
          const { username, password, phone, realName } = registerForm
          await register({ username, password, phone, realName })
          ElMessage.success('注册成功')
          router.push('/login')
        } catch (error) {
          console.error('注册失败:', error)
          ElMessage.error((error.response && error.response.data && error.response.data.message) || error.message || '注册失败')
        } finally {
          loading.value = false
        }
      }
    })
  }
  
  const goToLogin = () => {
    router.push('/login')
  }
  </script>
  
  <style scoped>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f5f5;
  }
  
  .register-card {
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