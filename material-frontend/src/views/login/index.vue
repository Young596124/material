<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>C9无尘室线边仓物料系统</h2>
        <p>Material In/Out Management System</p>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            show-password
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
        <div class="login-tip">默认账号:admin / admin123</div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    // 用户名校验
    const validateUsername = (rule, value, callback) => {
      if (!value || value.trim().length === 0) {
        callback(new Error('请输入用户名'))
      } else {
        callback()
      }
    }
    // 密码校验
    const validatePassword = (rule, value, callback) => {
      if (!value || value.length === 0) {
        callback(new Error('请输入密码'))
      } else if (value.length < 5) {
        callback(new Error('密码长度不能少于 5 位'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123'
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      // 重定向地址
      redirect: undefined
    }
  },
  watch: {
    $route: {
      immediate: true,
      handler(route) {
        this.redirect = route.query && route.query.redirect
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        this.$store
          .dispatch('user/login', this.loginForm)
          .then(() => {
            this.$message.success('登录成功')
            // 重置 hasRoutes 标志,使守卫重新加载路由
            this.$store.commit('user/SET_HAS_ROUTES', false)
            this.$router.push(this.redirect || '/').catch(() => {})
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.login-header h2 {
  margin: 0 0 8px;
  font-size: 26px;
  color: #303133;
}
.login-header p {
  margin: 0;
  font-size: 12px;
  color: #c0c4cc;
  letter-spacing: 1px;
}
.login-form {
  margin-top: 20px;
}
.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 4px;
}
.login-tip {
  text-align: center;
  margin-top: 10px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
