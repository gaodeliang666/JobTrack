# JobTrack

JobTrack 是一个面向个人求职者的求职过程管理项目，计划用于记录公司、岗位、投递、面试和沟通信息，并逐步形成个人维度的求职统计。

## 当前状态

v0.1.0 项目引导版本已于 2026-07-14 发布，v0.2.0 Spring Boot 项目骨架已于 2026-07-15 发布。v0.3.0 MySQL 表结构与可信用户上下文正在开发，当前仓库提供：

- 基于 Java 21、Spring Boot 3.5.16 和 Maven 3.9.14 的最小单体工程骨架；
- 可执行的 Spring Boot 应用入口；
- 不访问数据库的 `GET /api/health` 技术健康检查接口；
- 六张 MVP 关系骨架表的 Flyway V1 migration 和数据库设计文档；
- 通过 Spring 外部配置取得用户 ID 的 `CurrentUserProvider`；
- 最小 AppUser Mapper、用户上下文传递 Service 和无数据库自动化测试；
- 默认关闭、仅能针对安全本机测试库显式启用的真实 MySQL 集成测试。

v0.3.0 尚未发布。公司、岗位、投递、面试、沟通和统计 CRUD，登录、注册、密码、Token 和 Spring Security 均未实现。健康检查不是业务接口，也不代表后续业务版本已经完成。

## 环境要求

- Java 21；
- Maven 3.9.14。

执行以下命令时，`java` 和 Maven 必须使用同一个 Java 21 运行时：

```powershell
java -version
mvn -version
```

## 构建、测试和启动

普通自动化测试使用 `no-db-test` Profile，排除 DataSource、Flyway 和 MyBatis 自动配置，不需要 MySQL，也不读取数据库环境变量：

```powershell
mvn clean test
```

普通无数据库测试已经实际通过：Tests run 45、Passed 37、Failures 0、Errors 0、Skipped 8；8 个 skipped 均为默认关闭的 MySQL 集成测试。

MySQL 集成测试默认跳过。只有获得单独数据库操作授权、准备 localhost 上的专用空 `_test` 数据库和非 root 测试账号，并显式设置 `-Djobtrack.mysql.tests=true` 后，才允许执行：

```powershell
mvn -Djobtrack.mysql.tests=true clean test
```

第二阶段已在人工授权下使用 MySQL 8.4.8、localhost、专用非 root 测试账号和专用空 `_test` 数据库完成真实验证：

- Flyway V1 成功执行，创建六张业务表和 `flyway_schema_history`；
- 字段、索引和七个外键与数据库设计一致；
- Job 跨用户关联 Company、JobApplication 跨用户关联 Job 均被复合外键拒绝；
- CurrentUserService、Mapper 参数绑定和用户隔离查询验证通过；
- 第二次 migration 没有重复执行 V1；
- MySQL 集成测试全部通过，`mvn -Djobtrack.mysql.tests=true clean package` 成功；
- 已生成 `target/jobtrack-0.3.0-SNAPSHOT.jar`；
- 完整应用启动成功，`GET /api/health` 返回 HTTP 200 和 `{"status":"UP"}`；
- 临时测试数据通过事务回滚，六张业务表最终无数据残留。

从 v0.3.0 起，完整应用启动需要 MySQL，并必须由运行环境提供以下四项配置：

```text
JOBTRACK_DB_URL
JOBTRACK_DB_USERNAME
JOBTRACK_DB_PASSWORD
JOBTRACK_CURRENT_USER_ID
```

配置示例只使用占位值：

```powershell
$env:JOBTRACK_DB_URL = "jdbc:mysql://localhost:3306/<database-name>"
$env:JOBTRACK_DB_USERNAME = "<database-user>"
$env:JOBTRACK_DB_PASSWORD = "<local-password>"
$env:JOBTRACK_CURRENT_USER_ID = "<positive-user-id>"
```

不得把真实密码写入 README、YAML 或版本控制。提供上述正式运行变量后，可以使用已经实际验证的命令启动应用：

```powershell
mvn spring-boot:run
```

应用成功启动后，在另一个 PowerShell 中调用健康检查：

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

预期 JSON 响应为：

```json
{
  "status": "UP"
}
```

