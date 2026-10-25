import request from '@/utils/request'

// 查询物品类别列表（分页）
export function listCategory(query) {
  return request({
    url: '/consumable/category/list',
    method: 'get',
    params: query
  })
}

// 查询全部启用的类别（供下拉选择）
export function selectEnabledCategory() {
  return request({
    url: '/consumable/category/selectEnabled',
    method: 'get'
  })
}

// 查询类别详情
export function getCategory(categoryId) {
  return request({
    url: '/consumable/category/' + categoryId,
    method: 'get'
  })
}

// 新增类别
export function addCategory(data) {
  return request({
    url: '/consumable/category',
    method: 'post',
    data
  })
}

// 修改类别
export function updateCategory(data) {
  return request({
    url: '/consumable/category',
    method: 'put',
    data
  })
}

// 修改类别状态
export function changeCategoryStatus(data) {
  return request({
    url: '/consumable/category/changeStatus',
    method: 'put',
    data
  })
}

// 删除类别（支持批量，逗号分隔）
export function delCategory(ids) {
  return request({
    url: '/consumable/category/' + ids,
    method: 'delete'
  })
}
