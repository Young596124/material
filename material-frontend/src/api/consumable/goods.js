import request from '@/utils/request'

// 查询物品信息列表（分页）
export function listGoods(query) {
  return request({
    url: '/consumable/goods/list',
    method: 'get',
    params: query
  })
}

// 根据类别ID查询启用且显示的物品（供联动下拉）
export function listGoodsByCategory(categoryId) {
  return request({
    url: '/consumable/goods/listByCategory/' + categoryId,
    method: 'get'
  })
}

// 查询物品详情
export function getGoods(goodsId) {
  return request({
    url: '/consumable/goods/' + goodsId,
    method: 'get'
  })
}

// 新增物品
export function addGoods(data) {
  return request({
    url: '/consumable/goods',
    method: 'post',
    data
  })
}

// 修改物品
export function updateGoods(data) {
  return request({
    url: '/consumable/goods',
    method: 'put',
    data
  })
}

// 修改物品状态
export function changeGoodsStatus(data) {
  return request({
    url: '/consumable/goods/changeStatus',
    method: 'put',
    data
  })
}

// 删除物品（支持批量，逗号分隔）
export function delGoods(ids) {
  return request({
    url: '/consumable/goods/' + ids,
    method: 'delete'
  })
}
