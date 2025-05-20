<template>
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
        <el-menu-item index="pending-list">待审核信息列表</el-menu-item>
        <el-menu-item index="user-manage">管理用户</el-menu-item>
        <el-menu-item index="fee-manage">平台交易费用</el-menu-item>
        <el-menu-item index="merchant-level">卖家等级调整</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <div v-if="!activeMenu">
       <div style="padding: 32px;">
  <div style="display: flex; align-items: center; margin-bottom: 24px;">
    <img
      src="https://cdn.jsdelivr.net/gh/edent/SuperTinyIcons/images/svg/user.svg"
      alt="管理员"
      style="width: 64px; height: 64px; margin-right: 24px;"
    />
    <div>
      <h2 style="margin: 0;">欢迎来到管理员后台</h2>
      <p style="color: #888; margin: 4px 0 0 0;">您好，管理员！感谢您的辛勤管理。</p>
    </div>
  </div>

  <el-divider />
  <div style="margin-bottom: 24px;">
    <h3>平台统计数据</h3>
    <div style="display: flex; gap: 32px;">
      <el-card style="width: 180px; text-align: center;">
        <div>用户总数</div>
        <div style="font-size: 2em; color: #409EFF;">1234</div>
      </el-card>
      <el-card style="width: 180px; text-align: center;">
        <div>商家总数</div>
        <div style="font-size: 2em; color: #67C23A;">56</div>
      </el-card>
      <el-card style="width: 180px; text-align: center;">
        <div>商品总数</div>
        <div style="font-size: 2em; color: #E6A23C;">789</div>
      </el-card>
      <el-card style="width: 180px; text-align: center;">
        <div>今日交易额</div>
        <div style="font-size: 2em; color: #F56C6C;">￥12345.67</div>
      </el-card>
    </div>
  </div>

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

  <el-divider />
  
</div>
      </div>
      <router-view v-else />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeMenu = ref('')

function handleMenuSelect(index) {
  activeMenu.value = index
  // 跳转到对应路由
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
    case 'pending-list':
      router.push('/admin/pending-list')
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