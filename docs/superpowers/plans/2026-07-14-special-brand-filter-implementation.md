# 专项页品牌筛选 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让专项页默认展示全部品牌，并提供与搜索框同一行的 3/7 品牌筛选控件和宽圆角分类浮层。

**Architecture:** 将品牌筛选规则提取为无 Android 依赖的 UI 辅助函数，单元测试直接验证“全部品牌、分类、搜索叠加”三种规则。BrandPracticePage 仅负责筛选状态、3/7 布局和浮层展示，从该函数取得卡片列表。

**Tech Stack:** Kotlin、Jetpack Compose、JUnit 4、Android Gradle Plugin。

## Global Constraints

- 仅改动专项页及其直接的筛选辅助逻辑；不改动题库管理、随机练习、品牌资源和品牌卡片布局。
- 页面进入时默认使用“全部品牌”，且本次不持久化筛选状态。
- 左侧按钮必须始终显示“筛选品牌”，并占行宽 30%；右侧搜索框占 70%。
- 分类浮层第一项必须是“全部品牌”，并使用深色圆角、浅蓝描边和当前项高亮。

---

### Task 1: 可测试的专项品牌筛选规则

**Files:**
- Create: `app/src/main/java/com/example/carlogo/ui/BrandPracticeFilter.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/BrandPracticeFilterTest.kt`

**Interfaces:**
- Consumes: `com.example.carlogo.domain.model.Brand`。
- Produces: `ALL_BRANDS_FILTER_LABEL: String` 与 `filterPracticeBrands(brands: List<Brand>, selectedCategory: String?, query: String): List<Brand>`，其中 `null` 代表“全部品牌”。

- [x] **Step 1: Write the failing test**

```kotlin
@Test
fun `all brands filter returns every brand before a search`() {
    assertEquals(brands, filterPracticeBrands(brands, selectedCategory = null, query = ""))
}

@Test
fun `category filter keeps only matching brands`() {
    assertEquals(listOf(brands[1]), filterPracticeBrands(brands, selectedCategory = "新能源主流", query = ""))
}

@Test
fun `search is applied inside the selected category`() {
    assertEquals(listOf(brands[1]), filterPracticeBrands(brands, selectedCategory = "新能源主流", query = "tesla"))
}
```

- [x] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.BrandPracticeFilterTest`

Expected: FAIL because `filterPracticeBrands` and `ALL_BRANDS_FILTER_LABEL` do not yet exist.

- [x] **Step 3: Write minimal implementation**

```kotlin
package com.example.carlogo.ui

import com.example.carlogo.domain.model.Brand

internal const val ALL_BRANDS_FILTER_LABEL = "全部品牌"

internal fun filterPracticeBrands(
    brands: List<Brand>,
    selectedCategory: String?,
    query: String,
): List<Brand> = brands.filter { brand ->
    (selectedCategory == null || brand.category == selectedCategory) &&
        (brand.nameZh.contains(query, ignoreCase = true) || brand.nameEn.contains(query, ignoreCase = true))
}
```

- [x] **Step 4: Run test to verify it passes**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.BrandPracticeFilterTest`

Expected: PASS with three tests.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/carlogo/ui/BrandPracticeFilter.kt app/src/test/java/com/example/carlogo/ui/BrandPracticeFilterTest.kt
git commit -m "feat: add special brand filter rules"
```

### Task 2: 专项页 3/7 筛选与搜索布局

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:438-487`
- Modify: `app/src/main/java/com/example/carlogo/ui/BrandPracticeFilter.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/SpecialBrandFilterPresentationTest.kt`

**Interfaces:**
- Consumes: `ALL_BRANDS_FILTER_LABEL`、`filterPracticeBrands` 和 `SpecialBrandFilterPresentation`。
- Produces: 专项页默认全量列表、固定“筛选品牌”按钮、当前筛选提示、宽圆角分类浮层，以及按所选范围搜索的列表。

- [x] **Step 1: Add a failing layout-presentation test**

```kotlin
@Test
fun `special filter uses the approved 3 to 7 control ratio and wide menu`() {
    assertEquals(3f, SpecialBrandFilterPresentation.FILTER_WEIGHT)
    assertEquals(7f, SpecialBrandFilterPresentation.SEARCH_WEIGHT)
    assertEquals(220, SpecialBrandFilterPresentation.MENU_MIN_WIDTH_DP)
}
```

- [x] **Step 2: Run test to verify it fails**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.SpecialBrandFilterPresentationTest`

Expected: FAIL because `SpecialBrandFilterPresentation` does not yet exist.

- [x] **Step 3: Define the presentation constants and update the Compose page**

```kotlin
internal object SpecialBrandFilterPresentation {
    const val FILTER_WEIGHT = 3f
    const val SEARCH_WEIGHT = 7f
    const val MENU_MIN_WIDTH_DP = 220
}

var selectedCategory by remember { mutableStateOf<String?>(null) }
val filtered = filterPracticeBrands(brands, selectedCategory, query)

Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    Column(Modifier.weight(SpecialBrandFilterPresentation.FILTER_WEIGHT)) {
        Button(onClick = { categoryExpanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("筛选品牌")
        }
        Text("当前：${selectedCategory ?: ALL_BRANDS_FILTER_LABEL}")
    }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.weight(SpecialBrandFilterPresentation.SEARCH_WEIGHT),
        label = { Text("搜索品牌") },
    )
}
```

Add `ALL_BRANDS_FILTER_LABEL` as the first `DropdownMenuItem`, use a rounded `DropdownMenu` at least `MENU_MIN_WIDTH_DP.dp` wide with `CardNavy` background and `SoftBlue` border, and render the selected row with `ElectricBlue`.

- [x] **Step 4: Run focused test to verify it passes**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.ui.SpecialBrandFilterPresentationTest --tests com.example.carlogo.ui.BrandPracticeFilterTest`

Expected: PASS with all filter and presentation tests.

- [ ] **Step 5: Run complete verification**

Run: `.\\gradlew.bat --console=plain testDebugUnitTest assembleDebug`

Expected: `BUILD SUCCESSFUL` and a debug APK at `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/carlogo/ui/App.kt app/src/main/java/com/example/carlogo/ui/BrandPracticeFilter.kt app/src/test/java/com/example/carlogo/ui/SpecialBrandFilterPresentationTest.kt
git commit -m "feat: refine special brand filtering"
```
