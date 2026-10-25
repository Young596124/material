<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="queryParams.nickName" placeholder="请输入昵称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机">
        <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="工号">
        <el-input v-model="queryParams.employeeNo" placeholder="请输入工号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option v-for="d in statusOptions" :key="d.dictValue" :label="d.dictLabel" :value="d.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" :disabled="!ids.length" @click="handleDelete">删除</el-button>
      </el-col>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="userList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户名" prop="userName" align="center" show-overflow-tooltip />
      <el-table-column label="昵称" prop="nickName" align="center" show-overflow-tooltip />
      <el-table-column label="部门" align="center" width="120">
        <template slot-scope="scope">
          <span>{{ getDeptName(scope.row.deptId) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工号" prop="employeeNo" align="center" />
      <el-table-column label="性别" align="center">
        <template slot-scope="scope">
          <span>{{ formatGender(scope.row.gender) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" prop="phonenumber" align="center" />
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" width="160" />
      <el-table-column label="角色" align="center" width="120">
        <template slot-scope="scope">
          <el-tag v-if="getRoleName(scope.row.roleId)" size="small">{{ getRoleName(scope.row.roleId) }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名" :disabled="form.userId != null" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="14">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio v-for="d in sexOptions" :key="d.dictValue" :label="d.dictValue">{{ d.dictLabel }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="d in statusOptions" :key="d.dictValue" :label="d.dictValue">{{ d.dictLabel }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-cascader
                v-model="form.deptId"
                :options="deptOptions"
                :props="deptCascaderProps"
                :show-all-levels="false"
                placeholder="请选择部门"
                style="width:100%"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select v-model="form.roleId" placeholder="请选择角色" style="width:100%" clearable>
                <el-option v-for="r in roleOptions" :key="r.roleId" :label="r.roleName" :value="r.roleId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.userId == null" :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialog.visible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, addUser, updateUser, delUser } from '@/api/system/user'
import { getDicts } from '@/api/system/dict/data'
import { allRole } from '@/api/system/role'
import { listDept } from '@/api/system/dept'

export default {
  name: 'User',
  data() {
    return {
      // 加载
      loading: true,
      // 用户列表
      userList: [],
      // 总条数
      total: 0,
      // 选中的 ID 列表
      ids: [],
      // 性别字典
      sexOptions: [],
      // 状态字典
      statusOptions: [],
      // 角色列表
      roleOptions: [],
      // 部门平铺列表(用于名称回显)
      deptList: [],
      // 部门树选项(用于表单级联选择)
      deptOptions: [],
      // 部门级联配置
      deptCascaderProps: {
        checkStrictly: true,
        value: 'deptId',
        label: 'deptName',
        children: 'children',
        emitPath: false
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: '',
        nickName: '',
        phonenumber: '',
        employeeNo: '',
        status: ''
      },
      // 弹窗
      dialog: {
        visible: false,
        title: ''
      },
      // 表单
      form: {},
      // 校验
      rules: {
        userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
        roleId: [{ required: true, message: '请选择角色', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 5, message: '密码长度不能少于 5 位', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    // 加载性别字典
    getDicts('sys_user_sex').then(res => {
      this.sexOptions = res.data || []
    }).catch(() => {})
    // 加载状态字典
    getDicts('sys_normal_disable').then(res => {
      this.statusOptions = res.data || []
    }).catch(() => {})
    // 加载角色列表
    allRole().then(res => {
      this.roleOptions = res.data || []
    }).catch(() => {})
    // 加载部门列表
    listDept().then(res => {
      this.deptList = res.rows || []
      this.deptOptions = this.buildDeptTree(this.deptList)
    }).catch(() => {})
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      listUser(this.queryParams).then(res => {
        this.userList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 格式化性别
    formatGender(value) {
      const item = this.sexOptions.find(d => d.dictValue === String(value))
      return item ? item.dictLabel : '未知'
    },
    // 格式化状态
    formatStatus(value) {
      const item = this.statusOptions.find(d => d.dictValue === String(value))
      return item ? item.dictLabel : value
    },
    // 根据 roleId 获取角色名
    getRoleName(roleId) {
      if (!roleId) return ''
      const r = this.roleOptions.find(o => o.roleId === roleId)
      return r ? r.roleName : ''
    },
    // 根据 deptId 获取部门名
    getDeptName(deptId) {
      if (!deptId) return ''
      const d = this.deptList.find(o => o.deptId === deptId)
      return d ? d.deptName : ''
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
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        userName: '',
        nickName: '',
        phonenumber: '',
        employeeNo: '',
        status: ''
      }
      this.getList()
    },
    // 选择
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
    },
    // 新增
    handleAdd() {
      this.resetForm()
      this.dialog.title = '新增用户'
      this.dialog.visible = true
    },
    // 编辑
    handleEdit(row) {
      const userId = row.userId
      getUser(userId).then(res => {
        this.form = res.data || {}
        this.dialog.title = '编辑用户'
        this.dialog.visible = true
      })
    },
    // 删除
    handleDelete(row) {
      let userIds
      if (row.userId) {
        userIds = row.userId
      } else {
        userIds = this.ids.join(',')
      }
      if (!userIds) return
      this.$confirm('确认删除选中的用户吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delUser(userIds).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.userId != null) {
          // 编辑
          updateUser(this.form).then(() => {
            this.$message.success('修改成功')
            this.dialog.visible = false
            this.getList()
          })
        } else {
          // 新增
          addUser(this.form).then(() => {
            this.$message.success('新增成功')
            this.dialog.visible = false
            this.getList()
          })
        }
      })
    },
    // 重置表单
    resetForm() {
      this.form = {
        userId: null,
        deptId: undefined,
        userName: '',
        nickName: '',
        employeeNo: '',
        gender: '0',
        phonenumber: '',
        password: '',
        status: '0',
        roleId: undefined
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 16px;
}
.search-form {
  margin-bottom: 10px;
}
.mb8 {
  margin-bottom: 10px;
}
</style>
