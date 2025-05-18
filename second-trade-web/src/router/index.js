import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'

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
    component: () => import('@/views/SellerHome.vue'),
    meta: { requiresAuth: true, requiresRole: 1 }
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
  const token = localStorage.getItem('token')
  const role = Number(localStorage.getItem('role'))

  if (to.matched.some(record => record.meta && record.meta.requiresAuth)) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.requiresRole && to.meta.requiresRole !== role) {
      // 如果路由需要特定角色，但用户角色不匹配
      switch (role) {
        case 0:
          next('/user-home')
          break
        case 1:
          next('/seller')
          break
        case 2:
          next('/admin')
          break
        default:
          next('/user-home')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router 