验证完成后，在应用所在终端按 `Ctrl+C` 正常停止。也可以运行打包后的可执行 JAR：

```powershell
java -jar target/jobtrack-0.3.0-SNAPSHOT.jar
```

健康接口本身不查询数据库，但完整应用启动需要数据源连接和 Flyway migration 成功。再次调用相同健康检查并在验证后按 `Ctrl+C` 停止。应用默认使用 Spring Boot 的 8080 端口，本仓库没有写死本地端口配置。

## 配置说明

`src/main/resources/application.yml` 只保存应用名称、外部配置占位符、Flyway 和 MyBatis 的非敏感配置，不提供数据库地址、账号、密码或当前用户 ID 默认值。`application-local.example.yml` 可以安全提交；复制后的 `application-local.yml` 被 Git 忽略。真实敏感配置只能通过环境变量或被 Git 忽略的本地配置注入。

## 版本路线概览

以下内容均为版本规划，不代表对应能力已经实现。详细目标与验收标准见 [版本计划](docs/version-plan.md)。

| 版本 | 主题 | 当前状态 |
| --- | --- | --- |
| v0.1.0 | 项目初始化与开发规则 | 已发布 |
| v0.2.0 | Spring Boot 项目骨架 | 已发布 |
| v0.3.0 | MySQL 表结构与用户上下文 | 进行中 |
| v0.4.0 | 公司管理 | 计划 |
| v0.5.0 | 岗位管理 | 计划 |
| v0.6.0 | 投递记录 | 计划 |
| v0.7.0 | 面试记录 | 计划 |
| v0.8.0 | 沟通记录 | 计划 |
| v0.9.0 | 基础统计 | 计划 |
| v0.10.0 | 统一响应、参数校验和全局异常 | 计划 |
| v0.11.0 | 登录与数据归属 | 计划 |
| v0.12.0 | 测试、日志、接口文档和 README | 计划 |
| v1.0.0 | MVP 正式版 | 计划 |
| v1.1.0 | Excel 导入导出 | 计划 |
| v1.2.0 | AI 岗位分析 | 计划 |
| v1.3.0 | Docker 和 CI | 计划 |

## 产品边界

JobTrack 服务于单个求职者对本人求职数据的管理。它不是招聘网站、企业 ATS、人力资源系统或多组织协作平台，也不在当前路线中承担职位抓取、自动投递、招聘方管理和商业撮合。

详细范围见 [产品范围](docs/product-scope.md) 和 [业务模型](docs/business-model.md)。

## 技术栈边界

v0.2.0 经人工确认采用 Java 21、Spring Boot 3.5.16、Maven、`spring-boot-starter-web` 和 `spring-boot-starter-test`。v0.3.0 经人工确认增加 MyBatis Spring Boot Starter 3.0.5、MySQL Connector/J、Flyway Core 和 Flyway MySQL。当前没有引入 H2、Docker、Testcontainers、Bean Validation、OpenAPI 或其他未确认技术。

未经确认不主动引入微服务、消息队列、Elasticsearch、Redis、分布式事务、Kubernetes、前端框架或当前任务不需要的生产依赖。完整约束见 [AGENTS.md](AGENTS.md)。

## 开发方式

1. 每次先阅读 `AGENTS.md`、本 README、`CHANGELOG.md` 和当前版本计划。
2. 执行 `git status` 并确认不在 `main` 分支。
3. 一次只处理一个明确任务，先给出包含文件、规则、测试、风险和验收标准的计划。
4. 等待人工明确确认计划后再修改。
5. 修改后运行适用检查，展示状态和完整差异。
6. 保持未提交状态，由人工验收并明确指示后才能暂存、提交、推送、合并、打 Tag 或创建 Release。

分层职责、`userId` 隔离、SQL 参数绑定、异常日志和敏感信息规则以 [AGENTS.md](AGENTS.md) 与 [开发流程](docs/development-workflow.md) 为准，三者必须保持一致。

## 文档导航

- [产品范围](docs/product-scope.md)
- [业务模型](docs/business-model.md)
- [版本计划](docs/version-plan.md)
- [开发流程](docs/development-workflow.md)
- [验收清单](docs/acceptance-checklist.md)
- [变更记录](CHANGELOG.md)
