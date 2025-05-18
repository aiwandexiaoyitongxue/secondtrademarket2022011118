<template>
  <div class="publish-product-container">
    <el-card class="publish-form">
      <template #header>
        <div class="card-header">
          <h3>发布商品</h3>
        </div>
      </template>
      
      <!-- 角色检查提示 -->
      <el-alert
        v-if="roleCheckResult.show"
        :title="roleCheckResult.message"
        :type="roleCheckResult.type"
        :description="roleCheckResult.description"
        show-icon
        :closable="false"
        style="margin-bottom: 20px">
        <template #default>
          <div v-if="roleCheckResult.type !== 'success'">
            <el-button type="primary" size="small" @click="goToLogin" style="margin-top: 10px">
              去登录
            </el-button>
            <el-button size="small" @click="showDebugInfo = !showDebugInfo" style="margin-top: 10px; margin-left: 10px">
              {{ showDebugInfo ? '隐藏问题诊断' : '问题诊断' }}
            </el-button>
          </div>
        </template>
      </el-alert>
      
      <!-- 调试信息面板 -->
      <el-collapse-transition>
        <div v-if="showDebugInfo" class="debug-panel">
          <h4>问题诊断</h4>
          <p>如果您遇到<span class="error-text">403权限错误</span>，请检查以下事项：</p>
          
          <el-divider />
          
          <h5>1. 检查登录状态</h5>
          <div class="debug-item">
            <p>当前登录信息:</p>
            <div class="debug-value">
              <p><strong>用户名：</strong>{{ userDebugInfo.username || '未登录' }}</p>
              <p><strong>角色ID：</strong>{{ userDebugInfo.role || '未知' }} 
                <span v-if="userDebugInfo.role === '1'" class="success-text">(商家账号 ✓)</span>
                <span v-else-if="userDebugInfo.role" class="error-text">(不是商家账号 ✗)</span>
              </p>
              <p><strong>Token：</strong>{{ userDebugInfo.hasToken ? '已存在' : '不存在' }}</p>
            </div>
            <el-button size="small" type="primary" @click="refreshDebugInfo">刷新信息</el-button>
          </div>
          
          <h5>2. 角色映射问题</h5>
          <div class="debug-item">
            <p class="warning-text">检测到可能的前后端角色映射问题!</p>
            <div class="debug-value">
              <p>前端角色ID：<code>{{ userDebugInfo.role }}</code> (应为1代表商家)</p>
              <p>后端期望角色：<code>ROLE_MERCHANT</code> (Spring Security角色)</p>
              <p class="info-text">可能原因：Spring Security要求角色名称以"ROLE_"前缀，可能您的token中缺少正确的角色授权</p>
            </div>
            <p>解决方案：</p>
            <ul>
              <li>确认您使用的是商家账号登录 (角色ID = 1)</li>
              <li>尝试重新登录获取新的token</li>
              <li>如果仍然失败，可能需要联系管理员或检查后端角色映射配置</li>
            </ul>
          </div>
          
          <h5>3. 测试商家登录</h5>
          <div class="debug-item">
            <p>您可以使用以下测试账号：</p>
            <div class="debug-value">
              <p><strong>商家账号：</strong>merchant</p>
              <p><strong>密码：</strong>123456</p>
            </div>
            <el-button type="success" size="small" @click="useTestAccount">使用测试账号</el-button>
          </div>
          
          <h5>4. 操作建议</h5>
          <div class="debug-item">
            <el-button type="warning" size="small" @click="clearLoginInfo">清除登录状态</el-button>
            <el-button type="primary" size="small" @click="goToLogin" style="margin-left: 10px">重新登录</el-button>
          </div>
        </div>
      </el-collapse-transition>
      
      <el-form :model="productForm" :rules="rules" ref="productFormRef" label-width="120px" class="product-form">
        <el-form-item label="商品图片" prop="images">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :file-list="fileList"
            :limit="5">
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传5张图片，建议尺寸800x800px</div>
        </el-form-item>

        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称"></el-input>
        </el-form-item>

        <el-form-item label="商品类目" prop="category">
          <el-cascader
            v-model="productForm.category"
            :options="categoryOptions"
            placeholder="请选择商品类目">
          </el-cascader>
        </el-form-item>

        <el-form-item label="商品成色" prop="condition">
          <el-select v-model="productForm.condition" placeholder="请选择商品成色">
            <el-option label="全新" :value="1"></el-option>
            <el-option label="九成新" :value="2"></el-option>
            <el-option label="八成新" :value="3"></el-option>
            <el-option label="七成新" :value="4"></el-option>
            <el-option label="六成新及以下" :value="5"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="原价" prop="originalPrice">
          <el-input-number 
            v-model="productForm.originalPrice" 
            :min="0" 
            :precision="2" 
            :step="0.1"
            placeholder="请输入商品原价">
          </el-input-number>
        </el-form-item>

        <el-form-item label="售价" prop="price">
          <el-input-number 
            v-model="productForm.price" 
            :min="0" 
            :precision="2" 
            :step="0.1"
            placeholder="请输入商品售价">
          </el-input-number>
        </el-form-item>

        <el-form-item label="库存数量" prop="stock">
          <el-input-number 
            v-model="productForm.stock" 
            :min="0" 
            :precision="0"
            placeholder="请输入库存数量">
          </el-input-number>
        </el-form-item>

        <el-form-item label="尺寸" prop="size">
          <el-input v-model="productForm.size" placeholder="请输入商品尺寸"></el-input>
        </el-form-item>

        <el-form-item label="是否可议价" prop="isNegotiable">
          <el-switch
            v-model="productForm.isNegotiable"
            :active-value="1"
            :inactive-value="0"
            active-text="可议价"
            inactive-text="不可议价">
          </el-switch>
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述商品的成色、使用年限、瑕疵等信息">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">发布商品</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { publishProduct } from '@/api/product'
import { useRouter } from 'vue-router'

