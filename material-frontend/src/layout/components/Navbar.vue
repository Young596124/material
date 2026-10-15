<template>
  <div class="navbar">
    <div class="left">
      <span class="title">C9无尘室线边仓物料系统</span>
    </div>
    <div class="right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <i class="el-icon-user-solid"></i>
          {{ nickname || name || '用户' }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Navbar',
  computed: {
    name() {
      return this.$store.getters.name
    },
    nickname() {
      return this.$store.getters.nickname
    }
  },
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await this.$confirm('确定要退出登录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
          await this.$store.dispatch('user/logout')
          this.$message.success('已退出登录')
          this.$router.push('/login')
        } catch (e) {
          // 取消则不处理
        }
      }
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}
.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #5a5e66;
  font-size: 14px;
}
</style>
