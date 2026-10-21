# 物料入库出库管理系统

基于 Spring Boot + Vue 前后端分离的物料入库出库管理系统（参考若依框架风格）。

## 技术栈

| 层 | 选型 |
|---|---|
| 后端 | Java 8 + Spring Boot 2.7.18 + MyBatis-Plus 3.5.3.1 + Redis + JWT |
| 前端 | Vue 2.6 + Element UI 2.15 + Vue Router 3 + Vuex 3 + Axios |
| 数据库 | MySQL 8.x |
| 构建 | Maven（后端）/ npm（前端） |

## 目录结构

```
Material/
├── material-backend/        后端工程
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/material/
│       │   ├── MaterialApplication.java          启动类
│       │   ├── common/        通用：配置/常量/异常/工具/核心基类
│       │   ├── framework/     JWT 拦截器、TokenService
│       │   └── modules/
│       │       ├── system/    系统模块：用户/菜单/字典/登录/文件上传
│       │       └── material/  物料模块：编码申请/基表/入库/出库 + 记录/数据管理
│       └── resources/
│           ├── application.yml / application-dev.yml
│           ├── mapper/        MyBatis XML（system + material）
│           └── sql/material.sql   建库建表 + 初始数据脚本
├── material-frontend/       前端工程
│   ├── package.json / vue.config.js / .env.*
│   └── src/
│       ├── main.js / App.vue / settings.js
│       ├── api/              登录、系统、物料 API
│       ├── router/           静态路由 + 动态路由守卫
│       ├── store/            Vuex（user 模块：登录/用户信息/动态路由）
│       ├── utils/            request.js（axios 封装）、auth.js（token）
│       ├── layout/           布局：Sidebar / Navbar / AppMain
│       ├── components/       Pagination、FileUpload
│       └── views/
│           ├── login/        登录页
│           ├── index.vue     首页
│           ├── system/       用户管理 / 菜单管理 / 字典管理
│           └── material/     编码申请 / 基表 / 入库 / 入库记录 / 入库数据
│                            / 出库 / 出库记录 / 出库数据
└── README.md
```

## 功能清单

### 基础权限
- 登录（用户名 + 密码，JWT 鉴权，token 存 Redis）
- 用户管理（用户名、工号、性别、手机号、密码）
- 菜单管理（树形，支持目录/菜单/按钮类型）
- 字典管理（字典类型 + 字典数据）

### 物料业务（9 个页面）
1. **物料编码申请**：表单填写，物料编码按 `yyyyMMdd+2位流水号` 自动生成，提交后同步到 1F 线边仓物料基表
2. **1F线边仓物料基表**：分页列表，含入库总数/剩余数量/出库数量
3. **物料入库**：表单 + 子表格录入，自动编号 `RK+yyyyMMdd+2位流水`，状态=入库时累加基表库存
4. **物料入库-记录详情**：分页列表，可展开查看入库子表明细
5. **物料入库-数据管理**：分页列表（来源：入库子表数据）
6. **物料出库**：表单 + 子表格录入，自动编号 `CK+yyyyMMdd+2位流水`，校验出库数量≤剩余数量
7. **物料出库-记录详情**：分页列表，可展开查看出库子表明细
8. **物料出库-数据管理**：分页列表（来源：出库子表数据）

## 快速启动

### 1. 准备环境
- JDK 1.8、Maven 3.6+、MySQL 8.x、Redis、Node.js 14+

### 2. 初始化数据库
```bash
# 在 MySQL 中执行建库建表脚本
mysql -uroot -p < material-backend/src/main/resources/sql/material.sql
```
脚本会自动创建 `material_db` 库、10 张表，并插入初始管理员、菜单、字典数据。

### 3. 启动后端
```bash
cd material-backend
# 按需修改 src/main/resources/application-dev.yml 中的 MySQL/Redis 连接信息
mvn spring-boot:run
# 或 mvn clean package 后 java -jar target/material-management.jar
```
后端默认端口 `8080`。

> 如需修改文件上传目录，调整 `application-dev.yml` 中 `material.profile` 与 `WebConfig` 的 `PROFILE_PATH`。

### 4. 启动前端
```bash
cd material-frontend
npm install
npm run serve
```
前端默认端口 `80`，开发环境通过 `/api` 代理到后端 `http://localhost:8080`。

执行编译命令npm install之前未配置npm依赖仓地址，导致默认去到外网下载，外网连不上所以一直卡住；

