import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/api/auth/captcha',
    method: 'get',
    responseType: 'blob'  // 指定响应类型为blob
  })
}

// 验证验证码
export function verifyCaptcha(captcha) {
  return request({
    url: '/api/auth/verify-captcha',
    method: 'post',
    data: { captcha }
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}