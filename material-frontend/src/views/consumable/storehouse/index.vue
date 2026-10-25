<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="库房编号" prop="storehouseCode">
        <el-input v-model="queryParams.storehouseCode" placeholder="请输入库房编号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="仓库名称" prop="storehouseName">
        <el-input v-model="queryParams.storehouseName" placeholder="请输入仓库名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
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
      <el-table-column label="库房编号" align="center" prop="storehouseCode" width="100" />
      <el-table-column label="仓库名称" align="center" prop="storehouseName" :show-overflow-tooltip="true" />
      <el-table-column label="责任部门" align="center" prop="deptName" width="130" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.deptName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="仓库地址" align="center" prop="address" :show-overflow-tooltip="true" />
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
      <el-table-column label="创建人" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="修改人" align="center" prop="updateBy" width="100" />
      <el-table-column label="修改时间" align="center" prop="updateTime" width="160" />
      <el-table-column label="操作" align="center" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleEdit(scope.row)">修改</el-button>
          <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDeleteRow(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="仓库名称" prop="storehouseName">
          <el-input v-model="form.storehouseName" placeholder="请输入仓库名称（唯一）" />
        </el-form-item>
        <el-form-item label="责任部门" prop="deptId">
          <el-input v-model="form.deptName" placeholder="请选择责任部门" readonly style="width: calc(100% - 90px)" />
          <el-button icon="el-icon-search" size="small" style="margin-left: 6px" @click="openDeptDialog">选择</el-button>
        </el-form-item>
        <el-form-item label="仓库地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 责任部门树形选择弹框 -->
    <el-dialog title="选择责任部门" :visible.sync="deptOpen" width="400px" append-to-body :close-on-click-modal="false">
      <el-tree
        :data="deptOptions"
        :props="deptTreeProps"
        node-key="deptId"
        default-expand-all
        highlight-current
        @node-click="handleDeptNodeClick"
      />
      <div slot="footer">
        <el-button @click="clearDept">清 除</el-button>
        <el-button @click="deptOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStorehouse, getStorehouse, addStorehouse, updateStorehouse, delStorehouse, changeStorehouseStatus } from '@/api/consumable/storehouse'
import { listDept } from '@/api/system/dept'

export default {
  name: 'ConsumableStorehouse',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      dataList: [],
      ids: [],
      multiple: true,
      deptOptions: [],
      deptTreeProps: {
        label: 'deptName',
        children: 'children'
      },
      deptOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        storehouseCode: undefined,
        storehouseName: undefined,
        status: undefined
      },
      open: false,
      title: '',
      form: {},
      rules: {
        storehouseName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
        deptId: [{ required: true, message: '请选择责任部门', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getDeptList()
    this.getList()
  },
  methods: {
    getDeptList() {
      listDept().then(res => {
        this.deptOptions = this.buildDeptTree(res.rows || [])
      }).catch(() => {})
    },
    // 构建部门树(平铺列表 → 树)
    buildDeptTree(rows) {
      const map = {}
      const list = []
      rows.forEach(item => {
        map[item.deptId] = { ...item, children: [] }
      })
      rows.forEach(item => {
        const node = map[item.deptId]
        if (item.parentId && map[item.parentId]) {
          map[item.parentId].children.push(node)
        } else {
          list.push(node)
        }
      })
      const clean = arr => arr.forEach(n => {
        if (!n.children || n.children.length === 0) {
          n.children = undefined
        } else {
          clean(n.children)
        }
      })
      clean(list)
      return list
    },
    getList() {
      this.loading = true
      listStorehouse(this.queryParams).then(res => {
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
      this.ids = selection.map(item => item.storehouseId)
      this.multiple = !selection.length
    },
    reset() {
      this.form = {
        storehouseId: undefined,
        storehouseName: '',
        deptId: undefined,
        deptName: '',
        address: '',
        status: '0'
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleAdd() {
      this.reset()
      this.title = '新增库房'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      getStorehouse(row.storehouseId).then(res => {
        this.form = res.data || {}
        this.title = '修改库房'
        this.open = true
      })
    },
    // 打开部门选择弹框
    openDeptDialog() {
      this.deptOpen = true
    },
    // 部门树节点点击
    handleDeptNodeClick(node) {
      this.form.deptId = node.deptId
      this.form.deptName = node.deptName
      this.deptOpen = false
    },
    // 清除已选部门
    clearDept() {
      this.form.deptId = undefined
      this.form.deptName = ''
      this.deptOpen = false
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.storehouseId) {
          updateStorehouse(this.form).then(() => {
            this.$message.success('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addStorehouse(this.form).then(() => {
            this.$message.success('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    // 状态修改
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$confirm('确认要' + text + '库房"' + row.storehouseName + '"吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return changeStorehouseStatus({ storehouseId: row.storehouseId, status: row.status })
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
      this.doDelete([row.storehouseId])
    },
    doDelete(ids) {
      this.$confirm('确认删除选中的库房吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delStorehouse(ids.join(','))
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
