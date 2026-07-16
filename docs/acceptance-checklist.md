# JobTrack v0.3.0 验收清单

> 本清单用于 v0.3.0 正式发布收尾。功能 PR #5 已合并，第二阶段真实 MySQL 验证已经完成；发布收尾仍需通过自动化检查和最终人工审查，当前尚未创建或合并发布收尾 PR，也尚未创建 Tag 或 GitHub Release。

## 1. Git 与版本基线

- [ ] 当前分支为 `chore/v0.3.0-release`，不是 `main`。
- [ ] 发布分支从 PR #5 合并提交 `8599d61` 创建，`v0.1.0` 和 `v0.2.0` Tag 保持原发布提交不变。
- [ ] 功能 PR #5 已合并，功能分支的 5 个批准 Commit 已进入 `main`。
- [ ] v0.1.0、v0.2.0 和 v0.3.0 标记为“已发布”，v0.4.0 及后续版本保持“计划”。
- [ ] 项目版本为正式版本 `0.3.0`。
- [ ] 当前尚未创建或合并发布收尾 PR，尚未创建 `v0.3.0` Tag 或 GitHub Release。

## 2. Java、Maven 与依赖

- [ ] `java -version` 显示 Java 21。
- [ ] `mvn -version` 显示 Maven 3.9.14，并使用 Java 21 runtime。
- [ ] Spring Boot 版本仍为 3.5.16，唯一显式构建插件仍为 `spring-boot-maven-plugin`。
- [ ] MyBatis 直接依赖为 `org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5`。
- [ ] MySQL Driver 直接依赖为 `com.mysql:mysql-connector-j`，scope 为 `runtime`，不显式指定版本。
- [ ] Flyway 直接依赖为 `org.flywaydb:flyway-core` 和 `org.flywaydb:flyway-mysql`，不显式指定版本。
- [ ] Connector/J 和 Flyway 版本由 Spring Boot 3.5.16 依赖管理控制。
- [ ] 没有 `spring-boot-starter-flyway`、H2、Docker、Testcontainers 或其他未确认依赖和插件。

## 3. 配置和敏感信息

- [ ] 完整应用只通过 `JOBTRACK_DB_URL`、`JOBTRACK_DB_USERNAME`、`JOBTRACK_DB_PASSWORD` 和 `JOBTRACK_CURRENT_USER_ID` 取得数据库及当前用户配置。
- [ ] `application.yml` 不提供可能误连数据库的默认值。
- [ ] `spring.flyway.clean-disabled` 为 `true`。
- [ ] `application-local.example.yml` 只使用安全占位符，真实 `application-local.yml` 继续被 Git 忽略。
- [ ] 没有真实密码、Token、API Key、生产数据库地址或本机绝对路径。
- [ ] 没有 `schema.sql` 或 `data.sql`，没有与 Flyway 混用初始化机制。

## 4. Flyway 与六张表

- [ ] migration 只有 `db/migration/V1__create_mvp_schema.sql`，并已在人工授权的 MySQL 8.4.8 空测试库中成功执行。
- [ ] migration 建立 `app_user`、`company`、`job`、`job_application`、`interview`、`communication` 六张表。
- [ ] 所有表使用 InnoDB、`utf8mb4`、`BIGINT UNSIGNED AUTO_INCREMENT` 主键和 `DATETIME(6)` 审计时间。
- [ ] 外键使用 `ON DELETE RESTRICT` 和 `ON UPDATE RESTRICT`。
- [ ] Company、Job、JobApplication 直接保存非空 `user_id`。
- [ ] Job 使用 `(company_id, user_id) -> company(id, user_id)` 复合外键。
- [ ] JobApplication 使用 `(job_id, user_id) -> job(id, user_id)` 复合外键。
- [ ] Interview、Communication 只保存 `application_id`，通过 JobApplication 追溯用户。
- [ ] 索引和复合唯一技术约束与 `docs/database-design.md` 一致。
- [ ] 没有 Statistic 表、逻辑删除、业务状态或提前加入的名称、备注、薪资、结果等字段。
- [ ] migration 不插入用户、测试数据或其他业务数据。

## 5. MyBatis Mapper 与参数绑定

- [ ] 存在 AppUser Mapper 接口及 namespace 一致的 XML。
- [ ] Mapper XML 只使用 `#{}` 参数绑定，不存在 `${}` SQL 值拼接。
- [ ] AppUser Mapper 的最小查询只验证配置用户是否存在，不实现业务 CRUD。
- [ ] 测试专用 UserIsolationTestMapper 默认不执行，已在第二阶段真实 MySQL 验证中按显式开关运行。
- [ ] 后续读取、更新和删除方案必须同时限制对象 ID 与 `user_id` 或完整归属链，并检查影响行数。

## 6. CurrentUserProvider

- [ ] `ConfigurationCurrentUserProvider` 通过 Spring 属性 `jobtrack.current-user-id` 取得用户 ID。
- [ ] Provider 不直接调用 `System.getenv`。
- [ ] Provider 拒绝 0 和负数 ID。
- [ ] Provider 不读取 Header、请求参数、请求体、Cookie 或 `X-User-Id`。
- [ ] Provider 不使用 ThreadLocal，不记录用户 ID 或数据库凭据。
- [ ] CurrentUserService 从 Provider 取得 ID，并把同一个 ID 传给 Mapper。
- [ ] 当前实现能够在未来替换为认证上下文 Provider。
- [ ] 没有新增 Controller 或公开业务接口。

