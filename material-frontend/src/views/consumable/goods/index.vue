<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="货物类型" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择货物类型" clearable size="small">
          <el-option v-for="c in categoryOptions" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="货物名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入货物名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="货物编号" prop="goodsCode">
        <el-input v-model="queryParams.goodsCode" placeholder="请输入货物编号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="启用" value="0" />
          <el-option label="禁用" value="1" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" @click="getList">刷新</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="货物类型" align="center" prop="categoryName" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="货物编号" align="center" prop="goodsCode" width="120" />
      <el-table-column label="货物名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="货物单价" align="center" prop="price" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.price != null ? scope.row.price : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="货物单位" align="center" prop="unit" width="90" />
      <el-table-column label="消耗类型" align="center" prop="consumeType" width="130">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(consumeTypeOptions, scope.row.consumeType)" :type="dictTagType(consumeTypeOptions, scope.row.consumeType)" size="small">{{ dictLabel(consumeTypeOptions, scope.row.consumeType) }}</el-tag>
          <span v-else>{{ dictLabel(consumeTypeOptions, scope.row.consumeType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="等级" align="center" prop="level" width="80">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(levelOptions, scope.row.level)" :type="dictTagType(levelOptions, scope.row.level)" size="small">{{ dictLabel(levelOptions, scope.row.level) }}</el-tag>
          <span v-else>{{ dictLabel(levelOptions, scope.row.level) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="显示状态" align="center" prop="showStatus" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.showStatus === '0' ? 'success' : 'info'" size="small">{{ scope.row.showStatus === '0' ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEdit(scope.row)">修改</el-button>
          <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDeleteRow(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物类型" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择货物类型" style="width: 100%">
                <el-option v-for="c in categoryOptions" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物名称" prop="goodsName">
              <el-input v-model="form.goodsName" placeholder="请输入货物名称（不可重复）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货物单价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" style="width: 100%" placeholder="请输入货物单价" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="货物单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择货物单位" style="width: 100%" clearable>
                <el-option v-for="d in unitOptions" :key="d.dictValue" :label="d.dictLabel" :value="d.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="消耗类型" prop="consumeType">
              <el-select v-model="form.consumeType" placeholder="请选择消耗类型" style="width: 100%">
                <el-option v-for="d in consumeTypeOptions" :key="d.dictValue" :label="d.dictLabel" :value="d.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%" clearable>
                <el-option v-for="d in levelOptions" :key="d.dictValue" :label="d.dictLabel" :value="d.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="显示状态" prop="showStatus">
              <el-radio-group v-model="form.showStatus">
                <el-radio label="0">显示</el-radio>
                <el-radio label="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">启用</el-radio>
                <el-radio label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <file-upload v-model="form.image" list-type="picture-card" :limit="1" accept="image/*" />
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
import { listGoods, getGoods, addGoods, updateGoods, delGoods, changeGoodsStatus } from '@/api/consumable/goods'
import { selectEnabledCategory } from '@/api/consumable/category'
import { getDicts } from '@/api/system/dict/data'

export default {
  name: 'ConsumableGoods',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      ids: [],
      multiple: true,
      categoryOptions: [],
      unitOptions: [],
      consumeTypeOptions: [],
      levelOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryId: undefined,
        goodsName: undefined,
        goodsCode: undefined,
        status: undefined
      },
      open: false,
      title: '',
      form: {},
      rules: {
        categoryId: [{ required: true, message: '请选择货物类型', trigger: 'change' }],
        goodsName: [{ required: true, message: '请输入货物名称', trigger: 'blur' }],
        unit: [{ required: true, message: '请选择货物单位', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getCategoryList()
    this.getDictData()
    this.getList()
  },
  methods: {
    getCategoryList() {
      selectEnabledCategory().then(res => {
        this.categoryOptions = res.data || []
      })
    },
    getDictData() {
      getDicts('consumable_unit').then(res => {
        this.unitOptions = res.data || []
      })
      getDicts('consumable_consume_type').then(res => {
        this.consumeTypeOptions = res.data || []
      })
      getDicts('consumable_level').then(res => {
        this.levelOptions = res.data || []
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
      listGoods(this.queryParams).then(res => {
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.goodsId)
      this.multiple = !selection.length
    },
    reset() {
      this.form = {
        goodsId: undefined,
        categoryId: undefined,
        goodsName: '',
        price: undefined,
        unit: undefined,
        consumeType: '0',
        showStatus: '0',
        level: undefined,
        description: '',
        image: '',
        status: '0'
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增物品'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      getGoods(row.goodsId).then(res => {
        this.form = res.data || {}
        this.title = '修改物品'
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.goodsId) {
          updateGoods(this.form).then(() => {
            this.$message.success('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addGoods(this.form).then(() => {
            this.$message.success('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    // 状态修改
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '禁用'
      this.$confirm('确认要' + text + '货物"' + row.goodsName + '"吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeGoodsStatus({ goodsId: row.goodsId, status: row.status })
      }).then(() => {
        this.$message.success(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    handleDelete() {
      this.doDelete(this.ids)
    },
    handleDeleteRow(row) {
      this.doDelete([row.goodsId])
    },
    doDelete(ids) {
      this.$confirm('确认删除选中的物品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delGoods(ids.join(','))
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
</style>
