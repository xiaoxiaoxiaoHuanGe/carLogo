# 管理页真实品牌图标 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将专项页筛选按钮改为“筛选汽车品牌”，并让题库管理列表与品牌详情使用与专项页一致的真实品牌图标样式。

**Architecture:** 在 `App.kt` 内抽取可复用的真实图标底座，由专项页和题库管理共同调用；内置品牌通过 `BrandLogoRegistry` 显示资源，自定义品牌在同款底座中回退到英文首字母。测试用展示常量锁定文案和管理页 48dp 图标规格。

**Tech Stack:** Kotlin、Jetpack Compose、JUnit 4、Android Gradle Plugin。

## Global Constraints

- 仅改专项页筛选文案、题库管理品牌列表与品牌详情标题的图标展示。
- 筛选按钮必须精确显示“筛选汽车品牌”。
- 管理页图标为 48dp，不增加品牌列表行高；使用浅色圆角方形真实图标底座。
- 专项页图标继续保持 64dp、14dp 圆角、8dp 内边距。
- 自定义品牌使用同款方形底座中的英文首字母回退。

---

### Task 1: 共享真实图标底座与管理页替换

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`
- Modify: `app/src/test/java/com/example/carlogo/ui/SpecialBrandFilterPresentationTest.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/ManagementBrandLogoPresentationTest.kt`

**Interfaces:**
- Consumes: `BrandLogoRegistry.resourceIdFor(brand.id)` 和 `Brand.displayText()`。
- Produces: `ManagementBrandLogoPresentation`、共享图标底座、两个管理页真实图标调用和更新后的筛选按钮文案。

- [ ] **Step 1: Write failing tests**

```kotlin
assertEquals("筛选汽车品牌", SpecialBrandFilterPresentation.BUTTON_LABEL)
assertEquals(48, ManagementBrandLogoPresentation.TILE_SIZE_DP)
assertEquals(14, ManagementBrandLogoPresentation.TILE_CORNER_RADIUS_DP)
assertEquals(6, ManagementBrandLogoPresentation.TILE_PADDING_DP)
```

- [ ] **Step 2: Verify RED**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.SpecialBrandFilterPresentationTest --tests com.example.carlogo.ui.ManagementBrandLogoPresentationTest`

Expected: FAIL because the button string differs and `ManagementBrandLogoPresentation` is absent.

- [ ] **Step 3: Implement the minimum production change**

```kotlin
internal object ManagementBrandLogoPresentation {
    const val TILE_SIZE_DP = 48
    const val TILE_CORNER_RADIUS_DP = 14
    const val TILE_PADDING_DP = 6
}
```

Make `SpecialBrandLogo` and a new `ManagementBrandLogo` delegate to one private real-logo tile composable. Replace the two management `BrandMark` calls with the 48dp management logo. Set `SpecialBrandFilterPresentation.BUTTON_LABEL` to `筛选汽车品牌`.

- [ ] **Step 4: Verify GREEN**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.SpecialBrandFilterPresentationTest --tests com.example.carlogo.ui.ManagementBrandLogoPresentationTest --tests com.example.carlogo.ui.BrandLogoRegistryTest`

Expected: PASS.

- [ ] **Step 5: Verify full build**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest assembleDebug`

Expected: `BUILD SUCCESSFUL`.
