import request from '@/utils/request'

// 新增物料出库（出库编号 outboundCode 由后端自动生成，前端不传）
export function addOutbound(data) {
  return request({
    url: '/material/outbound',
    method: 'post',
    data: data
  })
}
