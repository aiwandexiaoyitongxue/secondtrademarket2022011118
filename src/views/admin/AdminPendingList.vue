<template>
  <el-card>
    <h2>待审核信息列表</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="商品" name="product">
        <el-table :data="pendingProducts" style="width: 100%">
          <el-table-column prop="id" label="商品ID" width="100"/>
          <el-table-column prop="name" label="商品名称"/>
          <el-table-column prop="merchantId" label="商家ID"/>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="primary" size="small" @click="() => goToProductAudit(scope.row)">去审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="用户" name="user">
        <el-table :data="pendingUsers" style="width: 100%">
          <el-table-column prop="id" label="用户ID" width="100"/>
          <el-table-column prop="username" label="用户名"/>
          <el-table-column prop="phone" label="手机号"/>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="primary" size="small" @click="() => goToUserAudit(scope.row)">去审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="商家" name="merchant">
        <el-table :data="pendingMerchants" style="width: 100%">
          <el-table-column prop="id" label="商家ID" width="100"/>
          <el-table-column prop="userId" label="用户ID"/>
          <el-table-column prop="businessLicense" label="营业执照"/>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="primary" size="small" @click="() => goToMerchantAudit(scope.row)">去审核</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('product')
const pendingProducts = ref([])
const pendingUsers = ref([])
const pendingMerchants = ref([])

const fetchPending = async () => {
  const [proRes, userRes, merchantRes] = await Promise.all([
    request.get('/admin/products/pending'),
    request.get('/admin/users/pending'),
    request.get('/admin/merchants/pending')
  ])
  pendingProducts.value = proRes
  pendingUsers.value = userRes
  pendingMerchants.value = merchantRes
}

// 添加这三个函数
const goToProductAudit = (row) => {
  router.push({
    path: '/admin/product-audit',
    query: { id: row.id }
  })
}

const goToUserAudit = (row) => {
  router.push({
    path: '/admin/user-audit',
    query: { id: row.id }
  })
}

const goToMerchantAudit = (row) => {
  router.push({
    path: '/admin/merchant-audit',
    query: { id: row.id }
  })
}
onMounted(fetchPending)
</script>