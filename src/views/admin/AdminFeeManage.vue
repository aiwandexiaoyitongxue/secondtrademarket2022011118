<template>
  <el-card>
    <h2>平台交易费率管理</h2>
    <el-table :data="rates" style="width: 100%">
      <el-table-column prop="level" label="商家等级" width="100"/>
      <el-table-column prop="rate" label="费率" width="120">
        <template #default="scope">
          <el-input v-model="scope.row.rate" size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"/>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="primary" size="small" @click="updateRate(scope.row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const rates = ref([])

// 添加获取费率列表的函数
const fetchRates = async () => {
  const res = await request.get('/api/admin/merchant-level-rate')
  rates.value = res
}

const updateRate = async (row) => {
  try {
    // 验证输入
    if (!row.level || row.level < 1 || row.level > 5) {
      ElMessage.error('商家等级必须在1-5之间')
      return
    }
    
    const rate = parseFloat(row.rate)
    if (isNaN(rate) || rate < 0 || rate > 1) {
      ElMessage.error('费率必须在0-1之间')
      return
    }

    // 验证费率是否随等级递增
    const currentLevel = row.level
    const currentRate = rate
    const otherRates = rates.value.filter(r => r.level !== currentLevel)
    
    // 检查是否违反费率递增规则
    const lowerLevels = otherRates.filter(r => r.level < currentLevel)
    const higherLevels = otherRates.filter(r => r.level > currentLevel)
    
    const hasLowerRate = lowerLevels.some(r => parseFloat(r.rate) >= currentRate)
    const hasHigherRate = higherLevels.some(r => parseFloat(r.rate) <= currentRate)
    
    if (hasLowerRate) {
      ElMessage.error(`费率必须大于低等级商家的费率`)
      return
    }
    
    if (hasHigherRate) {
      ElMessage.error(`费率必须小于高等级商家的费率`)
      return
    }

    console.log('Updating rate with data:', row)
    const response = await request.post('/api/admin/merchant-level-rate/update', {
      level: row.level,
      rate: rate,
      description: row.description
    })
    console.log('Update response:', response)
    ElMessage.success('保存成功')
    await fetchRates()  // 更新后重新获取列表
  } catch (error) {
    console.error('更新费率失败:', error)
    ElMessage.error(error.response?.data?.message || '更新失败，请稍后重试')
  }
}

// 添加输入验证
const validateRate = (rule, value, callback) => {
  const rate = parseFloat(value)
  if (isNaN(rate) || rate < 0 || rate > 1) {
    callback(new Error('费率必须在0-1之间'))
  } else {
    callback()
  }
}

// 添加表单验证规则
const rules = {
  rate: [
    { required: true, message: '请输入费率', trigger: 'blur' },
    { validator: validateRate, trigger: 'blur' }
  ]
}

onMounted(fetchRates)  // 页面加载时获取费率列表
</script>