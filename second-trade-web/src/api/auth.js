import request from '@/utils/request'

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get',
    responseType: 'blob'
  })
}

// 验证验证码
export function verifyCaptcha(captcha) {
  return request({
    url: '/auth/verify-captcha',
    method: 'post',
    data: { captcha }
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}