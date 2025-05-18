// 这个文件已不再使用
console.warn('测试脚本已弃用')

// 模拟商品数据
const testProductData = {
  name: '测试商品-' + new Date().getTime(),
  category: [1, 6], // 电子产品-手机
  condition: 1, // 全新
  originalPrice: 5999,
  price: 4999,
  stock: 10,
  size: '标准版',
  isNegotiable: 1,
  description: '这是一个测试商品，用于测试商品发布功能。商品成色全新，未拆封，支持议价。'
}

// 图片URL
const imageUrls = [
  'https://tse1-mm.cn.bing.net/th/id/OIP-C.9tiYco-vjTydwrYrs7DbdQHaHa?cb=iwc2&rs=1&pid=ImgDetMain',
  'https://tse4-mm.cn.bing.net/th/id/OIP-C.whwdELpUmkiRns5uvHBdawHaHa?cb=iwc2&rs=1&pid=ImgDetMain'
]

// 将URL转换为File对象
async function urlToFile(url, filename) {
  try {
    const response = await fetch(url)
    const blob = await response.blob()
    return new File([blob], filename, { type: blob.type })
  } catch (error) {
    console.error('转换图片失败:', error)
    return null
  }
}

// 自动填充表单
async function fillProductForm() {
  try {
    // 获取表单实例
    const form = document.querySelector('.product-form')
    if (!form) {
      console.error('找不到商品表单')
      return
    }

    // 获取所有需要填充的输入元素
    const elements = {
      name: form.querySelector('input[placeholder="请输入商品名称"]'),
      category: form.querySelector('.el-cascader'),
      condition: form.querySelector('.el-select'),
      originalPrice: form.querySelector('input[placeholder="请输入商品原价"]'),
      price: form.querySelector('input[placeholder="请输入商品售价"]'),
      stock: form.querySelector('input[placeholder="请输入库存数量"]'),
      size: form.querySelector('input[placeholder="请输入商品尺寸"]'),
      description: form.querySelector('textarea'),
      negotiable: form.querySelector('.el-switch')
    }

    // 填充商品名称
    if (elements.name) {
      elements.name.value = testProductData.name
      elements.name.dispatchEvent(new Event('input'))
    }

    // 选择商品类目 - 更新方法
    if (elements.category) {
      // 尝试直接从全局访问Vue实例
      if (window.__VUE_DEVTOOLS_GLOBAL_HOOK__) {
        console.log('找到Vue开发者工具，尝试访问应用实例')
        
        // 尝试通过开发者工具获取根组件
        const appRecord = window.__VUE_DEVTOOLS_GLOBAL_HOOK__.getAppRecord?.()
        if (appRecord && appRecord.rootInstance) {
          // 找到应用根实例后，遍历找到PublishProduct组件
          const instance = appRecord.rootInstance
          console.log('找到根实例，设置类目值')
          
          // 在实例中查找productForm
          if (instance.ctx && instance.ctx.productForm) {
            instance.ctx.productForm.category = testProductData.category
            console.log('类目设置成功')
          }
        }
      }
      
      // 备用方法：通过直接操作DOM实现级联选择
      const cascaderInput = elements.category.querySelector('input')
      if (cascaderInput) {
        // 先模拟输入搜索
        cascaderInput.focus()
        cascaderInput.click()
        
        // 然后通过模拟点击项目来选择
        setTimeout(() => {
          // 打开菜单并选择第一级
          document.querySelectorAll('.el-cascader-node')[0]?.click()
          
          // 等待子菜单打开，然后选择第二级
          setTimeout(() => {
            const secondLevelNodes = document.querySelectorAll('.el-cascader-menu:nth-child(2) .el-cascader-node')
            if (secondLevelNodes && secondLevelNodes.length > 0) {
              // 选择手机选项
              secondLevelNodes[0]?.click()
              console.log('已完成级联选择器的选择')
            }
          }, 300)
        }, 300)
      }
    }

    // 选择商品成色
    if (elements.condition) {
      // 打开下拉选择器
      elements.condition.click()
      // 选择全新选项
      setTimeout(() => {
        const options = document.querySelectorAll('.el-select-dropdown__item')
        options[0]?.click()
      }, 300)
    }

    // 填充原价
    if (elements.originalPrice) {
      const inputNumber = elements.originalPrice.closest('.el-input-number')
      if (inputNumber) {
        // 找到输入框并设置值
        const input = inputNumber.querySelector('input')
        input.value = testProductData.originalPrice
        input.dispatchEvent(new Event('input'))
      }
    }

    // 填充售价
    if (elements.price) {
      const inputNumber = elements.price.closest('.el-input-number')
      if (inputNumber) {
        const input = inputNumber.querySelector('input')
        input.value = testProductData.price
        input.dispatchEvent(new Event('input'))
      }
    }

    // 填充库存
    if (elements.stock) {
      const inputNumber = elements.stock.closest('.el-input-number')
      if (inputNumber) {
        const input = inputNumber.querySelector('input')
        input.value = testProductData.stock
        input.dispatchEvent(new Event('input'))
      }
    }

    // 填充尺寸
    if (elements.size) {
      elements.size.value = testProductData.size
      elements.size.dispatchEvent(new Event('input'))
    }

    // 设置是否可议价
    if (elements.negotiable) {
      // 模拟点击切换开关
      elements.negotiable.click()
    }

    // 填充商品描述
    if (elements.description) {
      elements.description.value = testProductData.description
      elements.description.dispatchEvent(new Event('input'))
    }

    // 处理图片上传
    try {
      const files = await Promise.all(
        imageUrls.map((url, index) => urlToFile(url, `test${index + 1}.jpg`))
      )
      
      const validFiles = files.filter(file => file !== null)
      if (validFiles.length > 0) {
        const uploadInput = form.querySelector('.el-upload__input')
        if (uploadInput) {
          const dataTransfer = new DataTransfer()
          validFiles.forEach(file => dataTransfer.items.add(file))
          uploadInput.files = dataTransfer.files
          uploadInput.dispatchEvent(new Event('change'))
        }
      }
    } catch (error) {
      console.error('处理图片失败:', error)
    }

    console.log('表单填充完成')
  } catch (error) {
    console.error('填充表单时出错:', error)
  }
}

// 自动提交表单
function submitForm() {
  try {
    const submitButton = document.querySelector('button[type="primary"]')
    if (submitButton) {
      submitButton.click()
      console.log('表单已提交')
    }
  } catch (error) {
    console.error('提交表单时出错:', error)
  }
}

// 执行测试
async function runTest() {
  console.log('开始自动填充测试数据...')
  try {
    // 优先使用组件暴露的全局变量
    if (window.__PUBLISH_PRODUCT_COMPONENT__) {
      console.log('找到组件全局访问点，直接调用组件方法')
      // 异步调用填充方法
      await window.__PUBLISH_PRODUCT_COMPONENT__.fillWithTestData()
      
      // 延迟一秒后提交
      setTimeout(() => {
        window.__PUBLISH_PRODUCT_COMPONENT__.submit()
      }, 1000)
      return
    }
    
    // 备用方法：通过DOM操作
    console.log('未找到组件全局访问点，使用DOM操作填充表单')
    await fillProductForm()
    
    // 等待1秒后提交表单
    setTimeout(() => {
      submitForm()
    }, 1000)
  } catch (error) {
    console.error('执行测试时出错:', error)
  }
}

// 导出测试函数
export { runTest, testProductData, imageUrls } 