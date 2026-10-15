import request from '@/utils/request'

// 查询1F线边仓物料基表列表
export function listWarehouseBase(query) {
  return request({
    url: '/material/warehouseBase/list',
    method: 'get',
    params: query
  })
}

// 查询1F线边仓物料基表详细
export function getWarehouseBase(id) {
  return request({
    url: '/material/warehouseBase/' + id,
    method: 'get'
  })
}

// 修改物料基表
export function updateWarehouseBase(data) {
  return request({
    url: '/material/warehouseBase',
    method: 'put',
    data: data
  })
}

// 查询所有基表物料（供入库/出库选物料）
export function selectAllWarehouseBase() {
  return request({
    url: '/material/warehouseBase/selectAll',
    method: 'get'
  })
}

// 按物料名称查询基表物料
export function selectByName(materialName) {
  return request({
    url: '/material/warehouseBase/selectByName',
    method: 'get',
    params: { materialName }
  })
}

// 导出物料基表 Excel
export function exportWarehouseBase() {
  return request({
    url: '/material/warehouseBase/export',
    method: 'post',
    responseType: 'blob'
  })
}

// 导入物料基表 Excel
export function importWarehouseBase(data) {
  return request({
    url: '/material/warehouseBase/import',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 下载导入模板
export function importTemplateWarehouseBase() {
  return request({
    url: '/material/warehouseBase/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}

