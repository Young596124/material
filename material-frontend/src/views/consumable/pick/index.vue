<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="领用编号" prop="pickCode">
        <el-input v-model="queryParams.pickCode" placeholder="请输入领用编号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="货物名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入货物名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作区 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增领用</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="领用编号" align="center" prop="pickCode" width="150" />
      <el-table-column label="货物类型" align="center" prop="categoryName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物编号" align="center" prop="goodsCode" width="110" />
      <el-table-column label="领用总数" align="center" prop="quantity" width="90" />
      <el-table-column label="单位" align="center" prop="unit" width="70" />
      <el-table-column label="领用库房" align="center" prop="storehouseName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="责任部门" align="center" prop="deptName" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="领用时间" align="center" prop="pickTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增领用弹窗：只能从库存领用，库房取自库存记录 -->
    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="领用货物" prop="stockId">
          <el-select
              v-model="form.stockId"
              filterable
              placeholder="请选择领用货物（仅显示有库存的记录）"
              style="width: 100%"
              @change="handleStockChange"
          >
            <el-option
                v-for="s in stockOptions"
                :key="s.id"
                :label="s.goodsName + '（' + s.storehouseName + '，可用 ' + s.stockQuantity + s.unit + '）'"
                :value="s.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物类型">
              <el-input v-model="form.categoryName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物编号">
              <el-input v-model="form.goodsCode" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="可用库存">
              <el-input v-model="form.availableQuantity" disabled>
                <template slot="append">{{ form.unit }}</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领用总数" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" :max="form.availableQuantity || 1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库房名称">
              <el-input v-model="form.storehouseName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库房编号">
              <el-input v-model="form.storehouseCode" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="责任部门">
              <el-input v-model="form.deptName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库房地址">
              <el-input v-model="form.storehouseAddress" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPick, addPick, exportPick } from '@/api/consumable/pick'
import { listAvailableStock } from '@/api/consumable/stock'

export default {
  name: 'ConsumablePick',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      stockOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        pickCode: undefined,
        goodsName: undefined
      },
      open: false,
      title: '',
      form: {},
      rules: {
        stockId: [{ required: true, message: '请选择领用货物', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入领用总数', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPick(this.queryParams).then(res => {
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
    reset() {
      this.form = {
        stockId: undefined,
        goodsId: undefined,
        storehouseId: undefined,
        categoryName: '',
        goodsName: '',
        goodsCode: '',
        unit: '',
        availableQuantity: 0,
        quantity: 1,
        storehouseName: '',
        storehouseCode: '',
        storehouseAddress: '',
        deptName: '',
        remark: ''
      }
      this.stockOptions = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增领用'
      this.open = true
      // 加载有库存的货物记录，库房信息随库存记录带出
      listAvailableStock({}).then(res => {
        this.stockOptions = res.data || []
      })
    },
    // 选择库存记录：自动带出货物与库房信息
    handleStockChange(stockId) {
      const s = this.stockOptions.find(item => item.id === stockId)
      if (!s) return
      this.form.goodsId = s.goodsId
      this.form.storehouseId = s.storehouseId
      this.form.categoryName = s.categoryName
      this.form.goodsName = s.goodsName
      this.form.goodsCode = s.goodsCode
      this.form.unit = s.unit
      this.form.availableQuantity = s.stockQuantity
      this.form.quantity = 1
      this.form.storehouseName = s.storehouseName
      this.form.storehouseCode = s.storehouseCode
      this.form.storehouseAddress = s.storehouseAddress
      this.form.deptName = s.manager
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.quantity > this.form.availableQuantity) {
          this.$message.error('领用数量不能大于库存数量，当前可用库存：' + this.form.availableQuantity)
          return
        }
        addPick({
          goodsId: this.form.goodsId,
          storehouseId: this.form.storehouseId,
          quantity: this.form.quantity,
          remark: this.form.remark
        }).then(() => {
          this.$message.success('领用成功')
          this.open = false
          this.getList()
        })
      })
    },
    // 导出
    handleExport() {
      exportPick(this.queryParams).then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '低值易耗品领用记录.xlsx'
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
