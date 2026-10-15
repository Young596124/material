<template>
  <div class="welcome-container">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <h1>欢迎使用C9无尘室线边仓物料系统</h1>
        <p class="subtitle">高效、便捷、可视化的物料入库出库管理平台</p>
        <el-row :gutter="20" class="quick-entry">
          <el-col :span="8">
            <div class="entry-item" @click="$router.push('/system/user')">
              <i class="el-icon-user"></i>
              <div>用户管理</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="entry-item" @click="$router.push('/system/menu')">
              <i class="el-icon-menu"></i>
              <div>菜单管理</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="entry-item" @click="$router.push('/system/dict')">
              <i class="el-icon-document"></i>
              <div>字典管理</div>
            </div>
          </el-col>
        </el-row>
        <div class="user-info">
          <span>当前登录用户:{{ nickname || name || '未知' }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Index',
  computed: {
    name() {
      return this.$store.getters.name
    },
    nickname() {
      return this.$store.getters.nickname
    }
  }
}
</script>

<style scoped>
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: calc(100vh - 140px);
}
.welcome-card {
  width: 100%;
}
.welcome-content {
  text-align: center;
  padding: 30px 20px;
}
.welcome-content h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 12px;
}
.subtitle {
  color: #909399;
  font-size: 14px;
  margin-bottom: 40px;
}
.quick-entry {
  margin-bottom: 40px;
}
.entry-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 24px 0;
  cursor: pointer;
  transition: all 0.3s;
}
.entry-item:hover {
  background: #ecf5ff;
  color: #409eff;
}
.entry-item i {
  font-size: 32px;
  color: #409eff;
}
.entry-item div {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}
.user-info {
  color: #909399;
  font-size: 14px;
}
</style>
