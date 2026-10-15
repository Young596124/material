import { login, getInfo, getRouters, logout } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { resetRouter } from '@/router'
import Layout from '@/layout/index'

// Layout 特殊处理标识,用于递归转换路由 component 字符串
function filterAsyncRoutes(routes) {
  const res = []
  routes.forEach(route => {
    const tmp = { ...route }
    if (tmp.component) {
      if (tmp.component === 'Layout') {
        tmp.component = Layout
      } else {
        // 字符串 → 动态 import,加在 @/views 下
        const componentPath = tmp.component
        tmp.component = () => import('@/views/' + componentPath + '.vue')
      }
    }
    // 递归处理子路由
    if (tmp.children && tmp.children.length) {
      tmp.children = filterAsyncRoutes(tmp.children)
    }
    res.push(tmp)
  })
  return res
}

const state = {
  token: getToken(),
  userId: null,
  name: '',
  nickname: '',
  avatar: '',
  // 是否已加载动态路由
  hasRoutes: false,
  // 动态路由(供侧边栏渲染)
  routes: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER: (state, info) => {
    state.userId = info.userId
    state.name = info.userName
    state.nickname = info.nickName
    state.avatar = info.avatar || ''
  },
  SET_HAS_ROUTES: (state, flag) => {
    state.hasRoutes = flag
  },
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  },
  RESET_STATE: (state) => {
    state.userId = null
    state.name = ''
    state.nickname = ''
    state.avatar = ''
    state.hasRoutes = false
    state.routes = []
  }
}

const actions = {
  // 登录
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password }).then(res => {
        commit('SET_TOKEN', res.token)
        setToken(res.token)
        resolve()
      }).catch(err => {
        reject(err)
      })
    })
  },

  // 拉取用户信息
  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getInfo().then(res => {
        const data = res.data || res
        if (!data) {
          commit('SET_HAS_ROUTES', false)
          reject('验证失败,请重新登录')
          return
        }
        commit('SET_USER', data)
        resolve(data)
      }).catch(err => {
        reject(err)
      })
    })
  },

  // 生成动态路由
  generateRoutes({ commit }) {
    return new Promise((resolve, reject) => {
      getRouters().then(res => {
        const routes = filterAsyncRoutes(res.data || [])
        commit('SET_ROUTES', routes)
        resolve(routes)
      }).catch(err => {
        reject(err)
      })
    })
  },

  // 退出登录
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      logout().then(() => {
        commit('SET_TOKEN', '')
        commit('RESET_STATE')
        removeToken()
        resetRouter()
        resolve()
      }).catch(() => {
        // 即便接口失败也清空前端状态
        commit('SET_TOKEN', '')
        commit('RESET_STATE')
        removeToken()
        resetRouter()
        resolve()
      })
    })
  },

  // 重置 token(无接口调用)
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('RESET_STATE')
      removeToken()
      resetRouter()
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
