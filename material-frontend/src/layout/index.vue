<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <sidebar class="sidebar-container" />
    <div class="main-container">
      <!-- 顶部导航 -->
      <navbar />
      <!-- 主体内容区 -->
      <app-main />
    </div>
  </div>
</template>

<script>
import Sidebar from './components/Sidebar/index'
import Navbar from './components/Navbar'
import AppMain from './components/AppMain'

export default {
  name: 'Layout',
  components: {
    Sidebar,
    Navbar,
    AppMain
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100%;
  width: 100%;
}
.sidebar-container {
  width: 210px;
  height: 100%;
  background: #304156;
  flex-shrink: 0;
}
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background: #f0f2f5;
}
</style>
