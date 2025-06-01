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
            <span>待审核</span>
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
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const products = ref([])
console.log('products 初始值:', products.value)  // 看初始值
const fetchProducts = async () => {
  const res = await request.get('/admin/products/pending')
  console.log('接口返回:', res)  // 第一个日志
  console.log('-------------------')  // 分隔符
  products.value = res.data || []
  console.log('products.value:', products.value)  // 第二个日志
}
onMounted(fetchProducts)

async function approve(id) {
  await request.post(`/admin/products/${id}/audit`, {
    approved: true,
    reason: ''
  })
  ElMessage.success('审核通过')
  fetchProducts()
}

async function reject(id) {
  await request.post(`/admin/products/${id}/audit`, {
    approved: false,
    reason: '不符合要求'
  })
  ElMessage.success('已驳回')
  fetchProducts()
}
</script>