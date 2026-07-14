# 猜车品牌（CarLogo）项目交接文档

> 用途：此文档面向未来的维护者或新的 Codex 任务对话。即使没有此前聊天上下文，也应先阅读本文，再对项目做任何修改。

## 1. 项目概述

### 背景与目标

“猜车品牌”是一个离线 Android 答题 App，用四选一选择题帮助用户记忆车型与汽车品牌的归属关系。它覆盖传统燃油车、新能源、豪华、合资与国产自主品牌；用户可做随机练习、品牌专项练习和错题复盘，也能在本地维护自定义品牌及车型。

### 当前状态（已核验）

| 项目项 | 当前值 |
| --- | --- |
| 工作目录 | `D:\BianCheng\usuallyAPP` |
| Android 包名 / namespace | `com.example.carlogo` |
| 项目名 | `CarLogo` |
| GitHub 仓库 | [xiaoxiaoxiaoHuanGe/usuallyAPP](https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP) |
| 默认分支 | `main` |
| 最新已推送提交 | `9c1ebed0599a612b986952bcceb07cad76eef3ba` — `feat: initial Guess Car Brand release` |
| 已发布版本 | [v1.0.0](https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP/releases/tag/v1.0.0) |
| Release APK | `carLogo.apk`，9,680,071 字节；GitHub Release 附件，不在 Git 中 |
| 应用内部版本 | `versionCode = 1`，`versionName = "1.0"` |
| 数据库 | Room `AppDatabase`，schema version `2` |
| 网络 / 后端 | 无；题库、进度和错题均保存到设备本地 |

最后一次在本机验证的构建命令为：

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

该命令已成功完成。发布 APK 也曾通过 `apksigner verify --verbose` 验证，使用 1 个 v2 签名者。

## 2. 已完成的工作

以下均为已经实现或已发布的能力，不是规划项。

1. **完整离线题库**：内置 40 个品牌，按“新能源主流 / 新能源新势力 / 豪华品牌 / 合资与国际品牌 / 国产自主与高端”五个互斥分类组织；绝大多数品牌具有至少 5 个车型。比亚迪旗下仰望、方程豹等品牌保留可选子品牌及母品牌标签。
2. **两类随机题目**：`QuizGenerator` 每局生成 20 题，其中 10 题“品牌 → 车型”、10 题“车型 → 品牌”；每题四选一，包含跨品牌干扰项，并按“题干 + 正确答案”去重。
3. **专项练习与分类筛选**：品牌专项页按分类展示，点击品牌后只围绕该品牌出题；首页“品牌专项”和“错题复盘”入口均已有页面跳转逻辑。
4. **纯文本答题界面与统一视觉风格**：答题选项与题目正文不再展示车型图片；界面采用深蓝科技风；标题和答题文字统一使用白色系，答对后字体仍保持白色。
5. **答题反馈与过渡动画**：选项选择后即时高亮正确/错误状态；正确且非最后一题时会经过短过渡自动进入下一题，题目切换使用 Compose 动画。
6. **答题回顾导航**：已答题可以通过“上一题”“下一题”和左右滑动回顾；第一题不显示上一题、最后一题不显示下一题，导航规则由 `QuizSessionController` 单独管理并有单元测试。
7. **稳定的题目布局**：题干容器固定高度；测量到三行及以上时，题干字体缩小为默认大小的 80%，仅影响题目区域，降低切题时页面跳动。
8. **本地数据持久化与管理**：Room 保存品牌、车型、练习记录、答案和错题；支持新增、编辑、删除自定义品牌/车型，车型可保存中英文名；数据库已有 `1 → 2` 迁移以支持母品牌字段。
9. **应用图标与发布签名**：已使用用户提供的车+问号图作为多密度启动图标；已在本机私有密钥库上生成签名 Release APK，并发布到 GitHub。
10. **项目发布资产**：已创建根目录 `README.md`、GitHub 仓库、`main` 分支和首个公开 Release `v1.0.0`。

