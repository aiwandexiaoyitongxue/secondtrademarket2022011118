<template>
  <div>
    <h3>收货地址</h3>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 16px;">新增地址</el-button>
    <el-table :data="addresses" style="width: 100%;" v-loading="loading">
      <el-table-column prop="receiverName" label="收件人" width="120" />
      <el-table-column prop="receiverPhone" label="手机号" width="140" />
      <el-table-column label="地址" min-width="300">
        <template #default="{ row }">
          {{ row.province }} {{ row.city }} {{ row.district }} {{ row.detail }}
        </template>
      </el-table-column>
      <el-table-column prop="isDefault" label="默认" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="editAddress(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteAddress(row.id)">删除</el-button>
          <el-button size="small" type="success" v-if="row.isDefault !== 1" @click="setDefault(row.id)">设为默认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑地址弹窗 -->
    <el-dialog :title="editId ? '编辑地址' : '新增地址'" v-model="showAddDialog" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="收件人" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  userId: Number
})

const addresses = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const editId = ref(null)
const form = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})
const formRef = ref(null)
const rules = {
  receiverName: [{ required: true, message: '请输入收件人', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/addresses')
    if (res.success) {
      addresses.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取地址失败')
    }
  } catch (e) {
    ElMessage.error('获取地址失败')
  } finally {
    loading.value = false
  }
}

const submitForm = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (editId.value) {
        // 编辑
        await request.post('/user/address/update', { id: editId.value, ...form.value })
        ElMessage.success('修改成功')
      } else {
        // 新增
        await request.post('/user/address/add', form.value)
        ElMessage.success('新增成功')
      }
      showAddDialog.value = false
      fetchAddresses()
      resetForm()
    } catch (e) {
      ElMessage.error('操作失败')
    }
  })
}

const editAddress = (row) => {
  editId.value = row.id
  form.value = {
    receiverName: row.receiverName,
    receiverPhone: row.receiverPhone,
    province: row.province,
    city: row.city,
    district: row.district,
    detail: row.detail
  }
  showAddDialog.value = true
}

const deleteAddress = async (id) => {
  try {
    await request.post('/user/address/delete', { id })
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const setDefault = async (id) => {
  try {
    await request.post('/user/address/setDefault', { id })
    ElMessage.success('设置成功')
    fetchAddresses()
  } catch (e) {
    ElMessage.error('设置默认失败')
  }
}

const resetForm = () => {
  editId.value = null
  form.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detail: ''
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>