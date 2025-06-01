import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

export function getUserAddresses() {
  return request({
    url: '/user/addresses',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/user/addresses',
    method: 'post',
    data
  })
}

export function updateAddress(id, data) {
  return request({
    url: `/user/addresses/${id}`,
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/user/addresses/${id}`,
    method: 'delete'
  })
}

export function setDefaultAddress(id) {
  return request({
    url: `/user/addresses/${id}/default`,
    method: 'put'
  })
}

export function getUserWallet() {
  return request({
    url: '/user/wallet',
    method: 'get'
  })
}

export function getRechargeRecords() {
  return request({
    url: '/user/wallet/recharge/records',
    method: 'get'
  })
}

export function getPaymentRecords() {
  return request({
    url: '/user/wallet/payment/records',
    method: 'get'
  })
}