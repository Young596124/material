<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <span>物料入库</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="用户" prop="userName">
              <el-input v-model="form.userName" disabled placeholder="当前登录用户" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入库日期" prop="inboundDate">
              <el-date-picker v-model="form.inboundDate" disabled type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 入库数据录入子表格 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openMaterialDialog">新增物料</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="selectionList.length === 0" @click="removeSelectedRows">删除选中</el-button>
        </el-col>
      </el-row>
      <el-table :data="form.details" border @selection-change="handleSelectionChange" :max-height="380">
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column label="物料名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
        <el-table-column label="来源类型" align="center" prop="sourceType" width="150">
          <template slot-scope="scope">
            <el-tag v-if="dictTagType(sourceTypeOptions, scope.row.sourceType)" :type="dictTagType(sourceTypeOptions, scope.row.sourceType)" size="small">{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</el-tag>
            <span v-else>{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="货位" align="center" prop="location" width="100" />
        <el-table-column label="状态" align="center" width="200">
          <template slot-scope="scope">
            <el-radio-group v-model="scope.row.status" size="mini">
              <el-radio v-for="dict in statusOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
        <el-table-column label="剩余数量" align="center" prop="remainingQuantity" width="90" />
        <el-table-column label="入库数量" align="center" width="180">
          <template slot-scope="scope">
            <el-input-number v-model="scope.row.inboundQuantity" :min="0" :disabled="scope.row.status === '2'" size="mini" controls-position="right" style="width: 150px" placeholder="入库数量" />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-delete" @click="removeRow(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-form>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div class="footer-bar">
        <el-button type="primary" @click="submitForm" v-loading="submitting">提交入库</el-button>
        <el-button @click="resetForm">重置</el-button>
      </div>
    </el-card>

    <!-- 选择物料弹框 -->
    <el-dialog title="选择物料" :visible.sync="materialDialog" width="780px" append-to-body :close-on-click-modal="false">
      <el-form :inline="true" label-width="80px">
        <el-form-item label="物料名称">
          <el-input v-model="materialQuery.materialName" placeholder="请输入物料名称" clearable size="small" @keyup.enter.native="searchMaterial" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="searchMaterial">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetMaterialQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="materialLoading"
        :data="materialOptions"
        border
        highlight-current-row
        @selection-change="handleMaterialSelectionChange"
        :max-height="380">
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column label="物料编码" align="center" prop="materialCode" width="160" />
        <el-table-column label="物料名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
        <el-table-column label="来源类型" align="center" prop="sourceType" width="120">
          <template slot-scope="scope">
            <el-tag v-if="dictTagType(sourceTypeOptions, scope.row.sourceType)" :type="dictTagType(sourceTypeOptions, scope.row.sourceType)" size="small">{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</el-tag>
            <span v-else>{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="货位" align="center" prop="location" width="100" />
        <el-table-column label="剩余数量" align="center" prop="remainingQuantity" width="90" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmSelectMaterial" :disabled="selectedMaterials.length === 0">确 定</el-button>
        <el-button @click="materialDialog = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { addInbound } from '@/api/material/inbound'
import { selectAllWarehouseBase, selectByName } from '@/api/material/warehouseBase'

export default {
  name: 'Inbound',
  data() {
    return {
      submitting: false,
      sourceTypeOptions: [],
      statusOptions: [],
      selectionList: [],
      // 表单
      form: {
        userName: '',
        userId: undefined,
        inboundDate: '',
        description: '',
        details: []
      },
      rules: {
        inboundDate: [{ required: true, message: '请选择入库日期', trigger: 'change' }]
      },
      // 物料选择弹框
      materialDialog: false,
      materialLoading: false,
      materialOptions: [],
      selectedMaterials: [],
      materialQuery: { materialName: '' }
    }
  },
  computed: {
    userInfo() {
      return (this.$store && this.$store.state && this.$store.state.user) || {}
    }
  },
  created() {
    this.getDicts()
    this.loadCurrentUser()
    this.form.inboundDate = this.parseTime(new Date(), '{y}-{m}-{d}')
    this.loadAllMaterial()
  },
  methods: {
    // 获取字典
    getDicts() {
      request({ url: '/system/dict/data/type/material_source_type', method: 'get' }).then(res => {
        this.sourceTypeOptions = res.data || []
      })
      request({ url: '/system/dict/data/type/material_inbound_status', method: 'get' }).then(res => {
        this.statusOptions = res.data || []
      })
    },
    // 载入当前用户（userId + userName=昵称(工号)）
    loadCurrentUser() {
      // 优先用 store 中已有信息同步回显，避免空白
      const st = this.$store.state.user || {}
      if (st.userId) {
        this.form.userId = st.userId
        const nick = st.nickname || st.name || ''
        this.form.userName = nick
      }
      // 拉取完整信息（含 employeeNo）覆盖填充
      if (this.$store && this.$store.dispatch) {
        this.$store.dispatch('user/getInfo').then(data => {
          const u = data || {}
          this.form.userId = u.userId
          const nick = u.nickName || u.userName || ''
          const emp = u.employeeNo || ''
          this.form.userName = emp ? (nick + '(' + emp + ')') : nick
        }).catch(() => {})
      }
    },
    // 字典标签
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
    // 时间格式化（轻量实现，避免依赖全局方法）
    parseTime(time, pattern) {
      const fmt = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      const d = time instanceof Date ? time : new Date(time)
      const map = { y: d.getFullYear(), m: String(d.getMonth() + 1).padStart(2, '0'), d: String(d.getDate()).padStart(2, '0'), h: String(d.getHours()).padStart(2, '0'), i: String(d.getMinutes()).padStart(2, '0'), s: String(d.getSeconds()).padStart(2, '0') }
      return fmt.replace(/\{(\w)\}/g, (m, k) => map[k] || '')
    },
    // 子表格多选
    handleSelectionChange(selection) {
      this.selectionList = selection
    },
    // 删除选中行
    removeSelectedRows() {
      const removeIds = this.selectionList.map(s => s._uid)
      this.form.details = this.form.details.filter(r => !removeIds.includes(r._uid))
    },
    // 删除单行
    removeRow(index) {
      this.form.details.splice(index, 1)
    },
    // 打开物料弹框
    openMaterialDialog() {
      this.materialDialog = true
      this.selectedMaterials = []
      this.materialQuery.materialName = ''
      this.loadAllMaterial()
    },
    // 加载全部物料
    loadAllMaterial() {
      this.materialLoading = true
      selectAllWarehouseBase().then(res => {
        this.materialOptions = res.data || []
      }).finally(() => {
        this.materialLoading = false
      })
    },
    // 按名称搜索
    searchMaterial() {
      if (!this.materialQuery.materialName) {
        this.loadAllMaterial()
        return
      }
      this.materialLoading = true
      selectByName(this.materialQuery.materialName).then(res => {
        this.materialOptions = res.data || []
      }).finally(() => {
        this.materialLoading = false
      })
    },
    resetMaterialQuery() {
      this.materialQuery.materialName = ''
      this.loadAllMaterial()
    },
    handleMaterialCurrentChange(val) {
      this.currentMaterial = val
    },
    // 确认选择物料
    confirmSelectMaterial() {
      if (!this.selectedMaterials) {
        this.$message.warning('请至少选择一行物料')
        return
      }
      let added = 0
      let skipped = 0
      this.selectedMaterials.forEach(m => {
        // 防止重复添加
        if (this.form.details.some(r => r.materialBaseId === m.id)) {
          skipped++
          return
        }
        this.form.details.push({
          _uid: new Date().getTime() + Math.random(),
          materialBaseId: m.id,
          materialName: m.materialName,
          sourceType: m.sourceType,
          location: m.location,
          status: '1',
          remainingQuantity: m.remainingQuantity,
          inboundQuantity: undefined
        })
        added++
      })
      if (added > 0) this.$message.success(`已添加 ${added} 条物料` + (skipped ? `，${skipped} 条已存在被跳过` : ''))
      else this.$message.warning('所选物料都已添加')
      this.materialDialog = false
    },
    // 提交
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (!this.form.details || this.form.details.length === 0) {
          this.$message.error('请至少添加一条入库数据')
          return
        }
        // 校验入库数量
        for (let i = 0; i < this.form.details.length; i++) {
          const row = this.form.details[i]
          if (row.status === '1') {
            if (row.inboundQuantity === undefined || row.inboundQuantity === null || row.inboundQuantity === '' || Number(row.inboundQuantity) <= 0) {
              this.$message.error('第' + (i + 1) + '行入库数量必须大于0')
              return
            }
          } else {
            // 取消状态置 0
            row.inboundQuantity = 0
          }
        }
        // 组装 InboundDTO
        const dto = {
          userId: this.form.userId,
          userName: this.form.userName,
          inboundDate: this.form.inboundDate,
          description: this.form.description,
          details: this.form.details.map(r => ({
            materialBaseId: r.materialBaseId,
            materialName: r.materialName,
            sourceType: r.sourceType,
            location: r.location,
            status: r.status,
            remainingQuantity: r.remainingQuantity,
            inboundQuantity: r.inboundQuantity
          }))
        }
        this.submitting = true
        addInbound(dto).then(() => {
          this.$message.success('入库提交成功')
          this.resetForm()
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    resetForm() {
      this.form.description = ''
      this.form.details = []
      this.selectionList = []
      this.$refs.form && this.$refs.form.clearValidate()
    },

    // 多选变化
    handleMaterialSelectionChange(selection) {
      this.selectedMaterials = selection
    },
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
.footer-bar { margin-top: 16px; text-align: center; }
</style>
