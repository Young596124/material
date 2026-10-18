-- =====================================
-- 物料入库出库管理系统 数据库脚本
-- =====================================

CREATE DATABASE IF NOT EXISTS `material_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `material_db`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
     `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
     `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
     `role_id` bigint DEFAULT NULL COMMENT '角色ID',
     `user_name` varchar(30) NOT NULL COMMENT '用户账号',
     `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
     `employee_no` varchar(30) DEFAULT NULL COMMENT '工号',
     `gender` char(1) DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
     `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
     `password` varchar(100) DEFAULT '' COMMENT '密码',
     `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
     `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
     `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
     `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
     PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- 初始管理员账号: admin / admin123
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `employee_no`, `gender`, `phonenumber`, `password`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(1, NULL, 'admin', '管理员', 'A001', '0', '13800000000', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOjTHK.2m5OB1IuLkF3nV3XHLQKx.', '0', '0', 'admin', NOW());

-- ----------------------------
-- 2. 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id`     BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name`   VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `parent_id`   BIGINT(20)  DEFAULT 0 COMMENT '父菜单ID',
  `order_num`   INT(4)      DEFAULT 0 COMMENT '显示顺序',
  `path`        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
  `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `query`       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame`    CHAR(1)     DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache`    CHAR(1)     DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type`   CHAR(1)     DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible`     CHAR(1)     DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms`       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `icon`        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 菜单数据
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
-- 系统管理
(1, '系统管理', 0, 1, 'system', NULL, NULL, '1', '0', 'M', '0', '0', '', 'system', 'admin', NOW()),
(100, '用户管理', 1, 1, 'user', 'system/user/index', NULL, '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW()),
(101, '菜单管理', 1, 2, 'menu', 'system/menu/index', NULL, '1', '0', 'C', '0', '0', 'system:menu:list', 'menu', 'admin', NOW()),
(102, '字典管理', 1, 3, 'dict', 'system/dict/index', NULL, '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', NOW()),
-- 物料管理
(2, '物料管理', 0, 2, 'material', NULL, NULL, '1', '0', 'M', '0', '0', '', 'tool', 'admin', NOW()),
(2000, '物料编码申请', 2, 1, 'codeApplication', 'material/codeApplication/index', NULL, '1', '0', 'C', '0', '0', 'material:codeApplication:apply', 'edit', 'admin', NOW()),
(2001, '1F线边仓物料基表', 2, 2, 'warehouseBase', 'material/warehouseBase/index', NULL, '1', '0', 'C', '0', '0', 'material:warehouseBase:list', 'list', 'admin', NOW()),
(2002, '物料入库', 2, 3, 'inbound', 'material/inbound/index', NULL, '1', '0', 'C', '0', '0', 'material:inbound:add', 'upload', 'admin', NOW()),
(2003, '物料入库-记录详情', 2, 4, 'inboundRecord', 'material/inboundRecord/index', NULL, '1', '0', 'C', '0', '0', 'material:inboundRecord:list', 'log', 'admin', NOW()),
(2004, '物料入库-数据管理', 2, 5, 'inboundData', 'material/inboundData/index', NULL, '1', '0', 'C', '0', '0', 'material:inboundData:list', 'table', 'admin', NOW()),
(2005, '物料出库', 2, 6, 'outbound', 'material/outbound/index', NULL, '1', '0', 'C', '0', '0', 'material:outbound:add', 'download', 'admin', NOW()),
(2006, '物料出库-记录详情', 2, 7, 'outboundRecord', 'material/outboundRecord/index', NULL, '1', '0', 'C', '0', '0', 'material:outboundRecord:list', 'log', 'admin', NOW()),
(2007, '物料出库-数据管理', 2, 8, 'outboundData', 'material/outboundData/index', NULL, '1', '0', 'C', '0', '0', 'material:outboundData:list', 'table', 'admin', NOW());

-- ----------------------------
-- 3. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id`     BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name`   VARCHAR(100) DEFAULT '' COMMENT '字典名称',
  `dict_type`   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
  `status`      CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `create_by`, `create_time`) VALUES
('用户性别', 'sys_user_sex', '0', 'admin', NOW()),
('用户状态', 'sys_normal_disable', '0', 'admin', NOW()),
('来源类型', 'material_source_type', '0', 'admin', NOW()),
('入库状态', 'material_inbound_status', '0', 'admin', NOW()),
('出库状态', 'material_outbound_status', '0', 'admin', NOW());

-- ----------------------------
-- 4. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code`  BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort`  INT(4)      DEFAULT 0 COMMENT '字典排序',
  `dict_label` VARCHAR(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` VARCHAR(100) DEFAULT '' COMMENT '字典键值',
  `dict_type`  VARCHAR(100) DEFAULT '' COMMENT '字典类型',
  `css_class`  VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
  `list_class` VARCHAR(100) DEFAULT NULL COMMENT '列表样式',
  `is_default` CHAR(1)     DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status`     CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by`  VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME   DEFAULT NULL COMMENT '创建时间',
  `update_by`  VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME   DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`) VALUES
-- 用户性别
(1, '男', '0', 'sys_user_sex', '', 'primary', 'Y', '0', 'admin', NOW()),
(2, '女', '1', 'sys_user_sex', '', 'danger', 'N', '0', 'admin', NOW()),
(3, '未知', '2', 'sys_user_sex', '', 'info', 'N', '0', 'admin', NOW()),
-- 用户状态
(1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', NOW()),
(2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', NOW()),
-- 来源类型
(1, '工装', '1', 'material_source_type', '', 'primary', 'N', '0', 'admin', NOW()),
(2, '工具', '2', 'material_source_type', '', 'success', 'N', '0', 'admin', NOW()),
(3, '量具', '3', 'material_source_type', '', 'info', 'N', '0', 'admin', NOW()),
(4, '仪器仪表', '4', 'material_source_type', '', 'warning', 'N', '0', 'admin', NOW()),
(5, '辅料', '5', 'material_source_type', '', 'danger', 'N', '0', 'admin', NOW()),
(6, '研发物料', '6', 'material_source_type', '', '', 'N', '0', 'admin', NOW()),
-- 入库状态
(1, '入库', '1', 'material_inbound_status', '', 'success', 'Y', '0', 'admin', NOW()),
(2, '取消', '2', 'material_inbound_status', '', 'danger', 'N', '0', 'admin', NOW()),
-- 出库状态
(1, '入库', '1', 'material_outbound_status', '', 'success', 'Y', '0', 'admin', NOW()),
(2, '取消', '2', 'material_outbound_status', '', 'danger', 'N', '0', 'admin', NOW());

-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `role_id`     BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name`   VARCHAR(30) NOT NULL COMMENT '角色名称',
                            `role_key`    VARCHAR(100) NOT NULL COMMENT '角色权限字符串(admin/common等)',
                            `role_sort`   INT(4)      DEFAULT 0 COMMENT '显示顺序',
                            `data_scope`  CHAR(1)     DEFAULT '1' COMMENT '数据范围(1全部 2自定义)',
                            `status`      CHAR(1)     DEFAULT '0' COMMENT '状态(0正常 1停用)',
                            `del_flag`    CHAR(1)     DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
                            `create_by`   VARCHAR(64) DEFAULT '' COMMENT '创建者',
                            `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
                            `update_by`   VARCHAR(64) DEFAULT '' COMMENT '更新者',
                            `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
                            `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`role_id`),
                            UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始角色:超级管理员 + 普通用户
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
                                                                                                                                         (1, '超级管理员', 'admin', 1, '0', '0', 'admin', NOW(), '拥有所有权限'),
                                                                                                                                         (2, '普通用户', 'common', 2, '0', '0', 'admin', NOW(), '基础物料操作权限');

-- ----------------------------
-- 角色-菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
                                 `menu_id` BIGINT(20) NOT NULL COMMENT '菜单ID',
                                 PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- 超级管理员(role_id=1):分配所有菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1), (1, 100), (1, 101), (1, 102),       -- 系统管理全部
                                                       (1, 2), (1, 2000), (1, 2001), (1, 2002), (1, 2003), (1, 2004), (1, 2005), (1, 2006), (1, 2007),
-- 角色管理(菜单ID稍后插入,先不包含)
                                                       (1, 3);
-- 普通用户(role_id=2):只分配物料管理下的编码申请/入库/出库
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (2, 2), (2, 2000), (2, 2002), (2, 2005);

-- ----------------------------
-- 更新 admin 用户的 role_id
-- ----------------------------
UPDATE `sys_user` SET `role_id` = 1 WHERE `user_id` = 1;

-- ----------------------------
-- 给菜单表增加"角色管理"菜单项(挂在系统管理下)
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
    (3, '角色管理', 1, 4, 'role', 'system/role/index', NULL, '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW());

-- 给超级管理员补上角色管理菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 3);


-- ----------------------------
-- 6. 1F线边仓物料基表
-- ----------------------------
DROP TABLE IF EXISTS `warehouse_material_base`;
CREATE TABLE `warehouse_material_base` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `material_code` varchar(50) NOT NULL COMMENT '物料编码',
    `material_name` varchar(200) NOT NULL COMMENT '物料名称',
    `applicant_name` varchar(100) DEFAULT NULL COMMENT '需求人',
    `project_system` varchar(200) DEFAULT NULL COMMENT '物料所属项目系统',
    `source_type` varchar(10) DEFAULT NULL COMMENT '来源类型',
    `location` varchar(100) DEFAULT NULL COMMENT '货位',
    `total_inbound` int DEFAULT '0' COMMENT '入库总数',
    `remaining_quantity` int DEFAULT '0' COMMENT '剩余数量',
    `outbound_quantity` int DEFAULT '0' COMMENT '出库数量',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `applicant_id` bigint DEFAULT NULL COMMENT '需求人id',
    `attachment` varchar(500) DEFAULT NULL COMMENT '附件',
    `special_note` text COMMENT '特殊说明',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='1F线边仓物料基表';

-- ----------------------------
-- 7. 物料入库主表
-- ----------------------------
DROP TABLE IF EXISTS `material_inbound`;
CREATE TABLE `material_inbound` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inbound_code`  VARCHAR(50)  NOT NULL COMMENT '入库编号（RK+yyyyMMdd+2位流水）',
  `user_id`       BIGINT(20)   DEFAULT NULL COMMENT '用户ID',
  `user_name`     VARCHAR(100) DEFAULT NULL COMMENT '用户名称+工号',
  `inbound_date`  DATE         DEFAULT NULL COMMENT '入库日期',
  `description`   TEXT         DEFAULT NULL COMMENT '描述',
  `create_by`     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料入库主表';

