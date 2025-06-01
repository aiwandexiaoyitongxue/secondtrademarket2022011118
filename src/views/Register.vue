<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
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

          <el-form-item label="用户类型" prop="userType">
          <el-radio-group v-model="registerForm.userType">
            <el-radio :value="'personal'">个人用户</el-radio>
            <el-radio :value="'business'">商家用户</el-radio>
            <el-radio :value="'admin'">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>

        <el-form-item label="城市" prop="city">
          <el-input v-model="registerForm.city" placeholder="请输入所在城市"></el-input>
        </el-form-item>

       <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="registerForm.gender">
          <el-radio :value="1">男</el-radio>
          <el-radio :value="2">女</el-radio>
        </el-radio-group>
      </el-form-item>

        <el-form-item label="银行账号" prop="bankAccount">
          <el-input v-model="registerForm.bankAccount" placeholder="请输入16位银行账号"></el-input>
        </el-form-item>

        <el-form-item label="营业执照" prop="businessLicense" v-if="registerForm.userType === 'business'">
          <el-upload
            class="upload-demo"
            action="/api/upload/public"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload">
            <el-button type="primary">上传营业执照</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="身份证照片" prop="idCard" v-if="registerForm.userType === 'business'">
          <el-upload
            class="upload-demo"
            action="/api/upload/public"
            :on-success="handleIdCardUploadSuccess"
            :before-upload="beforeUpload">
            <el-button type="primary">上传身份证照片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="验证码" prop="captcha">
          <div class="captcha-container">
            <el-input v-model="registerForm.captcha" placeholder="请输入验证码" style="width: 150px;"></el-input>
            <img :src="captchaUrl" @click="refreshCaptcha" class="captcha-img" alt="验证码">
          </div>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, getCaptcha } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const captchaUrl = ref('')

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  userType: 'personal',
  realName: '',
  phone: '',
  email: '',
  city: '',
  gender: 1,
  bankAccount: '',
  businessLicense: '',
  idCard: '', // 新增
  captcha: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validateBankAccount = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入银行账号'))
  } else if (!/^\d{16}$/.test(value)) {
    callback(new Error('银行账号必须是16位数字'))
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
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入所在城市', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  bankAccount: [
    { required: true, validator: validateBankAccount, trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const refreshCaptcha = async () => {
  try {
    const response = await getCaptcha()
    // 将blob转换为URL
    const imageUrl = URL.createObjectURL(response)
    captchaUrl.value = imageUrl
  } catch (error) {
    console.error('获取验证码错误:', error)
    ElMessage.error('获取验证码失败')
  }
}

onMounted(() => {
  refreshCaptcha()
})

// 组件卸载时清理URL
onUnmounted(() => {
  if (captchaUrl.value) {
    URL.revokeObjectURL(captchaUrl.value)
  }
})

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    registerForm.businessLicense = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}
const handleIdCardUploadSuccess = (response) => {
  if (response.code === 200) {
    registerForm.idCard = response.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传文件只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // userType 转 role
        let role = 0
        if (registerForm.userType === 'personal') role = 0
        else if (registerForm.userType === 'business') role = 1
        else if (registerForm.userType === 'admin') role = 2

        // 构造提交数据
        const submitData = { ...registerForm, role }
        delete submitData.userType

        // 打印 userType 和 submitData
        console.log('userType:', registerForm.userType);
        console.log('submitData:', submitData);

        const response = await register(submitData)
        if (response.code === 200) {
          ElMessage.success('注册成功')
          router.push('/login')
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败，请稍后重试')
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
  min-height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 500px;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-img {
  height: 40px;
  cursor: pointer;
}

.el-button {
  width: 100%;
  margin-top: 10px;
}

.upload-demo {
  width: 100%;
}
</style>`