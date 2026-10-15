import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user
  },
  getters: {
    hasRoutes: state => state.user.hasRoutes,
    dynamicRoutes: state => state.user.routes,
    userId: state => state.user.userId,
    name: state => state.user.name,
    nickname: state => state.user.nickname,
    avatar: state => state.user.avatar
  }
})
