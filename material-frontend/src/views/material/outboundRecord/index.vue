<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="出库编号" prop="outboundCode">
        <el-input v-model="queryParams.outboundCode" placeholder="请输入出库编号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作区 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 列表（展开行显示子表） -->
    <el-table v-loading="loading" :data="dataList" @expand-change="handleExpandChange">
      <el-table-column type="expand">
        <template slot-scope="scope">
          <div class="sub-table-wrap" v-loading="scope.row._loading">
            <el-table :data="scope.row.details || []" border size="mini">
              <el-table-column label="物料名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
              <el-table-column label="来源类型" align="center" prop="sourceType" width="100">
                <template slot-scope="child">
                  <el-tag v-if="dictTagType(sourceTypeOptions, child.row.sourceType)" :type="dictTagType(sourceTypeOptions, child.row.sourceType)" size="small">{{ dictLabel(sourceTypeOptions, child.row.sourceType) }}</el-tag>
                  <span v-else>{{ dictLabel(sourceTypeOptions, child.row.sourceType) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="货位" align="center" prop="location" width="100" />
              <el-table-column label="状态" align="center" prop="status" width="90">
                <template slot-scope="child">
                  <el-tag v-if="dictTagType(statusOptions, child.row.status)" :type="dictTagType(statusOptions, child.row.status)" size="small">{{ dictLabel(statusOptions, child.row.status) }}</el-tag>
                  <span v-else>{{ dictLabel(statusOptions, child.row.status) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="剩余数量" align="center" prop="remainingQuantity" width="90" />
              <el-table-column label="出库数量" align="center" prop="outboundQuantity" width="90" />
            </el-table>
            <div v-if="!scope.row.details || scope.row.details.length === 0" class="empty-tip">暂无子表数据</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="出库编号" align="center" prop="outboundCode" width="160" />
      <el-table-column label="用户" align="center" prop="userName" width="160" />
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import request from '@/utils/request'
import {listOutboundRecord, getOutboundRecordDetail, exportOutboundRecord} from '@/api/material/record'

export default {
  name: 'OutboundRecord',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      dateRange: [],
      sourceTypeOptions: [],
      statusOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        outboundCode: undefined,
        userName: undefined,
        beginDate: undefined,
        endDate: undefined
      }
    }
  },
  created() {
    this.getDicts()
    this.getList()
  },
  methods: {
    getDicts() {
      request({ url: '/system/dict/data/type/material_source_type', method: 'get' }).then(res => {
        this.sourceTypeOptions = res.data || []
      })
      request({ url: '/system/dict/data/type/material_outbound_status', method: 'get' }).then(res => {
        this.statusOptions = res.data || []
      })
    },
    dictLabel(options, value) {
      if (value === undefined || value === null || value === '') return ''
      const item = options.find(o => String(o.dictValue) === String(value))
      return item ? item.dictLabel : value
    },
    dictTagType(options, value) {
      if (value === undefined || value === null || value === '') return ''
      const item = options.find(o => String(o.dictValue) === String(value))
      return item ? (item.listClass || '') : ''
    },
    getList() {
      this.loading = true
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginDate = this.dateRange[0]
        this.queryParams.endDate = this.dateRange[1]
      } else {
        this.queryParams.beginDate = undefined
        this.queryParams.endDate = undefined
      }
      listOutboundRecord(this.queryParams).then(res => {
        this.dataList = (res.rows || []).map(item => Object.assign({}, item, { details: null, _loading: false }))
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    // 展开行加载子表
    handleExpandChange(row, expandedRows) {
      if (expandedRows.length === 0) return
      const target = expandedRows.find(r => r.id === row.id)
      if (!target) return
      if (target.details !== null) return
      this.$set(target, '_loading', true)
      getOutboundRecordDetail(target.id).then(res => {
        this.$set(target, 'details', res.data || [])
      }).finally(() => {
        this.$set(target, '_loading', false)
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 导出（主表一个工作表，子表一个工作表，子表含出库编号列）
    handleExport() {
      const query = Object.assign({}, this.queryParams)
      if (this.dateRange && this.dateRange.length === 2) {
        query.beginDate = this.dateRange[0]
        query.endDate = this.dateRange[1]
      }
      exportOutboundRecord(query).then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '物料出库记录详情.xlsx'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      })
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
.sub-table-wrap { padding: 10px 20px; }
.empty-tip { padding: 10px; text-align: center; color: #999; font-size: 12px; }
</style>
