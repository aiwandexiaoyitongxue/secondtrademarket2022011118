<template>
  <div class="header-bar">
    <el-button type="danger" @click="logout" size="small">退出登录</el-button>
  </div>
  <el-container style="height: 100vh;">
    <el-aside width="220px">
      <!-- 左侧菜单栏 -->
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
      >
        <el-menu-item index="product-audit">审核发布商品</el-menu-item>
        <el-menu-item index="user-audit">审核用户注册</el-menu-item>
        <el-menu-item index="merchant-audit">审核商家注册</el-menu-item>
        <el-menu-item index="user-manage">管理用户</el-menu-item>
        <el-menu-item index="fee-manage">平台交易费用</el-menu-item>
        <el-menu-item index="merchant-level">卖家等级调整</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <div v-if="!activeMenu">
        <div class="dashboard-welcome">
         <img
          src="https://openmoji.org/data/color/svg/1F43C.svg"
          alt="管理员"
          class="admin-avatar"
        />
          <div>
            <h2>欢迎来到管理员后台</h2>
            <p class="welcome-tip">您好，管理员！感谢您的辛勤管理。</p>
          </div>
        </div>

        <el-row :gutter="24" class="dashboard-cards">
  <el-col :span="6">
    <el-card class="stat-card user">
      <div class="stat-icon user"></div>
      <div>用户总数</div>
      <div class="stat-value">{{ statistics.userCount }}</div>
    </el-card>
  </el-col>
  <el-col :span="6">
    <el-card class="stat-card merchant">
      <div class="stat-icon merchant"></div>
      <div>商家总数</div>
      <div class="stat-value">{{ statistics.merchantCount }}</div>
    </el-card>
  </el-col>
  <el-col :span="6">
    <el-card class="stat-card product">
      <div class="stat-icon product"></div>
      <div>商品总数</div>
      <div class="stat-value">{{ statistics.productCount }}</div>
    </el-card>
  </el-col>

</el-row>

        <el-divider />
        <div style="margin-bottom: 24px;">
          <h3>平台公告</h3>
          <el-alert
            title="平台将于本月25日凌晨进行系统维护。"
            type="info"
            show-icon
            style="margin-bottom: 8px;"
          />
          <el-alert
            title="请及时审核新用户和新商品，保障平台安全。"
            type="info"
            show-icon
            style="margin-bottom: 8px;"
          />
        </div>
      </div>
      <router-view v-else />
    </el-main>
  </el-container>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const activeMenu = ref('')

const statistics = ref({
  userCount: 25,
  merchantCount: 10,
  productCount: 9
})

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  router.push('/login')
}
function handleMenuSelect(index) {
  activeMenu.value = index
  switch (index) {
    case 'product-audit':
      router.push('/admin/product-audit')
      break
    case 'user-audit':
      router.push('/admin/user-audit')
      break
    case 'merchant-audit':
      router.push('/admin/merchant-audit')
      break
    case 'user-manage':
      router.push('/admin/user-manage')
      break
    case 'fee-manage':
      router.push('/admin/fee-manage')
      break
    case 'merchant-level':
      router.push('/admin/merchant-level')
      break
  }
}


</script>
<style scoped>
.header-bar {
  position: absolute;
  top: 20px;
  right: 40px;
  z-index: 10;
}
.dashboard-welcome {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.admin-avatar {
  width: 64px;
  height: 64px;
  margin-right: 24px;
  border-radius: 50%;
  background: #f5f7fa;
  border: 1px solid #ebeef5;
}
.welcome-tip {
  color: #888;
  margin: 4px 0 0 0;
}
.dashboard-cards {
  margin-bottom: 24px;
}
.stat-card {
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.06);
  border-radius: 12px;
  padding: 18px 0;
  position: relative;
  overflow: hidden;
}
.stat-value {
  font-size: 2em;
  font-weight: bold;
  margin-top: 8px;
}
.stat-icon {
  width: 36px;
  height: 36px;
  margin: 0 auto 8px auto;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5em;
}
.stat-icon.pending { background: linear-gradient(135deg, #a3a380 0%, #e9d985 100%); }
.stat-icon.user { background: linear-gradient(135deg, #409EFF 0%, #6dd5fa 100%); }
.stat-icon.merchant { background: linear-gradient(135deg, #67C23A 0%, #b2f9b8 100%); }
.stat-icon.product { background: linear-gradient(135deg, #E6A23C 0%, #ffe29f 100%); }
.stat-icon.money { background: linear-gradient(135deg, #F56C6C 0%, #ffb6b9 100%); }
</style>