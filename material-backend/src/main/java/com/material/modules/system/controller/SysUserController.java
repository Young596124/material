package com.material.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysUser;
import com.material.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        Page<SysUser> page = buildPage();
        Page<SysUser> result = userService.selectUserList(page, user);
        return getDataTable(result);
    }

    /**
     * 根据用户ID查询详情
     */
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId)
    {
        SysUser user = userService.selectUserById(userId);
        if (user != null)
        {
            user.setPassword(null);
        }
        return AjaxResult.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public AjaxResult add(@RequestBody SysUser user)
    {
        if (!userService.checkUserNameUnique(user))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysUser user)
    {
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        int count = 0;
        for (Long userId : userIds)
        {
            count += userService.deleteUser(userId);
        }
        return toAjax(count);
    }

    /**
     * 响应返回结果
     */
    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
