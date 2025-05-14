import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue')
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    children: [
      { path: 'product-audit', component: () => import('@/views/admin/AdminProductAudit.vue') },
      { path: 'user-audit', component: () => import('@/views/admin/AdminUserAudit.vue') },
      { path: 'merchant-audit', component: () => import('@/views/admin/AdminMerchantAudit.vue') },
      { path: 'pending-list', component: () => import('@/views/admin/AdminPendingList.vue') },
      { path: 'user-manage', component: () => import('@/views/admin/AdminUserManage.vue') },
      { path: 'fee-manage', component: () => import('@/views/admin/AdminFeeManage.vue') },
      { path: 'merchant-level', component: () => import('@/views/admin/AdminMerchantLevel.vue') },
    ]
  },
  

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router