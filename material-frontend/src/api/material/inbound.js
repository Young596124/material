import request from '@/utils/request'

// 新增物料入库（入库编号 inboundCode 由后端自动生成，前端不传）
export function addInbound(data) {
  return request({
    url: '/material/inbound',
    method: 'post',
    data: data
  })
}
