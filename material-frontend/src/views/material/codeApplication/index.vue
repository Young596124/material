<template>
  <div class="app-container">
    <!-- 提交成功页面 -->
    <el-card v-if="submitted" class="box-card" shadow="never">
      <el-result icon="success" title="提交成功" sub-title="您的物料编码申请已提交,数据已同步至 1F 线边仓物料基表">
        <template slot="extra">
          <el-button type="primary" @click="submitAgain">再次提交</el-button>
        </template>
      </el-result>
    </el-card>

    <!-- 申请表单 -->
    <el-card v-else class="box-card" shadow="never">
      <div slot="header" class="clearfix">
        <span>物料编码申请</span>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="180px">
        <el-row>
          <el-col :span="18">
            <el-form-item label="物料编码" prop="materialCode">
              <el-input v-model="form.materialCode" disabled placeholder="提交后自动生成" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="需求人" required="required" prop="applicantId">
              <el-select v-model="form.applicantId" filterable placeholder="物料所属负责人" style="width: 100%" @change="handleApplicantChange">
                <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName + (u.employeeNo ? '(' + u.employeeNo + ')' : '')" :value="u.userId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="物料名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="规格+名称（如：KQ2H08-12A 异径直通接管），输入物料名称，不可重复" />
            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="物料所属项目系统" prop="projectSystem">
              <el-input v-model="form.projectSystem" placeholder="专属项目使用物料请填写具体到所对应系统；现场共用物料请填写‘‘车间共用‘’" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="19">
            <el-form-item label="来源类型" prop="sourceType">
              <el-radio-group v-model="form.sourceType">
                <el-radio v-for="dict in sourceTypeOptions" :key="dict.dictValue" :label="dict.dictValue">{{ dict.dictLabel }}</el-radio>
              </el-radio-group>
              <div class="ivu-form-item-description">
                <p v-for="(line, i) in descLines" :key="i">{{ line }}</p>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="18">
            <el-form-item label="货位" prop="location">
              <el-input v-model="form.location" placeholder="由库房管理员输入" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
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
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="20">
            <el-form-item label="特殊说明" prop="specialNote">
              <el-input v-model="form.specialNote" type="textarea" :rows="4" placeholder="物料如有特殊管控要求，或者其他注意事项，请在此描述清楚。" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import { addCodeApplication } from '@/api/material/codeApplication'

export default {
  name: 'CodeApplication',
  data() {
    return {
      submitting: false,
      submitted: false,
      sourceTypeOptions: [],
      userOptions: [],
      fileList: [],
      form: {
        applicantId: undefined,
        applicantName: '',
        materialName: '',
        projectSystem: '',
        sourceType: '',
        location: '',
        attachment: '',
        specialNote: ''
      },
      rules: {
        applicantId: [{ required: true, message: '请选择需求人', trigger: 'change' }],
        materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
        projectSystem: [{ required: true, message: '请输入项目系统', trigger: 'blur' }],
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
      ]
    }
  },
  created() {
    this.getDicts()
    this.getUserList()
  },
  methods: {
    getDicts() {
      request({ url: '/system/dict/data/type/material_source_type', method: 'get' }).then(res => {
        this.sourceTypeOptions = res.data || []
      })
    },
    getUserList() {
      request({ url: '/system/user/list', method: 'get', params: { pageSize: 999 } }).then(res => {
        this.userOptions = res.rows || res.data || []
      })
    },
    handleApplicantChange(userId) {
      const u = this.userOptions.find(o => o.userId === userId)
      if (u) {
        this.form.applicantName = u.nickName + (u.employeeNo ? '(' + u.employeeNo + ')' : '')
      } else {
        this.form.applicantName = ''
      }
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
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        addCodeApplication(this.form).then(() => {
          this.submitted = true
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    submitAgain() {
      this.resetForm()
      this.submitted = false
    },
    resetForm() {
      this.form = {
        applicantId: undefined,
        applicantName: '',
        materialName: '',
        projectSystem: '',
        sourceType: '',
        location: '',
        attachment: '',
        specialNote: ''
      }
      this.fileList = []
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
.box-card { margin: 20px auto; max-width: 1000px; }
.ivu-form-item-description{
  color: #999;
  font-size: 14px;
  padding-bottom: 10px;
  overflow-wrap: break-word;
}
.ivu-form-item-description p {
  margin: 0;         /* 关键：消除 <p> 默认间距 */
  line-height: 1.5;  /* 行高按需调整 */
}
</style>