const router = useRouter()
const productFormRef = ref(null)
const loading = ref(false)
const showDebugInfo = ref(false)
const fileList = ref([])

// 调试信息
const userDebugInfo = reactive({
  username: '',
  role: '',
  hasToken: false,
  userInfo: null
})

// 刷新调试信息
const refreshDebugInfo = () => {
  userDebugInfo.username = localStorage.getItem('username') || ''
  userDebugInfo.role = localStorage.getItem('role') || ''
  userDebugInfo.hasToken = !!localStorage.getItem('token')
  try {
    userDebugInfo.userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch (e) {
    userDebugInfo.userInfo = {}
  }
  console.log('当前用户调试信息:', userDebugInfo)
}

// 清除登录信息
const clearLoginInfo = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  localStorage.removeItem('userInfo')
  ElMessage.success('登录信息已清除')
  refreshDebugInfo()
  checkUserRole()
}

// 初始化获取调试信息
onMounted(() => {
  refreshDebugInfo()
  checkUserRole()
})

const productForm = reactive({
  images: [],
  name: '',
  category: [],
  condition: 1,
  originalPrice: 0,
  price: 0,
  stock: 0,
  size: '',
  isNegotiable: 0,
  description: ''
})

const categoryOptions = [
  {
    value: 1,
    label: '电子产品',
    children: [
      { value: 6, label: '手机' },
      { value: 7, label: '电脑' },
      { value: 8, label: '平板' },
      { value: 9, label: '耳机' }
    ]
  },
  {
    value: 2,
    label: '服装服饰',
    children: [
      { value: 10, label: '男装' },
      { value: 11, label: '女装' },
      { value: 12, label: '鞋靴' }
    ]
  },
  {
    value: 3,
    label: '图书教材',
    children: []
  },
  {
    value: 4,
    label: '生活用品',
    children: []
  },
  {
    value: 5,
    label: '运动器材',
    children: []
  }
]

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品类目', trigger: 'change' }
  ],
  condition: [
    { required: true, message: '请选择商品成色', trigger: 'change' }
  ],
  originalPrice: [
    { required: true, message: '请输入商品原价', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品售价', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ],
  size: [
    { required: true, message: '请输入商品尺寸', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ]
}

// 角色检查结果
const roleCheckResult = reactive({
  show: false,
  type: 'info',
  message: '',
  description: ''
})

// 检查当前用户是否有权限发布商品
const checkUserRole = () => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  const username = localStorage.getItem('username')
  
  console.log('检查用户权限:', { username, role, hasToken: !!token })
  
  if (!token) {
    roleCheckResult.show = true
    roleCheckResult.type = 'error'
    roleCheckResult.message = '您尚未登录'
    roleCheckResult.description = '请先登录后再发布商品'
    return false
  }
  
  if (role !== '1') {
    roleCheckResult.show = true
    roleCheckResult.type = 'warning'
    roleCheckResult.message = '您不是商家用户'
    roleCheckResult.description = '只有商家用户才能发布商品，请使用商家账号登录或升级为商家'
    return false
  }
  
  // 检查通过，显示欢迎信息
  roleCheckResult.show = true
  roleCheckResult.type = 'success'
  roleCheckResult.message = `欢迎，${username || '商家'}！`
  roleCheckResult.description = '您可以发布商品了'
  return true
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

const handleImageChange = (file) => {
  // 处理图片上传
  console.log('Image changed:', file)
  
  // 确保图片添加到productForm.images中
  if (file && file.raw) {
    // 查找是否已存在此图片
    const exists = productForm.images.some(img => img.name === file.name)
    if (!exists) {
      productForm.images.push(file)
      console.log('已添加图片到表单:', file.name)
    }
  }
}

// 处理图片删除的函数
const handleImageRemove = (file) => {
  console.log('Image removed:', file)
  
  // 从productForm.images中删除该图片
  const index = productForm.images.findIndex(img => img.name === file.name)
  if (index !== -1) {
    productForm.images.splice(index, 1)
    console.log('已从表单中删除图片:', file.name)
    console.log('当前表单中图片数量:', productForm.images.length)
    
    // 确保fileList也同步更新
    const fileIndex = fileList.value.findIndex(f => f.name === file.name)
    if (fileIndex !== -1) {
      fileList.value.splice(fileIndex, 1)
    }
  } else {
    console.warn('未能找到要删除的图片:', file.name)
  }
}

const submitForm = async () => {
  if (!productFormRef.value) return
  
  // 先检查用户角色
  if (!checkUserRole()) {
    ElMessage.warning('您没有发布商品的权限，请使用商家账号')
    showDebugInfo.value = true // 显示调试信息
    return
  }
  
  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 获取当前登录用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const token = localStorage.getItem('token')
        
        if (!userInfo || !userInfo.id) {
          throw new Error('请先登录')
        }

        if (!token) {
          throw new Error('登录已过期，请重新登录')
        }

        // 创建FormData对象用于提交
        const formData = new FormData()
        
        // 处理图片上传
        if (productForm.images && productForm.images.length > 0) {
          console.log('准备添加图片数据，数量:', productForm.images.length)
          
        productForm.images.forEach((file, index) => {
            if (file && file.raw) {
              console.log(`添加第${index+1}张图片:`, file.name)
          formData.append('images', file.raw)
            } else {
              console.warn(`第${index+1}张图片数据无效:`, file)
            }
        })
        } else {
          console.warn('没有图片数据需要上传')
          throw new Error('请至少上传一张商品图片')
        }
        
        // 添加其他表单数据
        const productData = {
          merchantId: userInfo.id, // 使用当前登录用户的ID作为商家ID
          name: productForm.name,
          categoryId: productForm.category[1],
          condition: productForm.condition,
          originalPrice: productForm.originalPrice,
          price: productForm.price,
          stock: productForm.stock,
          size: productForm.size,
          isNegotiable: productForm.isNegotiable,
          description: productForm.description
        }
        
        console.log('提交的商品数据:', productData)
        
        // 将商品数据转为JSON字符串，添加到formData
        formData.append('product', JSON.stringify(productData))
        
        // 发送请求
        const response = await publishProduct(formData)
        ElMessage.success('商品发布成功')
        resetForm()
      } catch (error) {
        console.error('发布商品失败:', error)
        
        // 根据错误类型显示不同提示
        if (error.response && error.response.status === 403) {
          ElMessage.error('没有发布商品的权限，请确认您已登录商家账号')
          // 显示调试面板
          showDebugInfo.value = true
          // 重新检查用户角色和刷新调试信息
          refreshDebugInfo()
          checkUserRole()
          
          // 提供更具体的错误指导
          if (userDebugInfo.role !== '1') {
            roleCheckResult.description = '您当前登录的账号不是商家账号。请使用商家账号登录，或者联系管理员升级您的账号。'
          } else if (!userDebugInfo.hasToken) {
            roleCheckResult.description = '登录状态已失效，请重新登录。'
          } else {
            roleCheckResult.description = '您的账号可能尚未通过审核或存在其他权限问题，请联系管理员。'
          }
        } else {
          ElMessage.error(error.response?.data?.message || error.message || '发布商品失败')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  if (!productFormRef.value) return
  productFormRef.value.resetFields()
  productForm.images = []
  fileList.value = []
}

// 使用测试账号
const useTestAccount = () => {
  // 模拟商家用户登录信息
  const testMerchant = {
    id: 2,
    username: 'merchant',
    role: 1,
    token: 'test-merchant-token-' + Date.now()
  }
  
  // 存储登录信息
  localStorage.setItem('userInfo', JSON.stringify(testMerchant))
  localStorage.setItem('token', testMerchant.token)
  localStorage.setItem('role', String(testMerchant.role))
  localStorage.setItem('username', testMerchant.username)
  
  ElMessage.success('已使用测试商家账号')
  
  // 刷新调试信息和权限状态
  refreshDebugInfo()
  checkUserRole()
  
  // 显示提示
  roleCheckResult.type = 'info'
  roleCheckResult.message = '使用测试账号 - 仅用于调试'
  roleCheckResult.description = '请注意：后端服务器仍可能拒绝此测试账号的请求。如果仍遇到403错误，需检查后端配置。'
}
</script>

<style scoped>
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

/* 调试面板样式 */
.debug-panel {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
}

.debug-item {
  margin-bottom: 15px;
}

.debug-value {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  margin: 5px 0;
}

.error-text {
  color: #f56c6c;
  font-weight: bold;
}

.success-text {
  color: #67c23a;
  font-weight: bold;
}

.warning-text {
  color: #e6a23c;
  font-weight: bold;
}

.info-text {
  color: #909399;
  font-style: italic;
}
</style> 