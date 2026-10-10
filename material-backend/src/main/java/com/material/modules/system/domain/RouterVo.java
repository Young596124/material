package com.material.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由配置信息
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo
{
    private static final long serialVersionUID = 1L;

    /** 路由名字 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 是否隐藏 true hidden false显示 */
    private Boolean hidden;

    /** 重定向地址 */
    private String redirect;

    /** 组件地址 */
    private String component;

    /** 路由参数：如 {"id": 1, "name": "ry"} */
    private String query;

    /** 当你一个路由下面的 children 声明的路由大于1个时，是否始终显示 */
    private Boolean alwaysShow;

    /** 其他元素 */
    private List<RouterVo> children = new ArrayList<>();

    /** meta信息 */
    private MetaVo meta;

    public RouterVo()
    {
    }

    public RouterVo(String name, String path, Boolean hidden, String redirect,
                    String component, String query, Boolean alwaysShow, MetaVo meta,
                    List<RouterVo> children)
    {
        this.name = name;
        this.path = path;
        this.hidden = hidden;
        this.redirect = redirect;
        this.component = component;
        this.query = query;
        this.alwaysShow = alwaysShow;
        this.meta = meta;
        this.children = children;
    }

    /**
     * meta信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MetaVo
    {
        private static final long serialVersionUID = 1L;

        /** 设置路由 tag 显示 */
        private String title;

        /** 设置该路由的图标 */
        private String icon;

        /** 设置为true，则不会被 <keep-alive> 缓存 */
        private Boolean noCache;

        public MetaVo()
        {
        }

        public MetaVo(String title, String icon)
        {
            this.title = title;
            this.icon = icon;
        }

        public MetaVo(String title, String icon, Boolean noCache)
        {
            this.title = title;
            this.icon = icon;
            this.noCache = noCache;
        }
    }
}
