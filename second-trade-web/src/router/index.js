import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import SellerHome from '@/views/SellerHome.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/user-home',
    name: 'UserHome',
    component: () => import('@/views/UserHome.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/seller',
    name: 'SellerHome',
    component: SellerHome,
    meta: {
      requiresAuth: true,
      requiresRole: 1
    }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { requiresAuth: true, requiresRole: 2 }
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta.requiresRole) {
      const role = localStorage.getItem('role')
      if (role !== String(to.meta.requiresRole)) {
        next('/login')
        return
      }
    }
  }
  next()
})

export default router 