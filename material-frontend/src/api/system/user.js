import request from '@/utils/request'

// 查询用户列表(分页)
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详情
export function getUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

// 删除用户(支持批量,逗号分隔)
export function delUser(userIds) {
  return request({
    url: '/system/user/' + userIds,
    method: 'delete'
  })
}
