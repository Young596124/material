<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="退库编号" prop="returnCode">
        <el-input v-model="queryParams.returnCode" placeholder="请输入退库编号" clearable size="small" @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增退库</el-button>
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
      <el-table-column label="退库编号" align="center" prop="returnCode" width="150" />
      <el-table-column label="货物类型" align="center" prop="categoryName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物编号" align="center" prop="goodsCode" width="110" />
      <el-table-column label="退库总数" align="center" prop="quantity" width="90" />
      <el-table-column label="单位" align="center" prop="unit" width="70" />
      <el-table-column label="退库库房" align="center" prop="storehouseName" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="责任部门" align="center" prop="deptName" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="退库时间" align="center" prop="returnTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增退库弹窗：只能退已领用的物品，退到原库房 -->
    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="领用记录" prop="pickId">
          <el-select
              v-model="form.pickId"
              filterable
              placeholder="请选择领用记录（仅显示可退的记录）"
              style="width: 100%"
              @change="handlePickChange"
          >
            <el-option
                v-for="p in pickOptions"
                :key="p.id"
                :label="p.pickCode + ' ' + p.goodsName + '（可退 ' + p.returnableQuantity + p.unit + '）'"
                :value="p.id"
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
            <el-form-item label="可退数量">
              <el-input v-model="form.returnableQuantity" disabled>
                <template slot="append">{{ form.unit }}</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退库总数" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" :max="form.returnableQuantity || 1" style="width: 100%" />
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
import { listReturn, addReturn, exportReturn } from '@/api/consumable/returnItem'
import { listReturnablePicks } from '@/api/consumable/pick'

export default {
  name: 'ConsumableReturn',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      pickOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        returnCode: undefined,
        goodsName: undefined
      },
      open: false,
      title: '',
      form: {},
      rules: {
        pickId: [{ required: true, message: '请选择领用记录', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入退库总数', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listReturn(this.queryParams).then(res => {
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
        pickId: undefined,
        categoryName: '',
        goodsName: '',
        goodsCode: '',
        unit: '',
        returnableQuantity: 0,
        quantity: 1,
        storehouseName: '',
        storehouseCode: '',
        storehouseAddress: '',
        deptName: '',
        remark: ''
      }
      this.pickOptions = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增退库'
      this.open = true
      // 加载仍可退库的领用记录
      listReturnablePicks().then(res => {
        this.pickOptions = res.data || []
      })
    },
    // 选择领用记录：带出货物与原库房信息
    handlePickChange(pickId) {
      const p = this.pickOptions.find(item => item.id === pickId)
      if (!p) return
      this.form.categoryName = p.categoryName
      this.form.goodsName = p.goodsName
      this.form.goodsCode = p.goodsCode
      this.form.unit = p.unit
      this.form.returnableQuantity = p.returnableQuantity
      this.form.quantity = 1
      this.form.storehouseName = p.storehouseName
      this.form.storehouseCode = p.storehouseCode
      this.form.storehouseAddress = p.storehouseAddress
      this.form.deptName = p.deptName
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.quantity > this.form.returnableQuantity) {
          this.$message.error('退库数量不能超过可退数量，当前可退：' + this.form.returnableQuantity)
          return
        }
        addReturn({
          pickId: this.form.pickId,
          quantity: this.form.quantity,
          remark: this.form.remark
        }).then(() => {
          this.$message.success('退库成功')
          this.open = false
          this.getList()
        })
      })
    },
    // 导出
    handleExport() {
      exportReturn(this.queryParams).then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '低值易耗品退库记录.xlsx'
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
