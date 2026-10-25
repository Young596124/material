package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.material.common.utils.SecurityUtils;
import com.material.common.utils.StringUtils;
import com.material.modules.system.domain.SysDept;
import com.material.modules.system.domain.SysUser;
import com.material.modules.system.mapper.SysDeptMapper;
import com.material.modules.system.mapper.SysUserMapper;
import com.material.modules.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门业务实现
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询部门列表
     */
    @Override
    public List<SysDept> selectDeptList(SysDept dept)
    {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<SysDept>()
                .like(dept != null && StringUtils.isNotEmpty(dept.getDeptName()),
                        SysDept::getDeptName, dept == null ? null : dept.getDeptName())
                .eq(dept != null && StringUtils.isNotEmpty(dept.getStatus()),
                        SysDept::getStatus, dept == null ? null : dept.getStatus())
                .orderByAsc(SysDept::getParentId)
                .orderByAsc(SysDept::getOrderNum);
        return sysDeptMapper.selectList(wrapper);
    }

    /**
     * 根据部门ID查询部门
     */
    @Override
    public SysDept selectDeptById(Long deptId)
    {
        return sysDeptMapper.selectById(deptId);
    }

    /**
     * 构建部门树
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<>();
        for (SysDept dept : depts)
        {
            if (dept.getParentId() != null && dept.getParentId() == 0L)
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        // 容错：未找到父节点的部门也作为根节点返回
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 是否存在子部门
     */
    @Override
    public boolean hasChildDept(Long deptId)
    {
        return sysDeptMapper.selectCount(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, deptId)) > 0;
    }

    /**
     * 部门下是否存在用户
     */
    @Override
    public boolean hasUser(Long deptId)
    {
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId)
                .eq(SysUser::getDelFlag, "0")) > 0;
    }

    /**
     * 新增部门
     */
    @Override
    public int insertDept(SysDept dept)
    {
        if (dept.getParentId() == null)
        {
            dept.setParentId(0L);
        }
        dept.setAncestors(buildAncestors(dept.getParentId()));
        dept.setCreateBy(SecurityUtils.getUsername());
        dept.setCreateTime(new Date());
        return sysDeptMapper.insert(dept);
    }

    /**
     * 修改部门
     */
    @Override
    public int updateDept(SysDept dept)
    {
        if (dept.getParentId() == null)
        {
            dept.setParentId(0L);
        }
        // 上级部门不能是自己
        if (dept.getParentId().equals(dept.getDeptId()))
        {
            return 0;
        }
        SysDept oldDept = sysDeptMapper.selectById(dept.getDeptId());
        String newAncestors = buildAncestors(dept.getParentId());
        dept.setAncestors(newAncestors);
        dept.setUpdateBy(SecurityUtils.getUsername());
        dept.setUpdateTime(new Date());
        int rows = sysDeptMapper.updateById(dept);
        // 父级变化时同步更新所有子部门的祖级列表
        if (rows > 0 && oldDept != null && !newAncestors.equals(oldDept.getAncestors()))
        {
            updateChildrenAncestors(dept.getDeptId(), oldDept.getAncestors(), newAncestors);
        }
        return rows;
    }

    /**
     * 删除部门
     */
    @Override
    public int deleteDept(Long deptId)
    {
        return sysDeptMapper.deleteById(deptId);
    }

    /**
     * 根据父部门ID计算祖级列表
     */
    private String buildAncestors(Long parentId)
    {
        if (parentId == null || parentId == 0L)
        {
            return "0";
        }
        SysDept parent = sysDeptMapper.selectById(parentId);
        if (parent == null || StringUtils.isEmpty(parent.getAncestors()))
        {
            return "0," + parentId;
        }
        return parent.getAncestors() + "," + parentId;
    }

    /**
     * 父级变化时，替换所有子部门祖级列表中的旧前缀
     */
    private void updateChildrenAncestors(Long deptId, String oldAncestors, String newAncestors)
    {
        String oldPrefix = oldAncestors + "," + deptId;
        String newPrefix = newAncestors + "," + deptId;
        List<SysDept> children = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .apply("find_in_set({0}, ancestors)", deptId));
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(
                    java.util.regex.Pattern.quote(oldPrefix), newPrefix));
            sysDeptMapper.updateById(child);
        }
    }

    /**
     * 递归组装子部门
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept child : childList)
        {
            if (!getChildList(list, child).isEmpty())
            {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept dept : list)
        {
            if (t.getDeptId().equals(dept.getParentId()))
            {
                tlist.add(dept);
            }
        }
        return tlist;
    }
}
