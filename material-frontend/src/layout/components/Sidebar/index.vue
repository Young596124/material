<template>
  <div class="sidebar">
    <!-- Logo / 系统名称 -->
    <div class="logo">
      <span>物料管理系统</span>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        router
      >
        <sidebar-item
          v-for="route in menuRoutes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import SidebarItem from './SidebarItem'

export default {
  name: 'Sidebar',
  components: { SidebarItem },
  computed: {
    // 当前激活菜单
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta && meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    // 渲染侧边栏的路由列表(过滤隐藏项)
    menuRoutes() {
      return this.$router.options.routes.concat(this.$store.getters.dynamicRoutes || []).filter(route => !route.hidden)
    }
  }
}
</script>

<style scoped>
.sidebar {
 /* 隐藏横向滚动条 */
  overflow-x: hidden !important;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  background: #2b2f3a;
}
</style>
