import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'
import { getToken, removeToken } from '@/utils/auth'

// 1. 定义一个全局的标志位，防止并发 401 重复弹窗和跳转
let isLoggingOut = false;
// 创建 axios 实例
const service = axios.create({
  // 基础路径:dev 走代理 /api,prod 用 /dev-api
  baseURL: process.env.VUE_APP_BASE_API,
  // 请求超时时间
  timeout: 10000,
  // 不携带跨域 Cookie
  withCredentials: false
})

// 2. 提取统一的处理 401 的方法（放在外面，方便两处调用）
const handle401 = (msg) => {
    // 如果已经在退出流程中了，直接返回，阻止后续的弹窗和跳转
    if (isLoggingOut) return;

    // 上锁
    isLoggingOut = true;

    Message.error(msg || '登录状态已过期,请重新登录');
    removeToken();

    // 跳转登录页
    router.push('/login').finally(() => {
        // 3. 跳转完成后（或延迟一小段时间后），解锁。
        // 这样如果用户重新登录，后续再次过期还能正常触发逻辑
        setTimeout(() => {
            isLoggingOut = false;
        }, 1000); // 1秒后解锁
    });
};
// 请求拦截器
service.interceptors.request.use(
  config => {
    // 给每个请求 Header 加上 Authorization
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 请求出错处理
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 文件下载（blob）直接返回原始数据
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    // code 为 200 表示请求成功
    if (res.code !== 200) {
      // 401:未登录或 token 失效
      if (res.code === 401) {
        handle401(res.msg);
        return Promise.reject(new Error(res.msg || '未授权'))
      }
      // 其他错误统一提示
      Message.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    // 成功返回数据体
    return res
  },
  error => {
    // HTTP 层面错误
    let message = error.message
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
          // 替换为调用统一的处理函数
          handle401('登录状态已过期,请重新登录');
          // 这里直接返回 error，不再走到下面弹 Message.error(message)，避免重复弹窗
          return Promise.reject(error);
      } else if (status === 404) {
        message = '请求资源不存在'
      } else if (status === 500) {
        message = '服务器内部错误'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时,请稍后重试'
    }
    Message.error(message)
    return Promise.reject(error)
  }
)

export default service
