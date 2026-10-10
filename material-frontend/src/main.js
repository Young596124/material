import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import { getToken } from '@/utils/auth'

// 引入自定义全局组件
import Pagination from '@/components/Pagination'
import FileUpload from '@/components/FileUpload'

// Element UI 默认尺寸
Vue.use(ElementUI, { size: 'medium' })

// 注册全局组件
Vue.component('Pagination', Pagination)
Vue.component('FileUpload', FileUpload)

// 关闭生产环境提示
Vue.config.productionTip = false

// 全局前置路由守卫:处理登录态与动态路由
router.beforeEach(async (to, from, next) => {
  const hasToken = getToken()
  if (hasToken) {
    if (to.path === '/login') {
      // 已登录跳转登录页 → 重定向到首页
      next({ path: '/' })
    } else {
      // 是否已加载动态路由
      const hasRoutes = store.getters.hasRoutes
      if (!hasRoutes) {
        try {
          // 拉取用户信息
          await store.dispatch('user/getInfo')
          // 生成动态路由
          const accessRoutes = await store.dispatch('user/generateRoutes')
          router.addRoutes(accessRoutes)
          // 404 兜底路由必须最后注册,避免拦截动态路由
          router.addRoutes([{ path: '*', redirect: '/404' }])
          // 标记已加载,避免重复加载
          store.commit('user/SET_HAS_ROUTES', true)
          // 重新跳转,使 addRoutes 生效
          next({ ...to, replace: true })
        } catch (err) {
          // 拉取失败,清空 token 跳登录
          await store.dispatch('user/resetToken')
          next(`/login?redirect=${to.path}`)
        }
      } else {
        next()
      }
    }
  } else {
    // 未登录
    if (to.path === '/login') {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