-- ----------------------------
-- 8. 物料入库子表
-- ----------------------------
DROP TABLE IF EXISTS `material_inbound_detail`;
CREATE TABLE `material_inbound_detail` (
  `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inbound_id`         BIGINT(20)   NOT NULL COMMENT '入库主表ID',
  `material_base_id`   BIGINT(20)   DEFAULT NULL COMMENT '物料基表ID',
  `material_name`      VARCHAR(200) DEFAULT NULL COMMENT '物料名称',
  `source_type`        VARCHAR(10)  DEFAULT NULL COMMENT '来源类型',
  `location`           VARCHAR(100) DEFAULT NULL COMMENT '货位',
  `status`             VARCHAR(10)  DEFAULT NULL COMMENT '状态（1入库 2取消）',
  `remaining_quantity` INT(11)      DEFAULT 0 COMMENT '剩余数量',
  `inbound_quantity`   INT(11)      DEFAULT 0 COMMENT '入库数量',
  `create_by`          VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料入库子表';

-- ----------------------------
-- 9. 物料出库主表
-- ----------------------------
DROP TABLE IF EXISTS `material_outbound`;
CREATE TABLE `material_outbound` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `outbound_code`  VARCHAR(50)  NOT NULL COMMENT '出库编号（CK+yyyyMMdd+2位流水）',
  `user_id`        BIGINT(20)   DEFAULT NULL COMMENT '用户ID',
  `user_name`      VARCHAR(100) DEFAULT NULL COMMENT '用户名称+工号',
  `description`    TEXT         DEFAULT NULL COMMENT '描述',
  `create_by`      VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料出库主表';

-- ----------------------------
-- 10. 物料出库子表
-- ----------------------------
DROP TABLE IF EXISTS `material_outbound_detail`;
CREATE TABLE `material_outbound_detail` (
  `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `outbound_id`        BIGINT(20)   NOT NULL COMMENT '出库主表ID',
  `material_base_id`   BIGINT(20)   DEFAULT NULL COMMENT '物料基表ID',
  `material_name`      VARCHAR(200) DEFAULT NULL COMMENT '物料名称',
  `source_type`        VARCHAR(10)  DEFAULT NULL COMMENT '来源类型',
  `location`           VARCHAR(100) DEFAULT NULL COMMENT '货位',
  `status`             VARCHAR(10)  DEFAULT NULL COMMENT '状态（1入库 2取消）',
  `remaining_quantity` INT(11)      DEFAULT 0 COMMENT '剩余数量',
  `outbound_quantity`  INT(11)      DEFAULT 0 COMMENT '出库数量',
  `create_by`          VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料出库子表';

-- =====================================
-- 仓库管理模块迁移脚本
-- 1. 新增仓库表 warehouse
-- 2. 物料编码申请表 / 1F线边仓物料基表 增加仓库字段
-- 3. 注册「系统管理 - 仓库管理」菜单
-- =====================================
USE `material_db`;

-- 1. 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
                                           `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
    `name`        VARCHAR(100) NOT NULL COMMENT '仓库名称',
    `manager`     VARCHAR(100) DEFAULT NULL COMMENT '管理员',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '状态（0启用 1禁用）',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息表';

-- 3. 1F线边仓物料基表增加仓库字段
ALTER TABLE `warehouse_material_base`
    ADD COLUMN `warehouse_id`   BIGINT(20)   DEFAULT NULL COMMENT '仓库ID' AFTER `applicant_name`,
  ADD COLUMN `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称' AFTER `warehouse_id`;

-- 4. 注册「系统管理 - 仓库管理」菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`)
VALUES (103, '仓库管理', 1, 4, 'warehouse', 'system/warehouse/index', NULL, '1', '0', 'C', '0', '0', 'system:warehouse:list', 'tree', 'admin', NOW());
