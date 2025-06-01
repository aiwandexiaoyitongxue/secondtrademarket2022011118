<template>
  <div class="personal-center">
    <el-card v-loading="loading" class="user-info-card">
      <div class="user-info-header">
        <span>个人信息</span>
        <el-button type="primary" @click="showEditDialog">编辑信息</el-button>
          <el-button type="primary" @click="passwordDialogVisible = true" style="margin-left: 20px;">
          修改密码
        </el-button>
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
        <el-descriptions-item label="城市">{{ user.city }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ user.gender === 1 ? '男' : user.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="个人介绍">{{ user.description }}</el-descriptions-item>
        <el-descriptions-item label="微信号">{{ user.wechat }}</el-descriptions-item>
        <el-descriptions-item label="角色">
        {{ user.role === 0 ? '普通用户' : user.role === 1 ? '商家' : '管理员' }}
        </el-descriptions-item>
        <el-descriptions-item label="头像">
        <img :src="avatarUrl" style="width:64px;height:64px;border-radius:50%;" />
      </el-descriptions-item>
      </el-descriptions>
    </el-card>
     <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑个人信息" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="editForm.city" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender">
            <el-option :value="0" label="未知" />
            <el-option :value="1" label="男" />
            <el-option :value="2" label="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="个人介绍">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="editForm.wechat" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/user/avatar"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            name="file" 
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateUserInfo">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updatePassword">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
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
import { ref, onMounted, computed  } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AddressList from './AddressList.vue'
import OrderList from './OrderList.vue'
import CartList from './CartList.vue'
import CommentList from './CommentList.vue'

const user = ref({})
const wallet = ref({ balance: 0, points: 0 })
const activeTab = ref('address')
const loading = ref(false)
// 编辑相关
const editDialogVisible = ref(false)
const editForm = ref({
  realName: '',
  phone: '',
  email: '',
  city: '',
  gender: 0,
  description: '',
  wechat: '',
  avatar: ''
})
const avatarUrl = computed(() => {
  if (!user.value.avatar) return ''
  if (user.value.avatar.startsWith('http')) return user.value.avatar
  // 判断当前端口，如果是5173（开发环境），用8080
  if (window.location.port === '5173') {
    return 'http://localhost:8080' + user.value.avatar
  }
  // 生产环境用当前origin
  return window.location.origin + user.value.avatar
})
// 密码修改相关
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 上传相关
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 显示编辑对话框
const showEditDialog = () => {
  editForm.value = { ...user.value }
  editDialogVisible.value = true
}

// 更新用户信息
const updateUserInfo = async () => {
  try {
    const res = await request.put('/user/update', editForm.value)
    if (res.success) {
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      await fetchUserInfo() // 刷新用户信息
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败，请稍后重试')
  }
}

// 修改密码
const updatePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  try {
    const res = await request.put('/user/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (res.success) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改失败，请稍后重试')
  }
}

// 头像上传相关
const handleAvatarSuccess = async (res) => {
  if (res.success) {
    editForm.value.avatar = res.data
    ElMessage.success('头像上传成功，正在保存...')
    await updateUserInfo() // 自动保存用户信息
  } else {
    ElMessage.error(res.message || '头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/user/info')
    if (res.success) {
      user.value = res.data
    } else {
      ElMessage.error(res.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const fetchWalletInfo = async () => {
  try {
    const res = await request.get('/api/user/wallet')
    if (res.success) {
      wallet.value = res.data
    } else {
      ElMessage.error(res.message || '获取钱包信息失败')
    }
  } catch (error) {
    console.error('获取钱包信息失败:', error)
    ElMessage.error('获取钱包信息失败')
  }
}

onMounted(async () => {
  await fetchUserInfo()
  await fetchWalletInfo()
})
</script>

<style scoped>
.personal-center {
  max-width: 100%;
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
.avatar-uploader {
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>