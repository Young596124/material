package com.material.framework.interceptor;

import com.material.common.constant.Constants;
import com.material.common.core.domain.AjaxResult;
import com.material.common.utils.JwtUtils;
import com.material.framework.manager.TokenService;
import com.material.modules.system.domain.LoginUser;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor
{
    /** 不需要拦截的路径 */
    private static final String[] EXCLUDE_PATHS = {
            "/login",
            "/register",
            "/captchaImage",
            "/profile/",
            "/common/download/"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String requestUri = request.getRequestURI();
        // 放行不需要拦截的路径
        for (String path : EXCLUDE_PATHS)
        {
            if (requestUri.contains(path))
            {
                return true;
            }
        }

        String token = TokenService.getToken(request);
        if (token == null)
        {
            renderJson(response, 401, "未登录或登录已过期");
            return false;
        }

        Claims claims = JwtUtils.parseToken(token);
        if (claims == null)
        {
            renderJson(response, 401, "登录已过期，请重新登录");
            return false;
        }

        String tokenKey = (String) claims.get(Constants.LOGIN_USER_KEY);
        if (tokenKey == null)
        {
            renderJson(response, 401, "无效的token");
            return false;
        }

        LoginUser loginUser = TokenService.getLoginUser(request);
        if (loginUser == null)
        {
            renderJson(response, 401, "登录已过期，请重新登录");
            return false;
        }

        return true;
    }

    private void renderJson(HttpServletResponse response, int code, String msg) throws Exception
    {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
