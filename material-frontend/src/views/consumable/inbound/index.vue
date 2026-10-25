<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="入库编号" prop="inboundCode">
        <el-input v-model="queryParams.inboundCode" placeholder="请输入入库编号" clearable size="small" @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增入库</el-button>
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
      <el-table-column label="入库编号" align="center" prop="inboundCode" width="150" />
      <el-table-column label="货物类型" align="center" prop="categoryName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物编号" align="center" prop="goodsCode" width="110" />
      <el-table-column label="入库总数" align="center" prop="quantity" width="90" />
      <el-table-column label="单位" align="center" prop="unit" width="70" />
      <el-table-column label="入库库房" align="center" prop="storehouseName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="责任部门" align="center" prop="deptName" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="入库时间" align="center" prop="inboundTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增入库弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="货物类型" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请先选择货物类型" style="width: 100%" @change="handleCategoryChange">
            <el-option v-for="c in categoryOptions" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="货物名称" prop="goodsId">
          <el-select v-model="form.goodsId" placeholder="请选择货物名称" style="width: 100%" :disabled="!form.categoryId" @change="handleGoodsChange">
            <el-option v-for="g in goodsOptions" :key="g.goodsId" :label="g.goodsName" :value="g.goodsId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物编号">
              <el-input v-model="form.goodsCode" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="入库总数" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :max="999999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="入库库房" prop="storehouseId">
          <el-input v-model="form.storehouseName" placeholder="请选择入库库房" readonly style="width: calc(100% - 90px)" />
          <el-button icon="el-icon-search" size="small" style="margin-left: 6px" @click="openStorehouseDialog">选择</el-button>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库房编号">
              <el-input v-model="form.storehouseCode" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任部门">
              <el-input v-model="form.deptName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="库房地址">
          <el-input v-model="form.storehouseAddress" disabled />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 库房选择弹框（列表单选） -->
    <el-dialog title="选择入库库房" :visible.sync="storehouseOpen" width="720px" append-to-body :close-on-click-modal="false">
      <el-table :data="storehouseOptions" highlight-current-row @row-click="handleStorehouseSelect" size="small" max-height="380">
        <el-table-column label="库房名称" align="center" prop="storehouseName" :show-overflow-tooltip="true" />
        <el-table-column label="库房编号" align="center" prop="storehouseCode" width="100" />
        <el-table-column label="库房地址" align="center" prop="address" :show-overflow-tooltip="true" />
        <el-table-column label="责任部门" align="center" prop="deptName" width="120" :show-overflow-tooltip="true" />
      </el-table>
      <div slot="footer">
        <el-button @click="storehouseOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInbound, addInbound, exportInbound } from '@/api/consumable/inbound'
import { selectEnabledCategory } from '@/api/consumable/category'
import { listGoodsByCategory } from '@/api/consumable/goods'
import { selectEnabledStorehouse } from '@/api/consumable/storehouse'

export default {
  name: 'ConsumableInbound',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      categoryOptions: [],
      goodsOptions: [],
      storehouseOptions: [],
      storehouseOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        inboundCode: undefined,
        goodsName: undefined
      },
      open: false,
      title: '',
      form: {},
      rules: {
        categoryId: [{ required: true, message: '请选择货物类型', trigger: 'change' }],
        goodsId: [{ required: true, message: '请选择货物名称', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入入库总数', trigger: 'blur' }],
        storehouseId: [{ required: true, message: '请选择入库库房', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getCategoryList()
    this.getStorehouseList()
    this.getList()
  },
  methods: {
    getCategoryList() {
      selectEnabledCategory().then(res => {
        this.categoryOptions = res.data || []
      })
    },
    getStorehouseList() {
      selectEnabledStorehouse().then(res => {
        this.storehouseOptions = res.data || []
      })
    },
    getList() {
      this.loading = true
      listInbound(this.queryParams).then(res => {
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
        categoryId: undefined,
        goodsId: undefined,
        goodsCode: '',
        unit: '',
        quantity: 1,
        storehouseId: undefined,
        storehouseName: '',
        storehouseCode: '',
        storehouseAddress: '',
        deptName: '',
        remark: ''
      }
      this.goodsOptions = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增入库'
      this.open = true
    },
    // 货物类型变化：加载该类别下的物品
    handleCategoryChange(categoryId) {
      this.form.goodsId = undefined
      this.form.goodsCode = ''
      this.form.unit = ''
      this.goodsOptions = []
      if (categoryId) {
        listGoodsByCategory(categoryId).then(res => {
          this.goodsOptions = res.data || []
        })
      }
    },
    // 货物名称变化：自动带出货物编号、单位
    handleGoodsChange(goodsId) {
      const goods = this.goodsOptions.find(g => g.goodsId === goodsId)
      if (goods) {
        this.form.goodsCode = goods.goodsCode
        this.form.unit = goods.unit
      } else {
        this.form.goodsCode = ''
        this.form.unit = ''
      }
    },
    // 打开库房选择弹框
    openStorehouseDialog() {
      this.storehouseOpen = true
    },
    // 库房行点击：自动带入库房信息
    handleStorehouseSelect(row) {
      this.form.storehouseId = row.storehouseId
      this.form.storehouseName = row.storehouseName
      this.form.storehouseCode = row.storehouseCode
      this.form.storehouseAddress = row.address
      this.form.deptName = row.deptName
      this.storehouseOpen = false
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        addInbound(this.form).then(() => {
          this.$message.success('入库成功')
          this.open = false
          this.getList()
        })
      })
    },
    // 导出
    handleExport() {
      exportInbound(this.queryParams).then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '低值易耗品入库记录.xlsx'
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
