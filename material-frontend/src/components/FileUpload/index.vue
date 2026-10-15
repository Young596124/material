<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="uploadData"
      :name="fieldName"
      :list-type="listType"
      :file-list="fileList"
      :limit="limit"
      :multiple="multiple"
      :accept="accept"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
      :show-file-list="!autoHidden"
    >
      <template v-if="listType === 'picture-card'">
        <i class="el-icon-plus"></i>
      </template>
      <template v-else>
        <el-button size="small" type="primary" icon="el-icon-upload">
          {{ buttonText }}
        </el-button>
      </template>
      <template v-if="tip" #tip>
        <div class="el-upload__tip">{{ tip }}</div>
      </template>
    </el-upload>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'

export default {
  name: 'FileUpload',
  props: {
    // 绑定值(单个 url 数组,用于 v-model 同步)
    value: {
      type: [String, Array],
      default: ''
    },
    // 上传按钮文字
    buttonText: {
      type: String,
      default: '点击上传'
    },
    // 文件字段名
    fieldName: {
      type: String,
      default: 'file'
    },
    // 上传数量限制
    limit: {
      type: Number,
      default: 5
    },
    // 是否多选
    multiple: {
      type: Boolean,
      default: false
    },
    // 接受的文件类型
    accept: {
      type: String,
      default: ''
    },
    // 列表样式 text/picture/picture-card
    listType: {
      type: String,
      default: 'text'
    },
    // 文件大小限制(MB)
    maxSize: {
      type: Number,
      default: 10
    },
    // 提示文字
    tip: {
      type: String,
      default: ''
    },
    // 上传成功后是否隐藏文件列表
    autoHidden: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 上传地址:dev 走代理 /api,prod 走 /dev-api
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      // 额外参数
      uploadData: {},
      // 文件列表
      fileList: [],
      // 已上传文件 url 列表
      urlList: []
    }
  },
  computed: {
    uploadHeaders() {
      return {
        Authorization: 'Bearer ' + (getToken() || '')
      }
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(val) {
        if (typeof val === 'string') {
          this.urlList = val ? [val] : []
        } else if (Array.isArray(val)) {
          this.urlList = [...val]
        }
        this.fileList = this.urlList.map((url, idx) => ({
          name: 'file-' + idx,
          url: this.resolveUrl(url)
        }))
      }
    }
  },
  methods: {
    // 上传前校验
    beforeUpload(file) {
      const isLtMax = file.size / 1024 / 1024 < this.maxSize
      if (!isLtMax) {
        this.$message.error('上传文件大小不能超过 ' + this.maxSize + 'MB')
        return false
      }
      return true
    },
    // 上传成功
    handleSuccess(res, file, fileList) {
      if (res.code === 200) {
        const url = res.data && res.data.url ? res.data.url : ''
        if (url) {
          this.urlList.push(url)
          this.emitValue()
        }
        this.$message.success('上传成功')
      } else {
        this.$message.error(res.msg || '上传失败')
      }
    },
    // 上传失败
    handleError() {
      this.$message.error('上传失败,请稍后重试')
    },
    // 移除文件
    handleRemove(file, fileList) {
      // 根据 file.url 反查 urlList
      const targetUrl = (file.response && file.response.data && file.response.data.url) || file.url
      const rawUrl = this.stripPrefix(targetUrl)
      this.urlList = this.urlList.filter(u => u !== rawUrl)
      this.emitValue()
    },
    // 超出限制
    handleExceed() {
      this.$message.warning('最多只能上传 ' + this.limit + ' 个文件')
    },
    // 同步 value
    emitValue() {
      if (typeof this.value === 'string') {
        this.$emit('input', this.urlList.join(','))
      } else {
        this.$emit('input', this.urlList)
      }
    },
    // 拼接后端地址前缀,用于展示
    resolveUrl(url) {
      if (!url) return ''
      if (/^https?:\/\//.test(url)) return url
      return 'http://localhost:8080' + url
    },
    // 去掉展示前缀,得到原始相对 url
    stripPrefix(url) {
      if (!url) return ''
      return url.replace(/^https?:\/\/[^/]+/, '')
    }
  }
}
</script>

<style scoped>
.file-upload {
  display: inline-block;
}
</style>
