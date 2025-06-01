<template>
  <div class="seller-home-container">
    <el-row :gutter="24">
      <!-- 商家信息卡片 -->
      <el-col :span="8">
        <el-card class="info-card">
          <div class="info-header">
            <div class="icon-list">
              <el-icon class="cute-icon"><UserFilled /></el-icon>
              <el-icon class="cute-icon"><WalletFilled /></el-icon>
              <el-icon class="cute-icon"><Medal /></el-icon>
            </div>
            <div class="merchant-basic">
              <div class="merchant-id">商家ID：<b>{{ merchantInfo.id }}</b></div>
              <div class="merchant-level">等级：<span class="level-badge">Lv.{{ merchantInfo.level }}</span></div>
              <div class="merchant-sales">总销售额：<span class="sales-amount">¥{{ merchantInfo.totalSales }}</span></div>
            </div>
          </div>
          <div class="merchant-tags">
            <span class="tag-title">商家标签：</span>
            <el-tag
              v-for="tag in topCategories"
              :key="tag.name"
              type="success"
              class="category-tag"
              effect="dark"
            >
              {{ tag.name }}
            </el-tag>
          </div>
          <!-- 服务评分平均分 -->
          <div class="score-section left-score" style="margin-top: 18px;">
            <div class="score-label">服务评分平均分</div>
            <el-rate :model-value="avgServiceRating" disabled show-score />
          </div>
        </el-card>
      </el-col>
      <!-- 评分与扇形图 -->
      <el-col :span="16">
        <el-card class="chart-card">
          <div class="pie-section">
            <div class="pie-title">用户购买最多的商品类型</div>
            <v-chart :option="pieOption" autoresize style="height: 260px; width: 320px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 商品评价表 -->
    <el-card class="review-table-card">
      <div class="table-title">商家所有商品评价</div>
      <el-table :data="productReviews" border stripe style="margin-top: 10px;">
        <el-table-column prop="productName" label="商品名称" width="180" />
        <el-table-column prop="username" label="买家" width="120" />
        <el-table-column prop="serviceRating" label="服务评分">
          <template #default="{ row }">
            <el-rate :model-value="row.serviceRating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" />
        <el-table-column prop="createdTime" label="评价时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { UserFilled, WalletFilled, Medal } from '@element-plus/icons-vue'

// 注册必要的组件
use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const merchantInfo = ref({
  id: '',
  level: '',
  totalSales: 0,
  businessLicense: '',
})
const productReviews = ref([])
const avgServiceRating = ref(0)
const topCategories = ref([])
const pieOption = ref({})

function formatTime(time) {
  if (!time) return '--'
  return new Date(time).toLocaleString()
}

const fetchMerchantInfo = async () => {
  try {
    const res = await request.get('/merchant/info')
    if (res.success) {
      merchantInfo.value = res.data
    }
  } catch (e) {
    ElMessage.error('获取商家信息失败')
  }
}

const fetchProductReviews = async () => {
  try {
    const res = await request.get('/user/review/list-merchant-reviews')
    if (res.success) {
      productReviews.value = res.data
      // 计算服务评分平均分
      if (productReviews.value.length > 0) {
        const sum = productReviews.value.reduce((acc, cur) => acc + (cur.serviceRating || 0), 0)
        avgServiceRating.value = sum / productReviews.value.length
      } else {
        avgServiceRating.value = 0
      }
    }
  } catch (e) {
    ElMessage.error('获取商品评价失败')
  }
}

const fetchCategoryStats = async () => {
  try {
    const res = await request.get('/merchant/product/category-stats')
    if (res.success) {
      // 取前三个分类作为标签
      topCategories.value = res.data.slice(0, 3)
      // 扇形图数据
      pieOption.value = {
        tooltip: { trigger: 'item' },
        legend: { top: 'bottom' },
        series: [
          {
            name: '商品类型',
            type: 'pie',
            radius: ['50%', '85%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            label: { show: true, position: 'outside', fontSize: 14 },
            emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
            data: res.data.map(item => ({ value: item.count, name: item.name })),
          },
        ],
      }
    }
  } catch (e) {
    ElMessage.error('获取商品分类统计失败')
  }
}

onMounted(() => {
  fetchMerchantInfo()
  fetchProductReviews()
  fetchCategoryStats()
})
</script>

<style scoped>
.seller-home-container {
  padding: 32px 24px;
  background: #f5f7fa;
}
.info-card {
  margin-bottom: 24px;
  background: linear-gradient(135deg, #e0e7ff 0%, #f0fdfa 100%);
  border-radius: 12px;
  box-shadow: 0 2px 12px #e0e7ff44;
}
.info-header {
  display: flex;
  align-items: center;
  gap: 18px;
}
.icon-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 16px;
}
.cute-icon {
  font-size: 32px;
  margin-bottom: 8px;
  color: #f59e42;
}
.merchant-basic {
  flex: 1;
}
.merchant-id {
  font-size: 18px;
  color: #6366f1;
  font-weight: bold;
}
.merchant-level {
  font-size: 20px;
  color: #f59e42;
  margin-top: 6px;
}
.level-badge {
  background: #f59e42;
  color: #fff;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 20px;
  font-weight: bold;
}
.merchant-sales {
  font-size: 22px;
  color: #10b981;
  margin-top: 8px;
  font-weight: bold;
}
.merchant-tags {
  margin-top: 18px;
}
.tag-title {
  font-size: 16px;
  color: #64748b;
  margin-right: 8px;
}
.category-tag {
  margin-right: 8px;
  font-size: 15px;
  padding: 4px 14px;
  border-radius: 16px;
  background: #e0f2fe;
  color: #0284c7;
  border: none;
}
.chart-card {
  min-height: 320px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px #e0e7ff44;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
}
.pie-section {
  flex: 1.5;
  padding-left: 40px;
}
.pie-title {
  font-size: 18px;
  color: #0284c7;
  margin-bottom: 10px;
}
.review-table-card {
  margin-top: 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px #e0e7ff44;
  padding: 24px;
}
.table-title {
  font-size: 20px;
  color: #6366f1;
  font-weight: bold;
  margin-bottom: 10px;
}
.left-score {
  text-align: left;
  padding-left: 10px;
}
</style> 