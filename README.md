<h1 align="center">猜车品牌 · carLogo</h1>

<p align="center">用四选一练习，记住车型与汽车品牌的关系。</p>

<p align="center">
  <a href="https://github.com/xiaoxiaoxiaoHuanGe/carLogo/releases/latest"><img alt="Release" src="https://img.shields.io/github/v/release/xiaoxiaoxiaoHuanGe/carLogo?style=flat-square"></a>
  <img alt="Android 8.0+" src="https://img.shields.io/badge/Android-8.0%2B-526273?style=flat-square">
</p>

<p align="center">
  <a href="https://github.com/xiaoxiaoxiaoHuanGe/carLogo/releases/latest">下载 APK</a> ·
  <a href="#开始练习">开始练习</a> ·
  <a href="#本地构建">本地构建</a> ·
  <a href="https://github.com/xiaoxiaoxiaoHuanGe/carLogo/issues">反馈问题</a>
</p>

---

猜车品牌是一款离线 Android 学习应用，围绕“车型属于哪个品牌”和“品牌旗下有哪些车型”出题。
内置题库涵盖传统燃油车、新能源、豪华、合资和国产自主品牌，适合用零碎时间练习。

## 可以做什么

| 功能 | 使用方式 |
| --- | --- |
| 🎲 随机练习 | 默认 10 题，可调整为 5–50 题；两种题型随机出现 |
| 🚘 品牌专项 | 从品牌中心按分类或母品牌关系查找，进入专项练习 |
| ✅ 答题回顾 | 即时显示对错和正确率，使用按钮或左右滑动回顾已答题目 |
| 📝 错题复盘 | 查看本机错题、错误次数，并重新练习 |
| 📅 学习记录 | 查看每日目标、本周概览和近 7 天学习情况 |
| 🗂️ 题库管理 | 新增、编辑和删除自定义品牌及车型，支持中英文名称 |

品牌专项每种题型最多 10 题；可用车型较少时，实际题数会减少。

## 开始练习

1. 从 [Releases](https://github.com/xiaoxiaoxiaoHuanGe/carLogo/releases/latest) 下载 `app-release.apk`。
2. 在 **Android 8.0 或更高版本**安装，按系统提示允许当前来源安装应用。
3. 打开“随机练习”设置题数，或从“品牌专项”选择品牌开始答题。
4. 完成后查看结果；需要复习时进入错题本或学习记录。

应用无需账号或后端。题库、已完成的练习记录和错题保存在手机本地；当前答题状态在内存中，关闭应用后不会续接未完成的一轮。

## 本地构建

需要 Android SDK 和项目配置的 JDK 21。项目使用 Kotlin、Jetpack Compose、Room 和 Gradle Wrapper；SDK 配置见 `app/build.gradle.kts`。

```powershell
git clone https://github.com/xiaoxiaoxiaoHuanGe/carLogo.git
cd carLogo
.\gradlew.bat testDebugUnitTest assembleDebug
```

调试 APK：`app/build/outputs/apk/debug/app-debug.apk`。
Linux / macOS 使用 `./gradlew` 执行相同任务。

<details>
<summary>构建发布 APK</summary>

在本机创建 `keystore.properties`，填写 `storeFile`、`storePassword`、`keyAlias` 和 `keyPassword`。
签名文件与密码只保存在本机，按 `app/build.gradle.kts` 的配置提供路径。

```powershell
.\gradlew.bat assembleRelease
```

配置签名后的输出：`app/build/outputs/apk/release/app-release.apk`。
源码在本仓库维护，安装包通过 GitHub Releases 单独发布。

</details>

## 来源与许可

项目由 [xiaoxiaoxiaoHuanGe](https://github.com/xiaoxiaoxiaoHuanGe) 维护。题库整理见 [品牌车型资料](sucai/汽车品牌车型列表(含logo).md)。
汽车品牌名称与标识的权利归各自权利人；第三方依赖遵循各自许可证。

当前仓库未提供源码许可证，公开可读不等于已授予任意使用、修改或分发的许可。
