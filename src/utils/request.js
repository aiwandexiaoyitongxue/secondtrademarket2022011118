import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // API 的基础URL
  timeout: 5000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(
  config => {
    // 添加 /api 前缀
    if (!config.url.startsWith('/api')) {
      config.url = '/api' + config.url
    }

    // 只把登录、注册、验证码等接口当成公共API
    const publicApis = [
      '/api/user/login', 
      '/api/user/register', 
      '/api/auth/captcha', 
      '/api/auth/verify-captcha'
    ]
    const isPublicApi = publicApis.some(api => config.url === api)
    if (!isPublicApi) {
      const token = localStorage.getItem('token')
      if (!token) {
        console.error('未找到token')
        ElMessage.error('未登录或登录已过期')
        router.push('/login')
        return Promise.reject(new Error('未登录或登录已过期'))
      }
      // 确保token格式正确
      const cleanToken = token.replace(/^Bearer\s+/i, '')
      if (!cleanToken) {
        console.error('token格式无效')
        ElMessage.error('无效的token')
        router.push('/login')
        return Promise.reject(new Error('无效的token'))
      }
      // 设置Authorization头，确保添加Bearer前缀
      config.headers['Authorization'] = `Bearer ${cleanToken}`
      console.log('已添加认证头:', config.headers['Authorization'])
    }

    // 如果是 FormData，删除 Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
      console.log('检测到FormData，已删除Content-Type头')
    }

    console.log('请求配置:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data instanceof FormData ? 'FormData对象' : config.data
    })
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// response拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log('响应数据:', res)  // 添加响应数据日志
    
    // 如果响应类型是blob，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }
    
    // 如果响应是字符串"success"，认为是成功
    if (res === 'success') {
      return { success: true, message: '操作成功' }
    }
    
    // 兼容后端直接返回数组/对象的情况
    if (Array.isArray(res) || (typeof res === 'object' && res !== null && !('code' in res) && !('success' in res))) {
      return res
    }
    // 如果响应成功
    if (res.success || res.code === 200) {
      return res
    }

    // 处理业务错误
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message || '操作失败'))
  },
  error => {
    // 详细记录错误信息
    console.error('响应错误:', error)
    console.error('错误状态码:', error.response?.status)
    console.error('错误响应数据:', error.response?.data)
    console.error('错误请求配置:', {
      url: error.config?.url,
      method: error.config?.method,
      headers: error.config?.headers,
      data: error.config?.data
    })
    
    // 处理401错误
    if (error.response && error.response.status === 401) {
      localStorage.clear()
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    
    // 处理500错误
    if (error.response && error.response.status === 500) {
      const errorMsg = error.response.data?.message || '服务器内部错误，请稍后重试'
      console.error('服务器错误详情:', error.response.data)
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    
    // 处理其他错误
    const errorMessage = error.response?.data?.message || error.message || '请求失败'
    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

export default service