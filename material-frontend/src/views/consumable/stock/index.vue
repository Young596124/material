<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="货物类型" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="请输入货物类型" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="货物名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入货物名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="货物编号" prop="goodsCode">
        <el-input v-model="queryParams.goodsCode" placeholder="请输入货物编号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="货物等级" prop="goodsLevel">
        <el-select v-model="queryParams.goodsLevel" placeholder="请选择等级" clearable size="small">
          <el-option v-for="d in levelOptions" :key="d.dictValue" :label="d.dictLabel" :value="d.dictValue" />
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
      <el-table-column label="货物图片" align="center" width="90">
        <template slot-scope="scope">
          <el-image
            v-if="scope.row.goodsImage"
            :src="resolveImageUrl(scope.row.goodsImage)"
            :preview-src-list="[resolveImageUrl(scope.row.goodsImage)]"
            style="width: 48px; height: 48px"
            fit="cover"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="货物类型" align="center" prop="categoryName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="货物等级" align="center" prop="goodsLevel" width="90">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(levelOptions, scope.row.goodsLevel)" :type="dictTagType(levelOptions, scope.row.goodsLevel)" size="small">{{ dictLabel(levelOptions, scope.row.goodsLevel) }}</el-tag>
          <span v-else>{{ dictLabel(levelOptions, scope.row.goodsLevel) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物编号" align="center" prop="goodsCode" width="110" />
      <el-table-column label="库存数量" align="center" prop="stockQuantity" width="90">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.stockQuantity > 0 ? '#67C23A' : '#F56C6C', fontWeight: 'bold' }">{{ scope.row.stockQuantity }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入库数量" align="center" prop="inboundQuantity" width="90" />
      <el-table-column label="领用数量" align="center" prop="usedQuantity" width="90" />
      <el-table-column label="退库数量" align="center" prop="returnQuantity" width="90" />
      <el-table-column label="单位" align="center" prop="unit" width="70" />
      <el-table-column label="货位号" align="center" prop="location" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.location || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库房名称" align="center" prop="storehouseName" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="库房编号" align="center" prop="storehouseCode" width="100" />
      <el-table-column label="库房地址" align="center" prop="storehouseAddress" :show-overflow-tooltip="true" />
      <el-table-column label="管理员" align="center" prop="manager" width="110" :show-overflow-tooltip="true" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listStock, exportStock } from '@/api/consumable/stock'
import { getDicts } from '@/api/system/dict/data'

export default {
  name: 'ConsumableStock',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      levelOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryName: undefined,
        goodsName: undefined,
        goodsCode: undefined,
        goodsLevel: undefined
      }
    }
  },
  created() {
    getDicts('consumable_level').then(res => {
      this.levelOptions = res.data || []
    })
    this.getList()
  },
  methods: {
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
    // 拼接图片完整地址
    resolveImageUrl(url) {
      if (!url) return ''
      if (/^https?:\/\//.test(url)) return url
      return 'http://localhost:8080' + url
    },
    getList() {
      this.loading = true
      listStock(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 导出
    handleExport() {
      exportStock(this.queryParams).then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '低值易耗品库存信息.xlsx'
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
</style>
