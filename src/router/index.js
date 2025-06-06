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
  // 添加结算页面路由
  {
    path: '/order/checkout',
    name: 'OrderCheckout',
    component: () => import('@/views/OrderCheckout.vue'),
    meta: { requiresAuth: true }  // 需要登录才能访问
  },
  {
    path: '/user',
    component: () => import('@/views/user/UserDashboard.vue'), // 主布局（含侧边栏）
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: 'dashboard' }, // 默认跳转到首页
      { path: 'dashboard', name: 'UserHome', component: () => import('@/views/user/UserHome.vue') }, // 首页内容
      { path: 'personal', name: 'PersonalCenter', component: () => import('@/views/user/PersonalCenter.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/user/CartList.vue') },
      { path: 'orders', name: 'Orders', component: () => import('@/views/user/OrderList.vue') },
      {
        path: 'wallet',
        component: () => import('@/views/user/wallet/Wallet.vue'), // 这里用 Wallet.vue
        children: [
          { path: '', redirect: 'balance' },
          { path: 'balance', name: 'WalletBalance', component: () => import('@/views/user/wallet/Balance.vue') },
          { path: 'recharge', name: 'WalletRecharge', component: () => import('@/views/user/wallet/Recharge.vue') },
          { path: 'payment', name: 'WalletPayment', component: () => import('@/views/user/wallet/Payment.vue') }
        ]
      },
      { path: 'points', name: 'Points', component: () => import('@/views/user/Points.vue') },
      { path: 'coupons', name: 'Coupons', component: () => import('@/views/user/Coupons.vue') },
      { path: 'comments', name: 'Comments', component: () => import('@/views/user/CommentList.vue') },
      { path: 'merchant-comments', name: 'MerchantComments', component: () => import('@/views/user/MerchantCommentList.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    children: [
      { path: 'product-audit', component: () => import('@/views/admin/AdminProductAudit.vue') },
      { path: 'user-audit', component: () => import('@/views/admin/AdminUserAudit.vue') },
      { path: 'merchant-audit', component: () => import('@/views/admin/AdminMerchantAudit.vue') },
      { path: 'user-manage', component: () => import('@/views/admin/AdminUserManage.vue') },
      { path: 'fee-manage', component: () => import('@/views/admin/AdminFeeManage.vue') },
      { path: 'merchant-level', component: () => import('@/views/admin/AdminMerchantLevel.vue') },
    ]
  },
  // 商家中心
  {
    path: '/seller',
    name: 'SellerHome',
    component: () => import('@/views/seller/SellerHome.vue'),
    meta: { requiresAuth: true, requiresRole: 1 }
  },
  {
    path: '/seller/product-detail',
    name: 'SellerProductDetail',
    component: () => import('@/views/ProductDetail.vue')
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.requiresRole && parseInt(userRole) !== to.meta.requiresRole) {
      // 如果路由需要特定角色，但用户角色不匹配
      next({ path: '/home' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router