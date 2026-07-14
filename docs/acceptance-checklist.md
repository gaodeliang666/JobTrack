# JobTrack v0.1.0 验收清单

> 本清单供人工验收使用。勾选表示验收人已检查，不因文件创建而自动视为通过。

## 1. Git 基线

- [ ] 当前分支为 `chore/v0.1-project-bootstrap`，不是 `main`。
- [ ] `HEAD`、`main`、`origin/main` 的预期基线关系未被本任务改变。
- [ ] 未执行暂存、Commit、Push、Merge、Rebase、Tag 或 GitHub Release。
- [ ] 工作区保持未提交状态，可供完整审阅。

## 2. 文件范围

- [ ] 仅创建或修改 `AGENTS.md`、`README.md`、`CHANGELOG.md`、`.gitignore` 和 `docs/` 下五个约定文档。
- [ ] 没有无关文件、临时文件或编辑器产物。
- [ ] 没有 Java 业务代码、Controller、Service 或 Mapper。
- [ ] 没有 `pom.xml`、生产依赖或其他构建配置。
- [ ] 没有数据库连接、迁移脚本或真实环境配置。

## 3. 内容一致性

- [ ] 项目名称统一为 JobTrack。
- [ ] README 明确当前没有 Spring Boot 工程骨架、没有可执行入口且暂时不可启动。
- [ ] README 未把公司、岗位、投递、面试、沟通或统计能力描述为已经实现。
- [ ] `AGENTS.md`、README 与开发流程对计划确认、分支、单任务和 Git 操作的规则一致。
- [ ] 产品范围、业务模型和版本计划的对象及边界一致。
- [ ] `CHANGELOG.md` 中 v0.1.0 的发布状态与 README 和版本计划一致。

## 4. 规则完整性

- [ ] `AGENTS.md` 包含项目定位、业务边界、技术栈允许范围和禁止主动引入项。
- [ ] 明确每次先制定计划、人工确认后修改，且一次只处理一个明确任务。
- [ ] 明确禁止直接在 `main` 开发。
- [ ] 明确 Controller、Service、Mapper 的职责边界。
- [ ] 明确 `userId` 数据隔离与完整归属链要求。
- [ ] 明确 SQL 参数绑定和动态字段白名单要求。
- [ ] 明确异常、日志、敏感信息、测试和人工验收规则。
- [ ] 明确 Branch、Commit、Push、Merge、Tag 和 Release 规则。

## 5. 安全与忽略规则

- [ ] 仓库中不存在密码、Token、API Key、私钥、Cookie 或生产连接凭据。
- [ ] 仓库文档中不存在开发者本机绝对路径。
- [ ] `.gitignore` 忽略常见构建产物、IDE 文件、本地环境和敏感配置。
- [ ] `.gitignore` 没有误排除 `.env.example`、`.env.template` 或 `application-example.*` 等安全示例配置。

## 6. 版本计划质量

- [ ] 是否完整包含 v0.1.0 至 v1.3.0 的所有约定版本：v0.1.0、v0.2.0、v0.3.0、v0.4.0、v0.5.0、v0.6.0、v0.7.0、v0.8.0、v0.9.0、v0.10.0、v0.11.0、v0.12.0、v1.0.0、v1.1.0、v1.2.0、v1.3.0。
- [ ] 每个版本是否具有目标和验收标准。
- [ ] v0.1.0 在 README、CHANGELOG 和版本计划中均为“已发布”，v0.2.0 及后续版本均为“计划”。
- [ ] 是否没有擅自合并、删除或提前完成版本。
- [ ] README 版本路线概览是否与 `docs/version-plan.md` 一致。

## 7. 最终命令检查

- [ ] 已审阅 `git status`。
- [ ] 已审阅 `git diff --stat`。
- [ ] 已审阅 `git diff`；若文件全部未跟踪，已理解普通 diff 不展示其内容。
- [ ] 已审阅 `git log --oneline --decorate --all -5`。
- [ ] 人工验收结论和后续 Git 指令已明确给出。
