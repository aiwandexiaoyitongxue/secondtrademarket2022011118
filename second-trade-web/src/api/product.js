import request from '@/utils/request'

// 发布商品
export function publishProduct(data) {
  // 确保FormData中有图片数据
  let hasImages = false;
  if (data instanceof FormData) {
    for (const pair of data.entries()) {
      if (pair[0] === 'images') {
        hasImages = true;
        break;
      }
    }
    
    if (!hasImages) {
      console.warn('警告: FormData中没有图片数据');
    }
  }
  
  return request({
    url: '/api/products/publish',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 10000 // 增加超时时间，图片上传可能需要更长时间
  })
}

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/api/products/list',
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