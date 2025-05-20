<template>
  <div class="comment-container">
    <el-card class="score-summary">
      <h3>评分汇总</h3>
      <div class="score-info">
        <span>平均分：{{ avgScore }}</span>
        <span>评价总数：{{ totalCount }}</span>
      </div>
    </el-card>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="未回复" name="unreplied">
        <el-table :data="unrepliedComments">
          <el-table-column prop="orderNo" label="订单编号" />
          <el-table-column prop="buyerName" label="买家" />
          <el-table-column prop="score" label="评分" />
          <el-table-column prop="content" label="评价内容" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="replyComment(scope.row)">回复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已回复" name="replied">
        <el-table :data="repliedComments">
          <el-table-column prop="orderNo" label="订单编号" />
          <el-table-column prop="buyerName" label="买家" />
          <el-table-column prop="score" label="评分" />
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="reply" label="我的回复" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-dialog v-model="replyDialogVisible" title="回复评价" width="400px">
      <el-input v-model="replyContent" type="textarea" rows="4" placeholder="请输入回复内容" />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const activeTab = ref('unreplied')
const replyDialogVisible = ref(false)
const replyContent = ref('')
const unrepliedComments = ref([])
const repliedComments = ref([])
const avgScore = ref(0)
const totalCount = ref(0)
const replyComment = (row) => {
  replyDialogVisible.value = true
  replyContent.value = ''
  // 记录当前回复对象
  // ...
}
const submitReply = () => {
  // 提交回复逻辑
  replyDialogVisible.value = false
}
</script>
<style scoped>
.comment-container { padding: 20px; }
.score-summary { margin-bottom: 20px; }
.score-info { display: flex; gap: 30px; font-size: 16px; }
</style> 