在npm install命令执行之前加上

npm config set registry https://cmc.centralrepo.rnd.huawei.com/artifactory/api/npm/npm-central-repo/

npm config set strict-ssl false



### 5. 访问系统
浏览器打开 `http://localhost:80`

默认账号：`admin` / `admin123`

## 验证流程

1. 登录（admin/admin123）→ 进入首页，侧边栏展示系统管理、物料管理菜单
2. 物料编码申请 → 填写并提交 → 1F线边仓物料基表出现该物料
3. 物料入库 → 选择物料录入入库数量并提交 → 基表库存增加 → 入库记录/数据管理出现数据
4. 物料出库 → 选择物料录出库数量（≤剩余数量）提交 → 基表库存减少 → 出库记录/数据管理出现数据

## 接口约定

- 鉴权：请求头 `Authorization: Bearer <token>`
- 统一响应：`{ code, msg, data? }`，列表响应 `{ code, msg, rows, total }`（code=200 成功，401 未登录）
- 文件上传：`POST /common/upload`，字段名 `file`，返回 `data.url`
- 主要接口：
  - 登录 `POST /login`、用户信息 `GET /getInfo`、路由 `GET /getRouters`、登出 `POST /logout`
  - 用户 `/system/user`、菜单 `/system/menu`、字典类型 `/system/dict/type`、字典数据 `/system/dict/data`
  - 物料编码申请 `/material/codeApplication`、基表 `/material/warehouseBase`
  - 入库 `/material/inbound`、入库记录 `/material/inboundRecord`、入库数据 `/material/inboundData`
  - 出库 `/material/outbound`、出库记录 `/material/outboundRecord`、出库数据 `/material/outboundData`
## 低值易耗品管理
	
	
	1.货物管理:
		1)  物品类别（分页列表）: 
			字段：类型名称、状态（启用、禁用）、排序
			功能：新增、修改、删除、状态修改		
		2）物品信息（分页列表）：
			字段：货物类型（必选，下拉选择类别的类型名称）、货物编号（大类名称 首字母大写+六位流水号）、货物名称（不可重复）、
						货物单价（数字类型）、
						货物单位（下拉选择字典数据：ml，PCS，个、件、克、具、包、千克、卷、双、只、台、块、套、平方、张、支、本、条、根、桶、瓶、盏、盒、箱、米、粒、组、节、袋、把、扎）、
						消耗类型（无、例行消耗-月度、例行消耗-季度、例行消耗-半年度）、
						显示状态（显示，隐藏）、描述、等级（A、B、C）、描述、图片上传
			功能：新增、修改、删除、状态修改	
	2.库房管理(分页列表）：
 		字段：库房编号（KF+三位流水号）、仓库名称（唯一）、责任部门（树形弹框选择）、仓库地址、状态（启用，停用）、创建人、创建时间、修改人、修改时间
		功能：新增、修改、删除、状态修改
	3.部门管理（分页列表）：
		字段：部门名称（必填唯一）、上级部门、负责人 
		功能：新增、修改、删除       
	4.物品入库（分页列表）：
		入库时逻辑：入库编号自动生成，先选择物品类型，才能再选择类别下的物品名称，自动带出货物编号、货物单位，选择入库库房自动带入库房名称、库房编号、库房地址、责任部门
		字段：入库编号（RK+年月日+3位流水号）、货物类型、货物名称、 货物编号、 入库总数、单位、 入库库房（弹框列表选择，库房名称、库房编号、库房地址、责任部门）、备注、创建人、入库时间
		功能：新增入库、导出
	5.库存信息（物品入库后产生库存信息，库房id+货物id唯一）：货物类型、货物等级、货物图片、库存数量、入库数量、领用数量、退库数量、单位、库房信息（库房名称、库房编号、库房地址、管理员）
	6.物品领用：领用编号（LY+年月日+3位流水号）、 货物类型、货物名称、 货物编号、领用总数、单位、 库房信息（弹框列表选择，库房名称、库房编号、库房地址、责任部门）、备注、创建人、领用时间
		功能：新增领用、导出
	7.物品退库：退库编号（TK+年月日+3位流水号）、货物类型、货物名称、 货物编号、 退库总数、单位、 退库库房（弹框列表选择，库房名称、库房编号、库房地址、责任部门）、备注、创建人、退库时间
		功能：新增退库、导出

