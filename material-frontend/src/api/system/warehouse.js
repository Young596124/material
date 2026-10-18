import request from '@/utils/request'

// 查询仓库列表（分页）
export function listWarehouse(query) {
  return request({
    url: '/system/warehouse/list',
    method: 'get',
    params: query
  })
}

// 查询仓库详情
export function getWarehouse(id) {
  return request({
    url: '/system/warehouse/' + id,
    method: 'get'
  })
}

// 查询全部启用的仓库（供下拉选择）
export function selectEnabledWarehouse() {
  return request({
    url: '/system/warehouse/selectEnabled',
    method: 'get'
  })
}

// 新增仓库
export function addWarehouse(data) {
  return request({
    url: '/system/warehouse',
    method: 'post',
    data
  })
}

// 修改仓库
export function updateWarehouse(data) {
  return request({
    url: '/system/warehouse',
    method: 'put',
    data
  })
}

// 删除仓库（支持批量，逗号分隔）
export function delWarehouse(ids) {
  return request({
    url: '/system/warehouse/' + ids,
    method: 'delete'
  })
}
