import request from '@/utils/request'

// 查询库房列表（分页）
export function listStorehouse(query) {
  return request({
    url: '/consumable/storehouse/list',
    method: 'get',
    params: query
  })
}

// 查询全部启用的库房（供弹框选择）
export function selectEnabledStorehouse() {
  return request({
    url: '/consumable/storehouse/selectEnabled',
    method: 'get'
  })
}

// 查询库房详情
export function getStorehouse(storehouseId) {
  return request({
    url: '/consumable/storehouse/' + storehouseId,
    method: 'get'
  })
}

// 新增库房
export function addStorehouse(data) {
  return request({
    url: '/consumable/storehouse',
    method: 'post',
    data
  })
}

// 修改库房
export function updateStorehouse(data) {
  return request({
    url: '/consumable/storehouse',
    method: 'put',
    data
  })
}

// 修改库房状态
export function changeStorehouseStatus(data) {
  return request({
    url: '/consumable/storehouse/changeStatus',
    method: 'put',
    data
  })
}

// 删除库房（支持批量，逗号分隔）
export function delStorehouse(ids) {
  return request({
    url: '/consumable/storehouse/' + ids,
    method: 'delete'
  })
}
