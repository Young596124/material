package com.material.modules.system.service;

import com.material.modules.system.domain.SysDept;

import java.util.List;

/**
 * 部门业务接口
 */
public interface ISysDeptService
{
    /**
     * 查询部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据部门ID查询部门
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 构建部门树
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 是否存在子部门
     */
    boolean hasChildDept(Long deptId);

    /**
     * 部门下是否存在用户
     */
    boolean hasUser(Long deptId);

    /**
     * 新增部门
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门
     */
    int deleteDept(Long deptId);
}
