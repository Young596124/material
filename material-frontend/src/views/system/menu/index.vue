<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="菜单名称">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAdd()">新增</el-button>
      </el-col>
    </el-row>

    <!-- 树形表格 -->
    <el-table
      v-loading="loading"
      :data="menuList"
      row-key="menuId"
      border
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column label="菜单名称" prop="menuName" width="200" />
      <el-table-column label="图标" prop="icon" align="center" width="80" />
      <el-table-column label="排序" prop="orderNum" align="center" width="80" />
      <el-table-column label="权限标识" prop="perms" align="center" show-overflow-tooltip />
      <el-table-column label="组件路径" prop="component" align="center" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" icon="el-icon-plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.visible" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-cascader
                v-model="form.parentId"
                :options="menuOptions"
                :props="cascaderProps"
                :show-all-levels="false"
                placeholder="选择上级菜单"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'F'" :gutter="20">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.menuType === 'C'" label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="如 system/user/index" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'M'" :gutter="20">
          <el-col :span="24">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="如 system:user:list" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'F'" :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入图标(占位文字)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示状态" prop="visible">
              <el-radio-group v-model="form.visible">
                <el-radio label="0">显示</el-radio>
                <el-radio label="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="d in statusOptions" :key="d.dictValue" :label="d.dictValue">{{ d.dictLabel }}</el-radio>
              </el-radio-group>
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
import { listMenu, getMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu'
import { getDicts } from '@/api/system/dict/data'

export default {
  name: 'Menu',
  data() {
    return {
      loading: true,
      // 菜单列表(树形)
      menuList: [],
      // 状态字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        menuName: '',
        status: ''
      },
      // 弹窗
      dialog: {
        visible: false,
        title: ''
      },
      // 表单
      form: {},
      // 上级菜单选项(树形)
      menuOptions: [],
      // 级联配置
      cascaderProps: {
        checkStrictly: true,
        value: 'menuId',
        label: 'menuName',
        children: 'children',
        emitPath: false
      },
      // 校验
      rules: {
        menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
        orderNum: [{ required: true, message: '请输入排序', trigger: 'blur' }],
        path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    getDicts('sys_normal_disable').then(res => {
      this.statusOptions = res.data || []
    }).catch(() => {})
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      listMenu(this.queryParams).then(res => {
        // 后端可能返回平铺列表或已构建的树
        const rows = res.rows || []
        // 若任一行已带 children,视为已构建树,直接去重空子项展示
        const hasChildren = rows.some(r => r && Array.isArray(r.children) && r.children.length > 0)
        this.menuList = hasChildren ? rows : this.buildTree(rows)
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 构建树(平铺列表 → 树)
    buildTree(rows) {
      const map = {}
      const list = []
      // 先建索引
      rows.forEach(item => {
        map[item.menuId] = { ...item, children: [] }
      })
      // 再组装父子关系
      rows.forEach(item => {
        const node = map[item.menuId]
        if (item.parentId && map[item.parentId]) {
          map[item.parentId].children.push(node)
        } else {
          list.push(node)
        }
      })
      // 清理空 children,避免树表格出现展开箭头
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
    // 格式化状态
    formatStatus(value) {
      const item = this.statusOptions.find(d => d.dictValue === String(value))
      return item ? item.dictLabel : value
    },
    // 生成上级菜单选项(基于当前列表)
    buildMenuOptions() {
      const optionRoot = { menuId: 0, menuName: '主目录', children: this.menuList }
      return [optionRoot]
    },
    // 搜索
    handleQuery() {
      this.getList()
    },
    // 重置
    resetQuery() {
      this.queryParams = { menuName: '', status: '' }
      this.getList()
    },
    // 新增
    handleAdd(row) {
      this.resetForm()
      this.menuOptions = this.buildMenuOptions()
      if (row && row.menuId) {
        this.form.parentId = row.menuId
      } else {
        this.form.parentId = 0
      }
      this.dialog.title = '新增菜单'
      this.dialog.visible = true
    },
    // 编辑
    handleEdit(row) {
      getMenu(row.menuId).then(res => {
        this.form = res.data || {}
        this.menuOptions = this.buildMenuOptions()
        this.dialog.title = '编辑菜单'
        this.dialog.visible = true
      })
    },
    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该菜单吗?子菜单会一并删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delMenu(row.menuId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    // 提交
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.menuId != null) {
          updateMenu(this.form).then(() => {
            this.$message.success('修改成功')
            this.dialog.visible = false
            this.getList()
          })
        } else {
          addMenu(this.form).then(() => {
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
        menuId: null,
        parentId: 0,
        menuName: '',
        orderNum: 0,
        path: '',
        component: '',
        query: '',
        icon: '',
        menuType: 'M',
        visible: '0',
        status: '0',
        perms: '',
        isFrame: '1',
        isCache: '0'
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