## 7. 无数据库测试

- [ ] `application-no-db-test.yml` 排除 DataSourceAutoConfiguration、FlywayAutoConfiguration 和 MybatisAutoConfiguration。
- [ ] no-db-test Profile 使用测试专用 `jobtrack.current-user-id: 1`。
- [ ] JobTrackApplicationTests 使用 no-db-test Profile 和测试专用 `@MockitoBean AppUserMapper`。
- [ ] HealthControllerTests 使用 no-db-test Profile，继续验证 HTTP 200、JSON Content-Type 和 `status=UP`。
- [ ] Provider 和 Service 单元测试不启动数据库上下文。
- [ ] `mvn clean test` 在未设置数据库环境变量时成功。
- [ ] 测试日志中没有 MySQL 连接、Hikari DataSource 启动或 Flyway migration。

## 8. MySQL 测试安全 Guard

- [ ] MySQL 集成测试默认 skipped，只有 `-Djobtrack.mysql.tests=true` 才能启用。
- [ ] Safety Guard 从 Spring Environment 读取最终有效的 `spring.datasource.*` 和 `jobtrack.current-user-id` 配置，不以原始 `JOBTRACK_TEST_*` 值代替最终安全判断；独立 Validator 只负责纯配置校验。
- [ ] Guard 在 ApplicationContext 刷新、DataSource 创建和 Flyway 执行前运行。
- [ ] 缺失 URL、非 `jdbc:mysql` 协议、非本机 host、非 `_test` 数据库、root 用户、空密码或非正数用户 ID 都被拒绝。
- [ ] `spring.flyway.clean-disabled` 的最终有效值必须为 `true`，缺失或被高优先级配置覆盖为 `false` 时 Guard 在连接数据库前拒绝启动。
- [ ] 显式开关为 true 但配置缺失或不安全时测试失败，而不是静默 skipped。
- [ ] Guard 单元测试不连接数据库。

## 9. 第二阶段真实 MySQL 验收

- [ ] 第二阶段已经取得独立人工授权并完成，测试数据库、账号、允许的创建和清理操作均在授权范围内。
- [ ] `mysql --version` 确认实际环境为 MySQL 8.4.8。
- [ ] 使用 localhost 上名称以 `_test` 结尾的专用空数据库和非 root 测试账号。
- [ ] 空库成功执行 V1 migration，`flyway_schema_history` 内容正确。
- [ ] 六张表、字段、索引、外键和复合归属约束在真实 MySQL 中与设计一致。
- [ ] 第二次 migration 不重复建表且校验成功。
- [ ] Job 不能关联其他用户的 Company，JobApplication 不能关联其他用户的 Job。
- [ ] currentUserId 从可信 Provider 经 Service 传入 Mapper。
- [ ] 用户 A 无法读取用户 B 的 Company 数据，空结果不泄露其他用户信息。
- [ ] 测试数据通过事务回滚或人工批准的清理步骤移除。
- [ ] `mvn -Djobtrack.mysql.tests=true clean test` 和打包成功。

## 10. 健康接口与启动边界

- [ ] `GET /api/health` 仍然无参数、返回 HTTP 200、JSON 和 `status=UP`。
- [ ] 健康 Controller 本身不查询数据库、不使用 Actuator 或统一响应封装。
- [ ] README 明确普通测试不需要 MySQL，但完整应用启动需要 DataSource 和 Flyway 成功。
- [ ] 第二阶段完整应用启动和健康接口调用已经实际验证，并使用 Ctrl+C 正常停止。

## 11. 文档一致性

- [ ] README 和版本计划将 v0.3.0 标记为已于 2026-07-16 发布，并保持 v0.4.0 及后续版本为“计划”。
- [ ] CHANGELOG 保留空的 `Unreleased`，并包含 `[0.3.0] - 2026-07-16` 正式版本区。
- [ ] README、CHANGELOG、版本计划、数据库设计和验收清单对能力、验证结果、发布日期与状态的描述一致。
- [ ] v0.2.0 的已发布状态没有被改动。

## 12. 最终安全和差异检查

- [ ] 已执行常见密码、Token、私钥、生产连接信息和本机绝对路径扫描。
- [ ] 已确认生产 Java 代码没有直接调用 `System.getenv`。
- [ ] 已确认 Mapper XML 不包含 `${}`。
- [ ] 已确认没有 `X-User-Id` 信任路径。
- [ ] 已审阅 `git status`、`git diff --name-only`、`git diff --stat` 和完整 `git diff`。
- [ ] `git diff --check` 通过，只包含批准范围内修改。
- [ ] 发布收尾只修改 `pom.xml`、README、CHANGELOG、版本计划和本清单，代码、Migration、Mapper、配置和数据库设计无变化。
- [ ] 发布收尾 Commit、Push 和 PR 仅在最终人工审查通过后执行；`v0.3.0` Tag 和 GitHub Release 仍等待单独人工指令。
