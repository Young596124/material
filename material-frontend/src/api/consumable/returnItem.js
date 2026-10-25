import request from '@/utils/request'

// 查询物品退库列表（分页）
export function listReturn(query) {
  return request({
    url: '/consumable/return/list',
    method: 'get',
    params: query
  })
}

// 查询退库详情
export function getReturn(id) {
  return request({
    url: '/consumable/return/' + id,
    method: 'get'
  })
}

// 新增退库
export function addReturn(data) {
  return request({
    url: '/consumable/return',
    method: 'post',
    data
  })
}

// 导出退库记录
export function exportReturn(query) {
  return request({
    url: '/consumable/return/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
