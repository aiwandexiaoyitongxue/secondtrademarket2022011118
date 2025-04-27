import axios from 'axios'

const service = axios.create({
  baseURL: '/', // 可根据实际后端接口地址调整
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可在此处添加 token
    // const token = localStorage.getItem('token')
    // if (token) config.headers['Authorization'] = token
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
)

export default service 