## 3. 待办事项

当前没有已确认、可复现的阻塞性 Bug；下表是建议优先处理的维护项和已知验证空缺。

| 优先级 | 事项 / 问题 | 预计工时 | 建议处理与验收方式 |
| --- | --- | ---: | --- |
| P0 | **真机安装正式版回归**：在真我 GT5 Pro 上卸载 Debug 测试版后，安装 GitHub Release 的 `carLogo.apk`，检查随机练习、专项、错题、左右滑动和本地管理。 | 1.5 小时 | 全流程无崩溃；Release 可冷启动；至少完成一局 20 题并能查看错题。 |
| P0 | **确定 Debug → Release 数据迁移策略**：Debug APK 与 Release APK 签名不同，不能直接覆盖；卸载 Debug 会清空其本地 Room 数据。 | 1 小时 | 明确接受“清空测试数据”，或为 Debug 使用不同 `applicationIdSuffix` 以便与 Release 并存。 |
| P1 | **下一版本统一版本号**：当前 Git 标签为 `v1.0.0`，但 `app/build.gradle.kts` 中 `versionName` 仍是 `1.0`。 | 0.5 小时 | 下次发布前将 `versionCode` 增至 `2`，并将 `versionName` 设为例如 `1.0.1`；重新构建、签名、发布对应 Tag。 |
| P1 | **补充 UI/真机测试**：现有测试主要覆盖题库、出题、导航和题干缩放逻辑；缺少 Compose UI 自动化测试与 Release 真机回归。 | 6 小时 | 为答题按钮可见性、专项入口、错题入口、手势导航增加 Compose 测试；在 API 26+ 真机/模拟器运行。 |
| P2 | **题库数据审校与更新流程**：车型与品牌归属是手工维护数据，车型停售、改名、子品牌关系可能随时间变化。 | 4–8 小时 / 次 | 审核 `SeedData.kt`，为每次数据更新记录来源和日期；重点复核新能源品牌、子品牌和中英文名。 |
| P2 | **减少单一 UI 文件复杂度**：`ui/App.kt` 包含首页、专项、管理、记录、错题和答题等大量 Composable。 | 4 小时 | 按 screen/component 拆分文件，保持现有回调和导航行为不变；执行现有单元测试与真机回归。 |

## 4. 技术栈与依赖

### 平台与构建环境

| 项目 | 版本 / 要求 |
| --- | --- |
| 语言 | Kotlin `2.2.10` |
| Android Gradle Plugin | `9.2.1` |
| Gradle Wrapper | `9.4.1` |
| Gradle JVM toolchain | JDK `21`（见 `gradle/gradle-daemon-jvm.properties`） |
| 当前本机 `java` 命令 | JDK `23`；构建曾成功，Gradle daemon 仍声明 JDK 21 toolchain |
| Java 编译目标 | Java `11`（`sourceCompatibility` / `targetCompatibility`） |
| compile / target SDK | Android API `36.1` / `36` |
| min SDK | API `26`（Android 8.0） |
| UI | Jetpack Compose，Compose BOM `2025.02.00`，Material 3 |
| 数据库 | Room `2.7.1` + KSP `2.2.10-2.0.2` |
| 导航 | Navigation Compose `2.8.9` |
| 图片库 | Coil Compose `2.7.0`；答题页当前不展示图片，管理页仍可保存用户选取的图片引用 |
| 生命周期 / Activity | Lifecycle Runtime KTX `2.8.7`、Activity Compose `1.10.1` |
| AndroidX 基础库 | Core KTX `1.15.0` |
| 测试 | JUnit `4.13.2`、AndroidX JUnit `1.1.5`、Espresso `3.5.1`、Compose UI Test（由 BOM 管理） |

