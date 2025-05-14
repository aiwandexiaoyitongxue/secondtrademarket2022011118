<template>
  <div>
    <el-card>
      <h2>待审核商品列表</h2>
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="name" label="商品名"/>
        <el-table-column prop="merchantId" label="商家ID"/>
        <el-table-column prop="price" label="价格"/>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <span v-if="scope.row.status === 0">待审核</span>
            <span v-else-if="scope.row.status === 1">在售</span>
            <span v-else-if="scope.row.status === 2">已下架</span>
            <span v-else-if="scope.row.status === 3">已售罄</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="success" size="small" @click="approve(scope.row.id)">通过</el-button>
            <el-button type="danger" size="small" @click="reject(scope.row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const products = ref([])

const fetchProducts = async () => {
  const res = await axios.get('/api/admin/products/pending', {
    headers: { Authorization: localStorage.getItem('token') }
  })
  products.value = res.data
}

onMounted(fetchProducts)

async function approve(id) {
  await axios.post(`/api/admin/products/${id}/approve`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('审核通过')
  fetchProducts()
}

async function reject(id) {
  await axios.post(`/api/admin/products/${id}/reject`, {}, {
    headers: { Authorization: localStorage.getItem('token') }
  })
  ElMessage.success('已驳回')
  fetchProducts()
}
</script>