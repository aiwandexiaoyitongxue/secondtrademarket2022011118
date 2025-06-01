import request from '@/utils/request'

// 发布商品
export function publishProduct(data) {
  return request({
    url: '/api/products',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    transformRequest: [function (data) {
      // 如果是 FormData 对象，直接返回
      if (data instanceof FormData) {
        return data
      }
      return data
    }]
  })
}

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/api/products',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductDetail(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'get'
  })
}

// 更新商品
export function updateProduct(id, data) {
  return request({
    url: `/api/products/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/api/products/${id}`,
    method: 'delete'
  })
} 
// 新增：只更新商品状态（上架/下架）
export function updateProductStatus(id, data) {
  return request({
    url: `/api/products/${id}/status`,
    method: 'put',
    data
  })
}