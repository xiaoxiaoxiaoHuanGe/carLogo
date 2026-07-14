# 专项页排版对齐 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 统一专项页筛选区和品牌卡片的垂直排版，避免分类名称与品牌名称长度造成控件和小字位置跳动。

**Architecture:** 用展示常量锁定副标题、当前分类提示和卡片顶部信息区尺寸；Compose 页面只重排专项页，不改筛选逻辑、品牌资源或其他页面。

**Tech Stack:** Kotlin、Jetpack Compose、JUnit 4。

## Global Constraints

- 副标题精确显示“选择品牌，针对练习”。
- 当前分类提示与副标题同一行，单行省略；筛选按钮下方不得再有提示。
- 筛选按钮与搜索框均为 56dp 并上下对齐。
- 卡片顶部固定为 64dp 高：左侧品牌图标，右侧车型数量和分类。
- 品牌名称独占卡片下方空间，最多两行；卡片总高度仍为 180dp。
- 仅修改专项页及其展示测试。

---

### Task 1: 专项页筛选区与品牌卡片布局

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`
- Modify: `app/src/main/java/com/example/carlogo/ui/BrandPracticeFilter.kt`
- Modify: `app/src/test/java/com/example/carlogo/ui/SpecialBrandFilterPresentationTest.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/SpecialBrandCardPresentationTest.kt`

- [ ] 写失败测试，断言副标题、提示前缀、64dp 信息区和品牌名称两行。
- [ ] 运行两个聚焦测试并确认因新常量不存在而失败。
- [ ] 最小实现常量并重排 `BrandPracticePage` 和 `BrandCard`。
- [ ] 运行聚焦测试确认通过。
- [ ] 运行 `.\\gradlew.bat --console=plain testDebugUnitTest assembleDebug`。
