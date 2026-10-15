import request from '@/utils/request'

// 查询字典类型列表(分页)
export function listType(query) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params: query
  })
}

// 查询单个字典类型详情
export function getType(dictId) {
  return request({
    url: '/system/dict/type/' + dictId,
    method: 'get'
  })
}

// 新增字典类型
export function addType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

// 修改字典类型
export function updateType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

// 删除字典类型(支持批量,逗号分隔)
export function delType(dictIds) {
  return request({
    url: '/system/dict/type/' + dictIds,
    method: 'delete'
  })
}
