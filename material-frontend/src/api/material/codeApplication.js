import request from '@/utils/request'

// 查询物料编码申请列表
export function listCodeApplication(query) {
  return request({
    url: '/material/codeApplication/list',
    method: 'get',
    params: query
  })
}

// 查询物料编码申请详细
export function getCodeApplication(id) {
  return request({
    url: '/material/codeApplication/' + id,
    method: 'get'
  })
}

// 新增物料编码申请（物料编码 materialCode 由后端自动生成，前端不传）
export function addCodeApplication(data) {
  return request({
    url: '/material/codeApplication',
    method: 'post',
    data: data
  })
}

// 修改物料编码申请
export function updateCodeApplication(data) {
  return request({
    url: '/material/codeApplication',
    method: 'put',
    data: data
  })
}

// 删除物料编码申请（ids 逗号分隔）
export function delCodeApplication(ids) {
  return request({
    url: '/material/codeApplication/' + ids,
    method: 'delete'
  })
}
