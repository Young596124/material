package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.system.domain.SysRole;
import com.material.modules.system.mapper.SysRoleMapper;
import com.material.modules.system.mapper.SysRoleMenuMapper;
import com.material.modules.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 角色业务实现
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService
{
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public Page<SysRole> selectRoleList(Page<SysRole> page, SysRole role)
    {
        return roleMapper.selectPage(page,
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getDelFlag, "0")
                        .like(role.getRoleName() != null && !"".equals(role.getRoleName()),
                                SysRole::getRoleName, role.getRoleName())
                        .like(role.getRoleKey() != null && !"".equals(role.getRoleKey()),
                                SysRole::getRoleKey, role.getRoleKey())
                        .eq(role.getStatus() != null && !"".equals(role.getStatus()),
                                SysRole::getStatus, role.getStatus())
                        .orderByAsc(SysRole::getRoleSort));
    }

    @Override
    public List<SysRole> selectRoleAll()
    {
        return roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getDelFlag, "0")
                        .eq(SysRole::getStatus, "0")
                        .orderByAsc(SysRole::getRoleSort));
    }

    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectById(roleId);
    }

    @Override
    public int insertRole(SysRole role)
    {
        role.setCreateBy(SecurityUtils.getNickName());
        role.setCreateTime(new Date());
        role.setDelFlag("0");
        return roleMapper.insert(role);
    }

    @Override
    public int updateRole(SysRole role)
    {
        role.setUpdateBy(SecurityUtils.getUsername());
        role.setUpdateTime(new Date());
        return roleMapper.updateById(role);
    }

    @Override
    public int deleteRoleByIds(List<Long> roleIds)
    {
        // 逻辑删除
        int count = 0;
        for (Long roleId : roleIds)
        {
            SysRole r = new SysRole();
            r.setRoleId(roleId);
            r.setDelFlag("2");
            r.setUpdateBy(SecurityUtils.getUsername());
            r.setUpdateTime(new Date());
            count += roleMapper.updateById(r);
        }
        // 同时删除角色-菜单关联
        for (Long roleId : roleIds)
        {
            roleMenuMapper.deleteByRoleId(roleId);
        }
        return count;
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId)
    {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignMenu(Long roleId, List<Long> menuIds)
    {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty())
        {
            return roleMenuMapper.batchInsert(roleId, menuIds);
        }
        return 0;
    }
}
