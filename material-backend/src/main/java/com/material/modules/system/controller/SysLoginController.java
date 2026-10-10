package com.material.modules.system.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.utils.SecurityUtils;
import com.material.common.utils.StringUtils;
import com.material.framework.manager.TokenService;
import com.material.modules.system.domain.LoginBody;
import com.material.modules.system.domain.LoginUser;
import com.material.modules.system.domain.RouterVo;
import com.material.modules.system.domain.SysMenu;
import com.material.modules.system.domain.SysUser;
import com.material.modules.system.service.ISysMenuService;
import com.material.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录控制器
 */
@RestController
public class SysLoginController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();
        // 校验用户名密码非空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            return AjaxResult.error("用户名/密码必须填写");
        }
        // 根据用户名查询用户
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return AjaxResult.error("用户不存在/密码错误");
        }
        if ("1".equals(user.getStatus()))
        {
            return AjaxResult.error("账号已被停用，请联系管理员");
        }
        // 校验密码
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            return AjaxResult.error("用户不存在/密码错误");
        }
        // 创建LoginUser并生成token
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        // 清除密码再缓存，避免敏感信息存入Redis
        user.setPassword(null);
        loginUser.setUser(user);
        String token = tokenService.createToken(loginUser);
        return AjaxResult.success().put("token", token);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo(HttpServletRequest request)
    {
        LoginUser loginUser = TokenService.getLoginUser(request);
        if (loginUser == null)
        {
            return AjaxResult.error("登录信息已过期，请重新登录");
        }
        SysUser user = loginUser.getUser();
        // 不返回密码
        user.setPassword(null);
        return AjaxResult.success(user);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request)
    {
        String token = TokenService.getToken(request);
        if (token != null)
        {
            tokenService.removeToken(token);
        }
        return AjaxResult.success("退出成功");
    }

    /**
     * 获取路由树
     */
    @GetMapping("/getRouters")
    public AjaxResult getRouters()
    {
        List<SysMenu> menus = menuService.selectMenuTreeAll();
        List<SysMenu> menuTree = menuService.buildMenus(menus);
        List<RouterVo> routerVos = buildRouters(menuTree);
        return AjaxResult.success(routerVos);
    }

    /**
     * 构建前端路由对象
     */
    private List<RouterVo> buildRouters(List<SysMenu> menus)
    {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus)
        {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setAlwaysShow("0".equals(menu.getVisible()) && menu.getChildren() != null && !menu.getChildren().isEmpty());
            router.setMeta(new RouterVo.MetaVo(menu.getMenuName(), menu.getIcon(), "1".equals(menu.getIsCache())));
            List<RouterVo> children = new ArrayList<>();
            if (menu.getChildren() != null && !menu.getChildren().isEmpty())
            {
                children = buildRouters(menu.getChildren());
            }
            router.setChildren(children);
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     */
    private String getRouteName(SysMenu menu)
    {
        String name = capitalize(menu.getPath());
        // 非外链且是一级目录
        if (isParentView(menu))
        {
            name = capitalize(menu.getPath()) + "Index";
        }
        return name;
    }

    /**
     * 首字母大写
     */
    private String capitalize(String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 获取路由地址
     */
    private String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        // 一级目录
        if (isParentView(menu) && "0".equals(String.valueOf(menu.getParentId())))
        {
            routerPath = "/" + menu.getPath();
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     */
    private String getComponent(SysMenu menu)
    {
        String component = menu.getComponent();
        if (StringUtils.isEmpty(component) && isParentView(menu))
        {
            component = "Layout";
        }
        return component;
    }

    /**
     * 是否为父视图（目录）
     */
    private boolean isParentView(SysMenu menu)
    {
        return "M".equals(menu.getMenuType()) && StringUtils.isEmpty(menu.getComponent());
    }
}
