# JobTrack v0.2.0 验收清单

> 本清单供人工验收使用。勾选表示验收人已检查；自动化测试通过不等于版本已验收或已发布。

## 1. Git 基线

- [ ] 当前分支为 `chore/v0.2.0-release`，不是 `main`。
- [ ] 发布收尾分支从 PR #3 合并提交 `27094b8` 创建，`v0.1.0` Tag 仍保持原发布提交。
- [ ] 本次发布收尾在人工确认前未执行暂存、Commit、Push、Merge、Rebase、Tag 或 GitHub Release；后续发布动作仅按人工明确指令执行。
- [ ] 工作区保持未暂存、未提交状态，可供完整审阅。

## 2. Java 与 Maven 环境

- [ ] `java -version` 显示 Java 21。
- [ ] `mvn -version` 显示 Maven 3.9.14，并确认 Maven 使用 Java 21。
- [ ] `pom.xml` 的 Java 版本为 21、Spring Boot 版本为 3.5.16、项目版本为 `0.2.0`。
- [ ] 项目使用 `io.github.gaodeliang666:jobtrack` 坐标、`jar` 打包和 `io.github.gaodeliang666.jobtrack` 根包。

## 3. 依赖与构建边界

- [ ] 唯一生产直接依赖是 `spring-boot-starter-web`。
- [ ] 唯一测试直接依赖是 `spring-boot-starter-test`，且 scope 为 `test`。
- [ ] 唯一显式构建插件是 `spring-boot-maven-plugin`。
- [ ] 没有 Maven Wrapper、Actuator、MyBatis、MySQL、Validation、OpenAPI、Lombok、DevTools、Security、Redis、Docker 或其他未确认依赖和基础设施。

## 4. 文件和目录结构

- [ ] 存在 `pom.xml` 和标准的 `src/main`、`src/test` Maven 目录结构。
- [ ] 存在 `JobTrackApplication` 应用入口。
- [ ] 存在 `health/HealthController` 和对应的 `HealthControllerTests`。
- [ ] `application.yml` 只配置 `spring.application.name: jobtrack`。
- [ ] 没有 `application-example.yml`、本地端口配置、数据库配置或真实环境配置。
- [ ] 没有无关文件、临时文件、IDE 产物或未确认的代码。

## 5. 健康检查接口

- [ ] `GET /api/health` 不要求参数并返回 HTTP 200。
- [ ] 响应 Content-Type 为 JSON，响应体的 `status` 等于 `UP`。
- [ ] 响应不包含时间戳、主机名、环境变量、内部实现或其他信息。
- [ ] 健康检查不访问数据库，不使用 Actuator 或统一响应封装。
- [ ] 没有为健康检查创建 Service、Mapper 或业务对象。

## 6. 自动化测试与打包

- [ ] Spring 上下文加载测试通过。
- [ ] 健康检查 MockMvc 测试验证 HTTP 200、JSON Content-Type 和 `status=UP`。
- [ ] `mvn clean test` 成功。
- [ ] `mvn clean package` 成功并生成 `target/jobtrack-0.2.0.jar`。

## 7. 启动验证

- [ ] `mvn spring-boot:run` 能够在不连接数据库或生产资源的情况下启动。
- [ ] Maven 启动后调用 `Invoke-RestMethod http://localhost:8080/api/health` 得到 `status=UP`。
- [ ] Maven 启动验证后使用 `Ctrl+C` 正常停止应用。
- [ ] `java -jar target/jobtrack-0.2.0.jar` 能够启动应用。
- [ ] JAR 启动后调用相同健康接口得到 `status=UP`。
- [ ] JAR 启动验证后使用 `Ctrl+C` 正常停止应用。

## 8. 未实现能力和安全边界

- [ ] 没有公司、岗位、投递、面试、沟通、统计等业务 CRUD 或业务模型代码。
- [ ] 没有数据库、MyBatis、SQL、事务、登录、安全认证或用户数据归属代码。
- [ ] 仓库不存在密码、Token、API Key、私钥、Cookie 或生产连接凭据。
- [ ] 项目文件不存在开发者本机绝对路径。
- [ ] 日志和健康响应不泄露敏感配置或内部环境信息。

## 9. 文档一致性

- [ ] README 明确 v0.2.0 已于 2026-07-15 发布，当前只有工程骨架和技术健康检查。
- [ ] README 明确业务、数据库和登录等能力尚未实现。
- [ ] README 中 Java 21、测试、打包、启动和接口调用命令已经实际验证。
- [ ] `CHANGELOG.md` 保留空的 `Unreleased`，并创建 `[0.2.0] - 2026-07-15` 正式版本区。
- [ ] `docs/version-plan.md` 只将 v0.2.0 状态改为“已发布”，其他版本状态和目标未改变。
- [ ] README、CHANGELOG 和版本计划对当前能力及状态的描述一致。

## 10. 最终差异检查

- [ ] 已审阅 `git status`。
- [ ] 已审阅 `git diff --stat`。
- [ ] 已审阅完整 `git diff`，包括所有新增和修改文件。
- [ ] 已执行 `git diff --check`，不存在空白错误。
- [ ] 已确认只修改批准范围内的文件。
- [ ] 人工验收结论和后续 Git 指令已明确给出。
