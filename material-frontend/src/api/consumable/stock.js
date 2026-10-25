import request from '@/utils/request'

// 查询库存信息列表（分页）
export function listStock(query) {
  return request({
    url: '/consumable/stock/list',
    method: 'get',
    params: query
  })
}

// 导出库存信息
export function exportStock(query) {
  return request({
    url: '/consumable/stock/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
