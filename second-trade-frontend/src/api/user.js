import request from '@/utils/request'

// 用户API
export const userApi = {
  // 获取用户列表
  getList: (params) => request({
    url: '/api/user/list',
    method: 'get',
    params
  }),
  
  // 获取用户详情
  getDetail: (id) => request({
    url: `/api/user/${id}`,
    method: 'get'
  }),
  
  // 创建用户
  create: (data) => request({
    url: '/api/user',
    method: 'post',
    data
  }),
  
  // 更新用户
  update: (id, data) => request({
    url: `/api/user/${id}`,
    method: 'put',
    data
  }),
  
  // 删除用户
  delete: (id) => request({
    url: `/api/user/${id}`,
    method: 'delete'
  })
} 