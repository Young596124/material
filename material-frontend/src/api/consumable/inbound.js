import request from '@/utils/request'

// 查询物品入库列表（分页）
export function listInbound(query) {
  return request({
    url: '/consumable/inbound/list',
    method: 'get',
    params: query
  })
}

// 查询入库详情
export function getInbound(id) {
  return request({
    url: '/consumable/inbound/' + id,
    method: 'get'
  })
}

// 新增入库
export function addInbound(data) {
  return request({
    url: '/consumable/inbound',
    method: 'post',
    data
  })
}

// 导出入库记录
export function exportInbound(query) {
  return request({
    url: '/consumable/inbound/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
