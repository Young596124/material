<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="物料编码" prop="materialCode">
        <el-input v-model="queryParams.materialCode" placeholder="请输入物料编码" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
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
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-upload2" size="mini" @click="handleImport">导入</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="物料编码" align="center" prop="materialCode" width="160" />
      <el-table-column label="物料名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
      <el-table-column label="需求人" align="center" prop="applicantName" width="120" />
      <el-table-column label="物料所属项目系统" align="center" prop="projectSystem" :show-overflow-tooltip="true" />
      <el-table-column label="来源类型" align="center" prop="sourceType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="dictTagType(sourceTypeOptions, scope.row.sourceType)" :type="dictTagType(sourceTypeOptions, scope.row.sourceType)" size="small">{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</el-tag>
          <span v-else>{{ dictLabel(sourceTypeOptions, scope.row.sourceType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="货位" align="center" prop="location" width="100" />
      <el-table-column label="入库总数" align="center" prop="totalInbound" width="90" />
      <el-table-column label="剩余数量" align="center" prop="remainingQuantity" width="90" />
      <el-table-column label="出库数量" align="center" prop="outboundQuantity" width="90" />
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="修改人" align="center" prop="updateBy" width="100" />
      <el-table-column label="修改时间" align="center" prop="updateTime" width="160" />
      <el-table-column label="操作" align="center" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑物料基表" :visible.sync="open" width="800px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="物料编码" prop="materialCode">
          <el-input v-model="form.materialCode" disabled />
        </el-form-item>
        <el-form-item label="物料名称" prop="materialName">
          <el-input v-model="form.materialName" placeholder="请输入物料名称" />
        </el-form-item>
        <el-form-item label="需求人" prop="applicantName">
          <el-input v-model="form.applicantName" placeholder="请输入需求人" />
        </el-form-item>
        <el-form-item label="物料所属项目系统" prop="projectSystem">
          <el-input v-model="form.projectSystem" placeholder="请输入物料所属项目系统" />
        </el-form-item>
        <el-form-item label="来源类型" prop="sourceType">
          <el-radio-group v-model="form.sourceType">
            <el-radio v-for="dict in sourceTypeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
          <div class="ivu-form-item-description">
            <p v-for="(line, i) in descLines" :key="i">{{ line }}</p>
          </div>
        </el-form-item>
        <el-form-item label="货位" prop="location">
          <el-input v-model="form.location" placeholder="请输入货位" />
        </el-form-item>
        <el-form-item label="附件" prop="attachment">
          <el-upload
              :http-request="handleUpload"
              :on-remove="handleUploadRemove"
              :file-list="fileList"
              :limit="1"
              accept=".png,.jpg,.jpeg,.gif,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.htm,.html"
              action="#"
              :auto-upload="true">
            <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">支持 png/jpg/jpeg/gif/pdf/doc/docx/xls/xlsx/ppt/pptx/txt/htm/html,单文件</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="特殊说明" prop="specialNote">
          <el-input v-model="form.specialNote" type="textarea" :rows="4" placeholder="物料如有特殊管控要求，或者其他注意事项，请在此描述清楚。" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入弹窗 -->
    <el-dialog title="导入物料基表" :visible.sync="importOpen" width="500px" append-to-body :close-on-click-modal="false">
      <div class="mb8">
        <el-button type="primary" size="mini" icon="el-icon-download" @click="handleDownloadTemplate">下载模板</el-button>
      </div>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx,.xls"
        :auto-upload="false"
        :on-change="handleFileChange"
        action="#"
        drag>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将 Excel 文件拖到此处,或<em>点击选择</em></div>
        <div class="el-upload__tip" slot="tip">支持 .xlsx/.xls 格式;物料编码已存在则更新,否则新增</div>
      </el-upload>
      <div slot="footer">
        <el-button type="primary" @click="submitImport">确 定</el-button>
        <el-button @click="importOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import {
  listWarehouseBase, getWarehouseBase, updateWarehouseBase,
  exportWarehouseBase, importWarehouseBase, importTemplateWarehouseBase
} from '@/api/material/warehouseBase'

export default {
  name: 'WarehouseBase',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      sourceTypeOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        materialCode: undefined,
        materialName: undefined,
        sourceType: undefined,
        location: undefined
      },
      // 编辑弹窗
      open: false,
      form: {},
      rules: {
        materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
        projectSystem: [{ required: true, message: '请输入物料所属项目系统', trigger: 'blur' }],
        sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
        location: [{ required: true, message: '请输入货位', trigger: 'blur' }]
      },
      descLines: [
        '①工装（需在物料名称处按要求填写工装号；例如：底板 GZ347856）',
        '②工具（仅接收通过安全评审并符合使用要求的工具，不接收不良品）',
        '③量具（仅接收计量合格并在有效期内的量具，到期退回责任人自行计量处理）',
        '④仪器仪表（有编码/资产编号）',
        '⑤辅料、标准件类物料（不可混放需明确实际数量及分类）',
        '⑥研发物料（仅接收短暂存放或重复频繁使用的物料）',
        '备注：工装、工具、量具、仪器仪表，不接收缺少配件附件进行入库，不能使用不合格物料的不能入库）'
      ],
      // 导入
      importOpen: false,
      uploadFile: null
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
      listWarehouseBase(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm.resetFields(); this.handleQuery() },

    // ====== 编辑 ======
    handleEdit(row) {
      getWarehouseBase(row.id).then(res => {
        this.form = res.data || {}
        this.open = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        updateWarehouseBase(this.form).then(() => {
          this.$message.success('修改成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleUpload(options) {
      const formData = new FormData()
      formData.append('file', options.file)
      request({ url: '/common/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } }).then(res => {
        const d = (res && res.data) || res || {}
        const url = d.url || d.fileName || ''
        this.form.attachment = url
        this.fileList = [{ name: options.file.name, url: url }]
        this.$message.success('上传成功')
      }).catch(() => {
        this.$message.error('上传失败')
      })
    },
    handleUploadRemove() {
      this.form.attachment = ''
      this.fileList = []
    },

    // ====== 导出 ======
    handleExport() {
      exportWarehouseBase().then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '1F线边仓物料基表.xlsx'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      })
    },

    // ====== 导入 ======
    handleImport() {
      this.uploadFile = null
      this.importOpen = true
      this.$nextTick(() => {
        this.$refs.upload && this.$refs.upload.clearFiles()
      })
    },
    handleDownloadTemplate() {
      importTemplateWarehouseBase().then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]))
        const a = document.createElement('a')
        a.href = url
        a.download = '1F线边仓物料基表导入模板.xlsx'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
        this.$message.success('模板下载成功')
      })
    },
    handleFileChange(file) {
      this.uploadFile = file.raw
    },
    submitImport() {
      if (!this.uploadFile) {
        this.$message.warning('请先选择文件')
        return
      }
      const formData = new FormData()
      formData.append('file', this.uploadFile)
      importWarehouseBase(formData).then(res => {
        this.$message.success(res.msg || '导入成功')
        this.importOpen = false
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
.ivu-form-item-description p {
  margin: 0;         /* 关键：消除 <p> 默认间距 */
  line-height: 1.5;  /* 行高按需调整 */
}
</style>
