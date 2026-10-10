package com.material.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单表 数据层
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu>
{
    /**
     * 查询所有菜单（用于构建路由树）
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList();
}
