'use strict'
const path = require('path')

// Vue CLI 配置
module.exports = {
  // 部署应用包时的基本 URL
  publicPath: './',
  // 生产环境构建输出目录
  outputDir: 'dist',
  // 静态资源目录
  assetsDir: 'static',
  // 关闭 eslint 校验
  lintOnSave: false,
  // 生产环境 sourceMap
  productionSourceMap: false,
  // 配置 webpack-dev-server 行为
  devServer: {
    // 端口
    port: 80,
    // 自动打开浏览器
    open: true,
    // 代理配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
    // 错误覆盖到浏览器
    overlay: {
      warnings: false,
      errors: true
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    }
  }
}
