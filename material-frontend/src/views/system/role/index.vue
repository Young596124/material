<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="88px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="权限字符串" prop="roleKey">
        <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符串" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable size="small" style="width:100px">
          <el-option label="正常" value="0" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增角色</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="角色ID" align="center" prop="roleId" width="80" />
      <el-table-column label="角色名称" align="center" prop="roleName" width="150" />
      <el-table-column label="权限字符串" align="center" prop="roleKey" width="150" />
      <el-table-column label="显示顺序" align="center" prop="roleSort" width="80" />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="240">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-lock" @click="handleAssignMenu(scope.row)">分配菜单</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符串" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="如 admin / common" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 分配菜单弹窗 -->
    <el-dialog :title="assignTitle" :visible.sync="assignOpen" width="520px" append-to-body :close-on-click-modal="false">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="角色">
          <el-input v-model="assignForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTree"
            :data="menuTree"
            show-checkbox
            node-key="menuId"
            :props="{ label: 'menuName', children: 'children' }"
            :default-checked-keys="checkedMenuIds"
            :default-expanded-keys="checkedMenuIds"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAssign">保 存</el-button>
        <el-button @click="assignOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { listRole, getRole, addRole, updateRole, delRole, assignMenu } from '@/api/system/role'

export default {
  name: 'Role',
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      open: false,
      title: '',
      form: {},
      rules: {
        roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
        roleKey: [{ required: true, message: '请输入权限字符串', trigger: 'blur' }],
        roleSort: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }]
      },
      queryParams: { pageNum: 1, pageSize: 10, roleName: undefined, roleKey: undefined, status: undefined },
      // 分配菜单相关
      assignOpen: false,
      assignTitle: '',
      assignForm: { roleId: undefined, roleName: '' },
      menuTree: [],
      checkedMenuIds: []
    }
  },
  created() {
    this.getList()
    this.loadMenuTree()
  },
  methods: {
    getList() {
      this.loading = true
      listRole(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm.resetFields(); this.handleQuery() },
    handleAdd() { this.reset(); this.open = true; this.title = '新增角色' },
    handleUpdate(row) {
      this.reset()
      getRole(row.roleId).then(res => {
        this.form = res.data || {}
        this.open = true
        this.title = '修改角色'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.roleId) {
          updateRole(this.form).then(() => {
            this.$message.success('修改成功'); this.open = false; this.getList()
          })
        } else {
          addRole(this.form).then(() => {
            this.$message.success('新增成功'); this.open = false; this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该角色？', '提示', { type: 'warning' }).then(() => {
        return delRole(row.roleId)
      }).then(() => {
        this.$message.success('删除成功'); this.getList()
      }).catch(() => {})
    },
    cancel() { this.open = false; this.reset() },
    reset() {
      this.form = { roleId: undefined, roleName: '', roleKey: '', roleSort: 0, status: '0', remark: '' }
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },

    // ====== 分配菜单 ======
    loadMenuTree() {
      request({ url: '/system/menu/tree', method: 'get' }).then(res => {
        this.menuTree = res.data || []
      })
    },
    handleAssignMenu(row) {
      getRole(row.roleId).then(res => {
        const data = res.data || {}
        this.assignForm = { roleId: data.roleId, roleName: data.roleName }
        this.checkedMenuIds = res.menuIds || []
        this.assignTitle = '分配菜单 - ' + data.roleName
        this.assignOpen = true
      })
    },
    submitAssign() {
      const checkedNodes = this.$refs.menuTree.getCheckedKeys(true) // includeHalfChecked=true
      // el-tree 勾选节点 + 半选父节点一起存(确保完整父子关系)
      const ids = checkedNodes.filter(id => id !== undefined && id !== null)
      assignMenu({ roleId: this.assignForm.roleId, menuIds: ids }).then(() => {
        this.$message.success('菜单分配成功')
        this.assignOpen = false
      })
    }
  }
}
</script>

<style scoped>
.mb8 { margin-bottom: 8px; }
</style>
