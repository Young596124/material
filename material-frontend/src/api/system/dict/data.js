import request from '@/utils/request'

// 查询字典数据列表(分页)
export function listData(query) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params: query
  })
}

// 根据字典类型获取字典数据选项(用于下拉/标签)
export function getDicts(dictType) {
  return request({
    url: '/system/dict/data/type/' + dictType,
    method: 'get'
  })
}

// 查询单条字典数据详情
export function getData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'get'
  })
}

// 新增字典数据
export function addData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

// 修改字典数据
export function updateData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

// 删除字典数据(支持批量)
export function delData(dictCodes) {
  return request({
    url: '/system/dict/data/' + dictCodes,
    method: 'delete'
  })
}
