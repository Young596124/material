import request from '@/utils/request'

// 查询角色列表
export function listRole(query) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params: query
  })
}

// 查询全部角色(供用户管理下拉选择)
export function allRole() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
}

// 查询角色详情(含已分配菜单ID)
export function getRole(roleId) {
  return request({
    url: '/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data: data
  })
}

// 删除角色
export function delRole(roleIds) {
  return request({
    url: '/system/role/' + roleIds,
    method: 'delete'
  })
}

// 分配菜单
export function assignMenu(data) {
  return request({
    url: '/system/role/dataScope',
    method: 'put',
    data: data
  })
}
