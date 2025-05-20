import request from '@/utils/request'

export function getAllCategories() {
  return request({
    url: '/api/categories/all',
    method: 'get'
  })
} 