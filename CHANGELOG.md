# Changelog

本文件记录 JobTrack 的重要变更。版本号遵循语义化版本规则；尚未发布的内容保留在 `Unreleased`。

## [Unreleased]

### Added

- 增加六张 MVP 关系骨架表的 Flyway V1 migration，建立直接用户归属、复合归属外键、索引和 RESTRICT 删除边界。
- 增加基于 Spring 外部配置的可信 CurrentUserProvider、最小 AppUser Mapper 和用户上下文传递 Service。
- 增加无数据库测试 Profile、Provider 与 Service 单元测试，以及默认关闭的 MySQL 集成测试和测试数据库安全 Guard。
- 增加数据库设计、初始化边界和 MySQL 8.4.8 实际验证结果说明。

### Changed

- 项目开发版本更新为 `0.3.0-SNAPSHOT`，增加经人工确认的 MyBatis、MySQL Connector/J、Flyway Core 和 Flyway MySQL 依赖。
- README 和版本计划将 v0.3.0 标记为进行中，并更新 v0.3.0 人工验收清单。
- 完整应用配置改为从环境变量读取数据库连接和当前用户 ID；普通自动化测试保持不依赖数据库。
- 在人工授权的 MySQL 8.4.8 本机专用空测试库中完成 Flyway V1、六张表、字段、索引和七个外键验证。
- 完成用户隔离、两类跨用户复合外键拒绝、重复 migration、打包、完整应用启动和健康接口验证。

### Fixed

- 修复 MySQL JDBC 将 `DATETIME_PRECISION` 元数据返回为 `Long` 时直接转换为 `Integer` 导致的测试类型转换错误。

## [0.2.0] - 2026-07-15

v0.2.0 提供最小、可运行、可测试的 Spring Boot 单体工程骨架、技术健康检查、自动化测试、可执行 JAR 构建和使用文档，不包含数据库、MyBatis、业务 CRUD、登录或其他后续版本能力。

### Added

- 创建基于 Java 21、Spring Boot 3.5.16 和 Maven 的最小单体工程骨架，并支持生成可执行 JAR。
- 增加不访问数据库的 `GET /api/health` 技术健康检查接口。
- 增加 Spring 上下文加载测试和健康检查接口 MockMvc 测试。

### Changed

- 项目版本由 `0.2.0-SNAPSHOT` 更新为正式版本 `0.2.0`。
- README 增加经过验证的环境、构建、测试、启动和健康检查调用说明。
- README 和版本计划同步标记 v0.2.0 为已发布，并更新对应人工验收清单。

### Fixed

- 无。

## [0.1.0] - 2026-07-14

v0.1.0 只包含项目文档、开发规则、版本路线和验收基线，不包含 Java 业务代码、Spring Boot 工程骨架、生产依赖或数据库实现。

### Added

- 建立 JobTrack 项目定位、产品范围、业务模型和 README 项目说明。
- 建立长期适用的 Codex 开发规则与开发流程。
- 建立从 v0.1.0 至 v1.3.0 的完整版本路线及状态管理规则。
- 建立 v0.1.0 人工验收清单和发布前检查基线。

### Changed

- README 和版本计划同步标记 v0.1.0 为已发布。
- README 增加与详细版本计划一致的版本路线概览。

### Fixed

- 修正文档中版本路线被合并、遗漏及状态不一致的问题。
