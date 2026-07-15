# JobTrack

JobTrack 是一个面向个人求职者的求职过程管理项目，计划用于记录公司、岗位、投递、面试和沟通信息，并逐步形成个人维度的求职统计。

## 当前状态

v0.1.0 项目引导版本已于 2026-07-14 发布。v0.2.0 Spring Boot 项目骨架已验收，待合并发布，当前仓库只提供：

- 基于 Java 21、Spring Boot 3.5.16 和 Maven 3.9.14 的最小单体工程骨架；
- 可执行的 Spring Boot 应用入口；
- 不访问数据库的 `GET /api/health` 技术健康检查接口；
- Spring 上下文加载测试和健康检查接口 MockMvc 测试。

公司、岗位、投递、面试、沟通、统计、数据库、MyBatis、登录和用户数据归属等能力均未实现。健康检查不是业务接口，也不代表后续版本功能已经完成。

## 环境要求

- Java 21；
- Maven 3.9.14。

执行以下命令时，`java` 和 Maven 必须使用同一个 Java 21 运行时：

```powershell
java -version
mvn -version
```

## 构建、测试和启动

以下命令已在 Java 21 和 Maven 3.9.14 环境中实际验证。

运行自动化测试：

```powershell
mvn clean test
```

打包可执行 JAR：

```powershell
mvn package
```

使用 Maven 启动：

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
java -jar target/jobtrack-0.2.0-SNAPSHOT.jar
```

再次调用相同健康检查并在验证后按 `Ctrl+C` 停止。应用默认使用 Spring Boot 的 8080 端口，本仓库没有写死本地端口配置。

## 配置说明

`src/main/resources/application.yml` 只设置应用名称 `jobtrack`，不包含数据库连接、真实凭据或外部资源配置。本版本没有需要示例化的外部配置，因此不创建 `application-example.yml`。真实敏感配置在后续版本中只能通过环境变量或被 Git 忽略的本地配置注入。

## 版本路线概览

以下内容均为版本规划，不代表对应能力已经实现。详细目标与验收标准见 [版本计划](docs/version-plan.md)。

| 版本 | 主题 | 当前状态 |
| --- | --- | --- |
| v0.1.0 | 项目初始化与开发规则 | 已发布 |
| v0.2.0 | Spring Boot 项目骨架 | 已验收 |
| v0.3.0 | MySQL 表结构与用户上下文 | 计划 |
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

v0.2.0 经人工确认采用 Java 21、Spring Boot 3.5.16、Maven、`spring-boot-starter-web` 和 `spring-boot-starter-test`。MyBatis、MySQL、Bean Validation、OpenAPI 及其他计划技术只有在对应后续版本计划经人工确认后才允许引入。

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
