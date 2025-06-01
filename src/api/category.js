import request from '@/utils/request'

export function getAllCategories() {
  return request({
    url: '/categories/all',
    method: 'get'
  })
} 