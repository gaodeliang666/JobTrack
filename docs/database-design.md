# JobTrack 数据库设计

## 1. 适用版本与边界

本文档描述 JobTrack v0.3.0 的 MySQL 关系骨架。当前版本只建立用户归属、对象关系、索引、外键和审计时间字段，不实现公司、岗位、投递、面试或沟通的业务 CRUD。

数据库基线使用 MySQL 8 兼容语法，实际 MySQL 8.4.8 验证必须在单独获得数据库操作授权后执行。第一阶段只编写 migration，不连接数据库，也不宣称 migration 已经执行成功。

## 2. 初始化机制

- 只使用 Flyway migration，不使用 `schema.sql` 或 `data.sql`。
- 首个 migration 为 `src/main/resources/db/migration/V1__create_mvp_schema.sql`。
- migration 不创建数据库、数据库账号、用户数据或测试数据。
- `spring.flyway.clean-disabled` 保持为 `true`。
- 已经应用的 migration 不允许修改；后续结构变化增加新的版本化 migration。

## 3. 通用约定

- 存储引擎：InnoDB。
- 字符集：`utf8mb4`。
- 排序规则：`utf8mb4_0900_ai_ci`。
- 主键：`BIGINT UNSIGNED AUTO_INCREMENT`。
- 时间字段：`DATETIME(6)`，包含 `created_at` 和 `updated_at`。
- 外键删除与更新策略：`RESTRICT`。
- 不使用逻辑删除。
- 不建立 Statistic 表，统计保持当前用户范围内的派生结果。
- 不提前增加名称、状态、备注、薪资、结果、登录凭据等后续版本字段。

## 4. 表关系

```text
User 1:N Company
User 1:N Job
Company 1:N Job
User 1:N JobApplication
Job 1:N JobApplication
JobApplication 1:N Interview
JobApplication 1:N Communication
```

Company、Job 和 JobApplication 直接保存 `user_id`。Interview 和 Communication 不冗余 `user_id`，必须通过 JobApplication 追溯到唯一 User。

## 5. 表结构

### 5.1 app_user

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

`app_user` 只是当前用户归属根节点。登录名、邮箱、密码和 Token 留到登录鉴权版本确认。

### 5.2 company

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| user_id | BIGINT UNSIGNED | 否 | 无默认值 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

- 外键：`user_id -> app_user.id`。
- 唯一技术约束：`(id, user_id)`，供 Job 的复合外键引用。
- 普通索引：`(user_id, id)`。
- 公司名称和重复规则留到 v0.4.0。

### 5.3 job

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| user_id | BIGINT UNSIGNED | 否 | 无默认值 |
| company_id | BIGINT UNSIGNED | 否 | 无默认值 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

- 外键：`user_id -> app_user.id`。
- 复合外键：`(company_id, user_id) -> company(id, user_id)`。
- 唯一技术约束：`(id, user_id)`，供 JobApplication 的复合外键引用。
- 普通索引：`(user_id, id)`、`(company_id, user_id)`。
- 岗位名称、链接、地点和薪资留到 v0.5.0。

### 5.4 job_application

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| user_id | BIGINT UNSIGNED | 否 | 无默认值 |
| job_id | BIGINT UNSIGNED | 否 | 无默认值 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

- 外键：`user_id -> app_user.id`。
- 复合外键：`(job_id, user_id) -> job(id, user_id)`。
- 普通索引：`(user_id, id)`、`(job_id, user_id)`。
- 是否允许同一岗位多次投递，以及状态和时间字段，留到 v0.6.0。

### 5.5 interview

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| application_id | BIGINT UNSIGNED | 否 | 无默认值 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

- 外键：`application_id -> job_application.id`。
- 普通索引：`(application_id, id)`。
- 不保存冗余 `user_id`；读取或修改时必须通过 JobApplication 的 `user_id` 验证归属。
- 面试时间、轮次、形式、结果和复盘留到 v0.7.0。

### 5.6 communication

| 字段 | 类型 | NULL | 默认值或属性 |
| --- | --- | --- | --- |
| id | BIGINT UNSIGNED | 否 | AUTO_INCREMENT，主键 |
| application_id | BIGINT UNSIGNED | 否 | 无默认值 |
| created_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6) |
| updated_at | DATETIME(6) | 否 | CURRENT_TIMESTAMP(6)，更新时刷新 |

- 外键：`application_id -> job_application.id`。
- 普通索引：`(application_id, id)`。
- 不保存冗余 `user_id`；读取或修改时必须通过 JobApplication 的 `user_id` 验证归属。
- 沟通时间、渠道、联系人和摘要留到 v0.8.0。

## 6. 数据库约束与应用校验

外键保证记录引用存在，复合外键进一步阻止 Job 关联其他用户的 Company，以及 JobApplication 关联其他用户的 Job。应用层仍必须从可信 CurrentUserProvider 取得用户 ID，在查询、更新和删除 SQL 中同时限制对象 ID 和用户归属，并检查更新或删除的影响行数。

Interview 和 Communication 的用户隔离必须通过关联 JobApplication 实现，不能只按子对象 ID 查询。

## 7. 配置与敏感信息

完整应用启动需要 `JOBTRACK_DB_URL`、`JOBTRACK_DB_USERNAME`、`JOBTRACK_DB_PASSWORD` 和 `JOBTRACK_CURRENT_USER_ID`。这些值只能由 Spring 外部配置读取，不得提交真实密码、生产数据库地址或本机绝对路径。

普通自动化测试使用 `no-db-test` Profile，排除 DataSource、Flyway 和 MyBatis 自动配置，不需要任何数据库变量。真实 MySQL 测试使用独立 Profile、安全 Guard 和显式开关，并且只有在单独获得数据库操作授权后才能执行。

## 8. 后续 MySQL 验证

后续第二阶段至少验证：

1. MySQL 8.4.8 专用空测试库能够应用 V1 migration；
2. 六张表、字段、索引和外键与本文档一致；
3. 第二次启动不会重复执行 migration；
4. 跨用户 Company/Job 和 Job/JobApplication 关联被拒绝；
5. Mapper 参数绑定和用户隔离测试通过；
6. 测试数据通过事务回滚或人工批准的清理步骤移除。

上述内容在第一阶段均不执行，也不得描述为已经验证。
