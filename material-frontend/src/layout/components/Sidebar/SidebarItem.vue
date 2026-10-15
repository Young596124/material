<template>
  <div v-if="!item.hidden">
    <!-- 情况1：只有一个可见子节点，或者没有子节点（显示为单行菜单） -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">

      <!-- 直接使用 el-menu-item，通过 router 属性或 index 跳转 -->
      <el-menu-item
        :index="resolvePath(onlyOneChild.path)"
        :class="{ 'submenu-title-noDropdown': !isNest }"
      >
        <!-- 渲染图标：使用 Element UI 的 i 标签 -->
        <i v-if="onlyOneChild.meta && onlyOneChild.meta.icon" :class="onlyOneChild.meta.icon"></i>
        <i v-else-if="item.meta && item.meta.icon" :class="item.meta.icon"></i>

        <!-- 渲染标题 -->
        <span slot="title">{{ (onlyOneChild.meta && onlyOneChild.meta.title) || item.meta.title }}</span>
      </el-menu-item>

    </template>

    <!-- 情况2：有多个子节点（显示为可折叠的目录） -->
    <el-submenu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template slot="title">
        <i v-if="item.meta && item.meta.icon" :class="item.meta.icon"></i>
        <span v-if="item.meta && item.meta.title" slot="title">{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import path from 'path'

export default {
  name: 'SidebarItem',
  props: {
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      onlyOneChild: null
    }
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        } else {
          this.onlyOneChild = item
          return true
        }
      })

      if (showingChildren.length === 1) {
        return true
      }

      if (showingChildren.length === 0) {
        this.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(routePath) {
      if (routePath && routePath.startsWith('/')) {
        return routePath
      }
      return path.resolve(this.basePath, routePath || '')
    }
  }
}
</script>
