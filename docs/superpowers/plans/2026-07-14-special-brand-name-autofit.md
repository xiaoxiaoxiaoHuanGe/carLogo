# 专项品牌名称自适应 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在固定 180dp 卡片中让短品牌名保持原字号、长品牌名自动缩小并完整显示。

**Architecture:** 保留顶部 64dp 图标信息区，将剩余高度交给一个基于 Compose 实际测量结果选择最大可用字号的品牌名称组件。字号从 1.0 逐级尝试到 0.5，最多四行，不使用省略号。

**Tech Stack:** Kotlin、Jetpack Compose TextMeasurer、JUnit 4。

## Global Constraints

- 卡片高度维持 180dp，顶部图标信息区维持 64dp。
- 短名称保持 `titleMedium` 原字号。
- 长名称在剩余区域中选择能够完整显示的最大字号，最多四行。
- 品牌名称不显示省略号。
- 仅修改专项品牌名称布局及对应测试。

---

### Task 1: 品牌名称自动适配

**Files:**
- Create: `app/src/main/java/com/example/carlogo/ui/BrandNameAutoFit.kt`
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`
- Modify: `app/src/test/java/com/example/carlogo/ui/SpecialBrandCardPresentationTest.kt`

- [ ] 先测试候选字号选择规则和四行上限并确认失败。
- [ ] 实现纯 Kotlin 选择规则。
- [ ] 使用 `rememberTextMeasurer` 测量每个候选字号并选择第一个无溢出的字号。
- [ ] 使用 `TextOverflow.Clip`，取消品牌名称省略号。
- [ ] 运行聚焦测试和完整 Debug 构建。