依赖版本的唯一维护位置是：[gradle/libs.versions.toml](gradle/libs.versions.toml)。Compose UI、Material3、Tooling 与 Compose Test 未单独指定版本，均受 Compose BOM `2025.02.00` 统一约束。

### 本地环境配置

1. 安装 Android Studio（含 Android SDK Platform 36、Build Tools、Platform Tools）和 JDK 21。
2. 让 `local.properties` 含有本机 `sdk.dir`。该文件已经被 Git 忽略，不应提交。
3. 首次运行 Gradle 需要访问 Google/Maven Central/Gradle 服务以下载依赖和 Wrapper。
4. 调试构建：`.\gradlew.bat testDebugUnitTest assembleDebug`。
5. 签名发布构建：在项目根目录创建本机私有 `keystore.properties` 后执行 `.\gradlew.bat assembleRelease`。该文件应包含密钥库路径、别名和口令，但**不要**将其内容写入聊天、文档或 Git。

## 5. 文档与资源

### 关键代码入口

| 资源 | 路径 / 链接 | 用途 |
| --- | --- | --- |
| 应用启动入口 | [app/src/main/java/com/example/carlogo/MainActivity.kt](app/src/main/java/com/example/carlogo/MainActivity.kt) | Android `ComponentActivity` 与 Compose 根入口。 |
| 全局 UI / 页面导航 | [app/src/main/java/com/example/carlogo/ui/App.kt](app/src/main/java/com/example/carlogo/ui/App.kt) | 首页、专项、管理、记录、错题、答题页面和主题化组件。 |
| 出题算法 | [app/src/main/java/com/example/carlogo/domain/QuizGenerator.kt](app/src/main/java/com/example/carlogo/domain/QuizGenerator.kt) | 20 题、两类题型、干扰项与去重规则。 |
| 答题状态与回顾 | [app/src/main/java/com/example/carlogo/domain/QuizSessionController.kt](app/src/main/java/com/example/carlogo/domain/QuizSessionController.kt) | 答题锁定、得分、上一题/下一题可用性。 |
| 题库数据 | [app/src/main/java/com/example/carlogo/data/SeedData.kt](app/src/main/java/com/example/carlogo/data/SeedData.kt) | 内置品牌、车型、分类、母品牌标签。 |
| 本地仓库 | [app/src/main/java/com/example/carlogo/data/CarRepository.kt](app/src/main/java/com/example/carlogo/data/CarRepository.kt) | 题库初始化、CRUD、练习和错题存取。 |
| Room 数据库 | [app/src/main/java/com/example/carlogo/data/local/AppDatabase.kt](app/src/main/java/com/example/carlogo/data/local/AppDatabase.kt) | 数据库 schema 与迁移。 |
| 单元测试 | [app/src/test/java/com/example/carlogo](app/src/test/java/com/example/carlogo) | 出题、题库、答题导航、题干布局的测试。 |

### 文档与发布资源

| 资源 | 路径 / 链接 | 访问说明 |
| --- | --- | --- |
| 项目 README | [README.md](README.md) | 对 GitHub 访客说明功能、安装与构建。 |
| 初始 MVP 实施计划 | [docs/superpowers/plans/2026-07-13-guess-car-brand-mvp.md](docs/superpowers/plans/2026-07-13-guess-car-brand-mvp.md) | 记录最初功能范围与实现思路。 |
| GitHub 仓库 | [xiaoxiaoxiaoHuanGe/usuallyAPP](https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP) | 需要仓库写权限才能推送、创建 Tag 和 Release。 |
| 已发布版本 | [v1.0.0 Release](https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP/releases/tag/v1.0.0) | 公开下载 `carLogo.apk`。 |
| 本机 Release 输出 | `app/build/outputs/apk/release/app-release.apk` | 仅在本机构建后存在；目录被 Git 忽略。 |
| 本机签名配置 | `keystore.properties`、`%USERPROFILE%\AndroidKeys\car-logo-release.jks` | 仅本机可访问；不得提交、上传或透露密码。 |

