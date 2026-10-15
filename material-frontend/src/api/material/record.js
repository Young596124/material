import request from '@/utils/request'

// ============== 物料入库记录 ==============

// 查询物料入库记录列表
export function listInboundRecord(query) {
  return request({
    url: '/material/inboundRecord/list',
    method: 'get',
    params: query
  })
}

// 查询物料入库记录详细（主表 + 子表）
export function getInboundRecord(id) {
  return request({
    url: '/material/inboundRecord/' + id,
    method: 'get'
  })
}

// 查询入库记录子表明细（展开行获取子表）
export function getInboundRecordDetail(inboundId) {
  return request({
    url: '/material/inboundRecord/detail/' + inboundId,
    method: 'get'
  })
}

// 导出物料入库记录（主表一个工作表、子表一个工作表）
export function exportInboundRecord(query) {
  return request({
    url: '/material/inboundRecord/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// ============== 物料入库数据管理 ==============

// 查询物料入库数据列表
export function listInboundData(query) {
  return request({
    url: '/material/inboundData/list',
    method: 'get',
    params: query
  })
}

//导出物料入库管理数据
export function exportInboundData() {
  return request({
    url: '/material/inboundData/export',
    method: 'post',
    responseType: 'blob'
  })
}

// ============== 物料出库记录 ==============

// 查询物料出库记录列表
export function listOutboundRecord(query) {
  return request({
    url: '/material/outboundRecord/list',
    method: 'get',
    params: query
  })
}

// 查询物料出库记录详细（主表 + 子表）
export function getOutboundRecord(id) {
  return request({
    url: '/material/outboundRecord/' + id,
    method: 'get'
  })
}

// 查询出库记录子表明细（展开行获取子表）
export function getOutboundRecordDetail(outboundId) {
  return request({
    url: '/material/outboundRecord/detail/' + outboundId,
    method: 'get'
  })
}

// 导出物料出库记录（主表一个工作表、子表一个工作表）
export function exportOutboundRecord(query) {
  return request({
    url: '/material/outboundRecord/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// ============== 物料出库数据管理 ==============

// 查询物料出库数据列表
export function listOutboundData(query) {
  return request({
    url: '/material/outboundData/list',
    method: 'get',
    params: query
  })
}

//导出物料入库管理数据
export function exportOutboundData() {
  return request({
    url: '/material/outboundData/export',
    method: 'post',
    responseType: 'blob'
  })
}
