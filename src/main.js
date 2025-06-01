import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import { CanvasRenderer } from 'echarts/renderers'
import * as echarts from 'echarts/core'
import VueECharts from 'vue-echarts'

// 注册所有图标
const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)

echarts.use([CanvasRenderer])
app.component('v-chart', VueECharts)

app.mount('#app')