### 当前工作区注意

生成本交接文档时，工作区存在以下**未由本任务创建**的 Android Studio 配置改动：`.idea/misc.xml` 被修改，且 `.idea/.name`、`AndroidProjectSystem.xml`、`compiler.xml`、`deploymentTargetSelector.xml`、`gradle.xml`、`inspectionProfiles/`、`runConfigurations.xml` 未跟踪。接手时先运行 `git status --short`，确认是否保留这些 IDE 个人配置；不要在不确认的情况下 `git add -A`。

## 6. 风险与注意事项

1. **Release 签名不可丢失（高）**：未来版本必须用同一个 `car-logo-release.jks` 与相同 alias 签名，Android 才能覆盖安装已发布的 `carLogo.apk`。丢失密钥或口令将无法为现有包名发布可升级版本。
2. **Debug 与 Release 不能直接互相覆盖（高）**：两者通常使用不同签名且包名相同。测试正式版前若卸载 Debug，Room 中的题库、记录、错题和自定义数据会一并被清除。
3. **版本号必须递增（高）**：下一次公开 APK 必须提高 `versionCode`；相同或更低的 `versionCode` 不能覆盖安装。建议同步让 Git Tag、`versionName` 和 Release 标题一致。
4. **题库是本地手工数据（中）**：`SeedData.kt` 是唯一内置题库来源。它并未从云端自动更新；修改品牌、车型或分类需复核正确性，并运行 `SeedDataTest` / `QuizGeneratorTest`。
5. **数据库迁移要求同步维护（中）**：当前只有 `MIGRATION_1_2`。若修改 Room entity、表或字段，必须提高 `@Database` 版本并加入迁移；否则已有用户升级会崩溃或丢数据。
6. **当前 UI 文件较大（中）**：`App.kt` 集中多个页面与手势逻辑。改动前应先阅读 `QuizSessionControllerTest`，避免破坏“已答题回顾但不能越过未答题”的边界。
7. **构建网络依赖（低）**：新的电脑首次构建会下载 Gradle 9.4.1 和依赖；网络受限时构建可能失败。先检查网络、SDK 和 JDK toolchain。
8. **不要提交个人文件（高）**：`keystore.properties`、`local.properties`、`*.jks`、`*.keystore`、`build/` 已在 `.gitignore` 中排除。提交前仍应使用 `git status` 和 `git check-ignore -v` 再确认一次。

## 7. 新任务对话启动提示

将下列内容粘贴到新的任务对话中，即可让接手者从正确上下文开始：

```text
请接手 Windows 项目 D:\BianCheng\usuallyAPP（GitHub: https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP）。
首先完整阅读项目根目录 handoff.md 与 README.md；不要仅凭聊天上下文推测。
随后运行 git status --short，保留并报告任何已有的未提交文件，尤其是 .idea 配置；不要执行 git reset、git checkout 或 git add -A。
应用是 Kotlin + Jetpack Compose + Room 的离线“猜车品牌”App，当前公开版本为 v1.0.0。需要改代码时，先定位对应源码和单元测试，再用 .\gradlew.bat testDebugUnitTest assembleDebug 验证。若要发布新版，必须使用本机私有 keystore.properties 配置的同一签名密钥、递增 versionCode，并将 APK 作为 GitHub Release 附件上传，不要把密钥或构建产物提交到 Git。
请先给出当前仓库状态、相关代码入口和实施计划，再执行改动。
```

## 8. 最短恢复流程

```powershell
cd D:\BianCheng\usuallyAPP
git status --short
.\gradlew.bat testDebugUnitTest assembleDebug
```

若要安装本地调试版，使用 `app/build/outputs/apk/debug/app-debug.apk`。若要测试公开正式版，下载 GitHub Release 的 `carLogo.apk`；因签名不同，可能需要先卸载 Debug 版，且会清除该应用的本地数据。
