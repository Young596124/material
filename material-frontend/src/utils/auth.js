/* 全局表格表头样式 */
.el-table th.el-table__cell {
  background-color: #f5f7fa !important;   /* 表头背景 */
  color: #303133 !important;              /* 表头文字颜色 */
  font-weight: 600;                       /* 加粗 */
}

/* 去掉表头 hover 变白的问题（Element 默认有 hover 样式） */
.el-table th.el-table__cell:hover {
  background-color: #f5f7fa !important;
}

/* 固定列的表头也要一致（右侧/左侧固定列） */
.el-table__fixed-header-wrapper th.el-table__cell,
.el-table__fixed-right-header-wrapper th.el-table__cell {
  background-color: #f5f7fa !important;
  color: #303133 !important;
}

/* 如果表格有边框，避免表头边框颜色不协调 */
.el-table--border th.el-table__cell {
  border-bottom: 1px solid #ebeef5;
}

.el-table__fixed::before,
.el-table__fixed-right::before {
  display: none;  /* 去掉固定列默认的阴影线，避免和边框冲突 */
}
