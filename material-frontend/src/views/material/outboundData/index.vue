<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="物料名称" prop="materialName">
        <el-input v-model="queryParams.materialName" placeholder="请输入物料名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="来源类型" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="请选择来源类型" clearable size="small" style="width: 160px">
          <el-option v-for="dict in sourceTypeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="货位" prop="location">
        <el-input v-model="queryParams.location" placeholder="请输入货位" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small" style="width: 140px">
          <el-option v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
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

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="物料名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
      <el-table-column label="来源类型" align="center" prop="sourceType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(sourceTypeOptions, scope.row.sourceType)" :type="dictTagType(sourceTypeOptions, scope.row.sourceType)" size="small">{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</el-tag>
          <span v-else>{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="货位" align="center" prop="location" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(statusOptions, scope.row.status)" :type="dictTagType(statusOptions, scope.row.status)" size="small">{{ dictLabel(statusOptions, scope.row.status) }}</el-tag>
          <span v-else>{{ dictLabel(statusOptions, scope.row.status) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余数量" align="center" prop="remainingQuantity" width="90" />
      <el-table-column label="出库数量" align="center" prop="outboundQuantity" width="90" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import request from '@/utils/request'
import {listOutboundData, exportOutboundData} from '@/api/material/record'

export default {
  name: 'OutboundData',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      sourceTypeOptions: [],
      statusOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        materialName: undefined,
        sourceType: undefined,
        location: undefined,
        status: undefined
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
      listOutboundData(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // ====== 导出 ======
    handleExport() {
      exportOutboundData().then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '物料出库-数据管理.xlsx'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      })
    },
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
</style>
