import request from '@/utils/request'

// 查询物品领用列表（分页）
export function listPick(query) {
  return request({
    url: '/consumable/pick/list',
    method: 'get',
    params: query
  })
}

// 查询领用详情
export function getPick(id) {
  return request({
    url: '/consumable/pick/' + id,
    method: 'get'
  })
}

// 查询可退库的领用记录（含可退数量）
export function listReturnablePicks() {
  return request({
    url: '/consumable/pick/returnableList',
    method: 'get'
  })
}

// 新增领用
export function addPick(data) {
  return request({
    url: '/consumable/pick',
    method: 'post',
    data
  })
}

// 导出领用记录
export function exportPick(query) {
  return request({
    url: '/consumable/pick/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
