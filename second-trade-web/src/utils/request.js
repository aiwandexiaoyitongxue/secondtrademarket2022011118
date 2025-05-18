import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// axios.defaults.baseURL = 'http://localhost:8081/api'

const service = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    console.log(`🚀 发送请求: ${config.method.toUpperCase()} ${config.url}`)
    
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')
    const username = localStorage.getItem('username')
    
    console.log(`当前用户信息: 用户名=${username || '未登录'}, 角色=${role || '未知'}, 令牌=${token ? '有效' : '无效'}`)
    
    if (token) {
      // 确保headers对象存在
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('已添加授权令牌到请求头')
    } else {
      console.warn('未找到授权令牌，请求可能被拒绝')
    }
    
    // 商品发布请求的特殊处理
    if (config.url && config.url.includes('/products/publish')) {
      console.log('检测到商品发布请求')
      
      // 检查当前用户角色是否为商家
      if (role !== '1') {
        console.warn(`当前用户角色(${role})不是商家(1)，发布商品请求可能被拒绝`)
      }
      
      // 如果是表单上传
      if (config.headers && config.headers['Content-Type'] === 'multipart/form-data') {
        console.log('检测到表单上传请求')
        // 检查数据是否正确
        if (config.data instanceof FormData) {
          let hasFiles = false
          let hasProduct = false
          
          for (const pair of config.data.entries()) {
            if (pair[0] === 'images' && pair[1] instanceof File) {
              hasFiles = true
              console.log(`包含文件: ${pair[1].name}, 大小: ${pair[1].size} 字节`)
            }
            if (pair[0] === 'product') {
              hasProduct = true
              try {
                const productData = JSON.parse(pair[1])
                console.log('商品数据:', productData)
              } catch (e) {
                console.warn('无法解析商品数据:', pair[1])
              }
            }
          }
          
          if (!hasFiles) {
            console.warn('表单中没有找到有效的文件数据')
          }
          
          if (!hasProduct) {
            console.warn('表单中没有找到商品数据')
          }
        }
      }
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log(`✅ 收到响应: ${response.config.url} - 状态: ${response.status}`)
    
    // 如果是blob类型，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }
    
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口请求有误
    if (res.code !== 200) {
      // 记录错误信息
      console.error(`请求失败: ${response.config.url} - 错误码: ${res.code} - 消息: ${res.message}`)
      
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 处理具体错误码
      if (res.code === 401) {
        console.warn('登录已过期，即将跳转到登录页')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      } else if (res.code === 403) {
        console.warn('没有权限访问此资源')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    // 详细记录错误信息
    console.error(`❌ 请求失败: ${error.config?.url || '未知URL'}`)
    console.error('错误详情:', error)
    
    const status = error.response?.status
    
    // 根据不同的HTTP状态码处理
    if (status) {
      console.error(`HTTP状态码: ${status} - ${error.response.statusText}`)
      
      switch (status) {
        case 400:
          console.error('错误的请求参数')
          break
        case 401:
          console.warn('登录已过期，即将跳转到登录页')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          console.warn('没有权限访问此资源')
          // 对于图片上传可能是因为未登录或权限不足
          if (error.config?.url?.includes('products/publish')) {
            ElMessage({
              message: '商品发布失败: 您可能未登录或没有发布权限',
              type: 'error',
              duration: 5 * 1000
            })
            return
          }
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          break
      }
    }
    
    // 显示错误消息
    const errorMsg = error.response?.data?.message || error.message || 'Error'
    ElMessage({
      message: errorMsg,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

export default service 