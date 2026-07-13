# 猜车品牌

一款帮助记忆“车型与汽车品牌归属关系”的离线 Android 练习应用。

通过四选一选择题，你可以练习“某车型属于哪个品牌”与“某品牌旗下有哪些车型”，适合利用碎片时间熟悉传统燃油车、新能源汽车、豪华品牌、合资品牌和国产自主品牌。

## 主要功能

- **随机练习**：每轮固定 20 题，车型识别与品牌识别两种题型均衡出现。
- **品牌专项**：按品牌分类筛选后进行针对性练习，新能源品牌和母品牌/子品牌关系更易查找。
- **即时反馈**：选择答案后立即显示正确或错误状态，并展示当前答对数、总题数与正确率。
- **答题回顾**：已答题目支持“上一题”“下一题”按钮以及左右滑动回顾；不会跳转到尚未作答的题目。
- **错题复盘**：错误答案会记录在本机，可从错题本重新练习。
- **本地题库管理**：内置常见品牌与车型，支持新增、编辑自定义品牌和车型；车型可同时显示中文名和英文名。
- **离线使用**：题库、练习进度和错题记录都保存在手机本地，无需账号、后端服务或联网。

## 技术栈

- Kotlin
- Jetpack Compose
- Room（本地数据库）
- Gradle / Android SDK

## 下载与安装

1. 打开本仓库的 [GitHub Releases 发布页面](https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP/releases)。
2. 下载最新版本中的 APK 文件。
3. 将 APK 传到 Android 手机，或直接在手机浏览器下载。
4. 首次安装时，按系统提示允许当前来源安装未知应用，然后完成安装。

## 本地构建

在 Windows PowerShell 中进入项目根目录后执行：

```powershell
# 运行单元测试并生成调试 APK
.\gradlew.bat testDebugUnitTest assembleDebug

# 生成签名发布 APK（需要在本机配置签名信息）
.\gradlew.bat assembleRelease
```

调试 APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

发布 APK 输出位置：

```text
app/build/outputs/apk/release/app-release.apk
```

## 发布

源代码通过 GitHub 仓库维护；可安装的 APK 作为 GitHub Release 附件单独发布。
