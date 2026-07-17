# 品牌中心与首页命名调整 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将“练习 / 专项 / 题库管理”重组为“首页 / 品牌”流程，且保留自定义品牌与车型维护。

**Architecture:** `CarLogoApp` 继续持有已加载的品牌和练习会话；`MainShell` 管理品牌详情选择状态。品牌列表合并新增品牌、筛选与进入详情的能力；品牌详情合并车型展示、维护和专项练习。

**Tech Stack:** Kotlin、Jetpack Compose、Room、JUnit4、Gradle。

## Global Constraints

- 导航名称必须为“首页 / 品牌 / 我的”。
- 首页快捷卡必须显示“学习记录”并打开既有学习记录页。
- 品牌详情必须展示全部车型、提供“开始练习”，并保留自定义品牌和车型的增删改。
- 内置品牌与车型仍只读。
- 所有用户可见“错题本”改为“错题记录”。

---

### Task 1: 建立品牌中心文案的回归测试

**Files:**
- Create: `app/src/test/java/com/example/carlogo/ui/BrandHubPresentationTest.kt`
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:130-160`

**Interfaces:**
- Produces: `internal object BrandHubPresentation`，供 Compose 页面使用的中文文案常量。

- [ ] **Step 1: 写入失败测试**

```kotlin
@Test fun `brand hub uses approved labels`() {
    assertEquals("首页", BrandHubPresentation.HOME_NAV_LABEL)
    assertEquals("品牌", BrandHubPresentation.BRAND_NAV_LABEL)
    assertEquals("学习记录", BrandHubPresentation.LEARNING_RECORD_SHORTCUT_LABEL)
    assertEquals("错题记录", BrandHubPresentation.MISTAKE_RECORD_LABEL)
    assertEquals("开始练习", BrandHubPresentation.START_PRACTICE_LABEL)
    assertEquals("全部车型", BrandHubPresentation.ALL_MODELS_LABEL)
}
```

- [ ] **Step 2: 运行测试确认因 `BrandHubPresentation` 缺失而失败**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandHubPresentationTest`

Expected: FAIL，编译错误指出 `BrandHubPresentation` 未解析。

- [ ] **Step 3: 实现最小文案模型**

```kotlin
internal object BrandHubPresentation {
    const val HOME_NAV_LABEL = "首页"
    const val BRAND_NAV_LABEL = "品牌"
    const val LEARNING_RECORD_SHORTCUT_LABEL = "学习记录"
    const val MISTAKE_RECORD_LABEL = "错题记录"
    const val START_PRACTICE_LABEL = "开始练习"
    const val ALL_MODELS_LABEL = "全部车型"
}
```

- [ ] **Step 4: 重新运行测试确认通过**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandHubPresentationTest`

Expected: PASS，测试通过。

- [ ] **Step 5: 提交该测试与最小实现**

```bash
git add app/src/main/java/com/example/carlogo/ui/App.kt app/src/test/java/com/example/carlogo/ui/BrandHubPresentationTest.kt
git commit -m "test: define brand hub presentation"
```

### Task 2: 将导航、首页快捷入口和品牌列表路由改为品牌中心

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:270-730`
- Modify: `app/src/test/java/com/example/carlogo/ui/BrandPracticeFilterTest.kt`

**Interfaces:**
- Consumes: `BrandHubPresentation`、`filterPracticeBrands`、`QuizMode.BrandPractice`。
- Produces: `selectedBrandId`，供品牌详情与专项练习复用。

- [ ] **Step 1: 扩展失败测试，声明选中品牌的专项模式**

```kotlin
@Test fun `selected brand id creates the matching practice mode`() {
    assertEquals(QuizMode.BrandPractice("tesla"), QuizMode.BrandPractice("tesla"))
}
```

- [ ] **Step 2: 运行测试确认缺少 `QuizMode` 导入而失败**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandPracticeFilterTest`

Expected: FAIL，测试文件出现 `QuizMode` 未解析错误。

- [ ] **Step 3: 修改页面状态、导航与快捷卡**

```kotlin
var selectedBrandId by remember { mutableStateOf<String?>(null) }
TechNavItem(BrandHubPresentation.HOME_NAV_LABEL, "◉", selectedTab == "random") { onOpenPage("random") }
TechNavItem(BrandHubPresentation.BRAND_NAV_LABEL, "◇", selectedTab == "special") { onOpenPage("special") }
```

将首页“品牌专项”卡替换为“学习记录”卡并路由到 `history`；将品牌卡点击由直接 `onStart` 改为设置 `selectedBrandId` 后进入 `brandDetail`。删除设置页的“题库管理”入口和 `manage` / `manageDetail` 独立路由。

- [ ] **Step 4: 修正导入并运行测试确认通过**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandPracticeFilterTest --tests com.example.carlogo.ui.BrandHubPresentationTest`

Expected: PASS。

- [ ] **Step 5: 提交导航和路由变更**

```bash
git add app/src/main/java/com/example/carlogo/ui/App.kt app/src/test/java/com/example/carlogo/ui/BrandPracticeFilterTest.kt
git commit -m "feat: consolidate brand navigation"
```

### Task 3: 将车型维护与专项练习合并进品牌详情

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:1266-1450`
- Modify: `app/src/test/java/com/example/carlogo/ui/ManagementBrandLogoPresentationTest.kt`

**Interfaces:**
- Consumes: `Brand`、`CarModel`、品牌/车型仓库增删改方法。
- Produces: 品牌详情：开始练习、全部车型、自定义品牌维护和自定义车型维护。

- [ ] **Step 1: 写入失败测试，锁定详情页关键文案**

```kotlin
@Test fun `brand hub retains model list and practice labels`() {
    assertEquals(48, ManagementBrandLogoPresentation.TILE_SIZE_DP)
    assertEquals("全部车型", BrandHubPresentation.ALL_MODELS_LABEL)
    assertEquals("开始练习", BrandHubPresentation.START_PRACTICE_LABEL)
}
```

- [ ] **Step 2: 运行测试确认在 Task 1 实现前失败**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.ManagementBrandLogoPresentationTest`

Expected: FAIL，`BrandHubPresentation` 未解析。

- [ ] **Step 3: 用品牌详情融合维护与练习**

```kotlin
TechButton(
    BrandHubPresentation.START_PRACTICE_LABEL,
    { onStart(QuizMode.BrandPractice(brand.id)) },
    brand.cars.isNotEmpty(),
    Modifier.fillMaxWidth(),
)
Text("${BrandHubPresentation.ALL_MODELS_LABEL}（${brand.cars.size}）", ...)
```

复用原车型编辑弹窗和内置车型只读规则；在详情头部为自定义品牌提供编辑、删除入口。每个写入回调后重新加载品牌，删除品牌后返回品牌列表。

- [ ] **Step 4: 统一错题记录文案并运行相关测试**

将设置入口、错题列表标题、详情回退提示和首页错题快捷卡中的“错题本”统一为“错题记录”。

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.ManagementBrandLogoPresentationTest --tests com.example.carlogo.ui.MistakePresentationTest --tests com.example.carlogo.ui.MistakeReviewShortcutPresentationTest`

Expected: PASS。

- [ ] **Step 5: 提交品牌详情融合改动**

```bash
git add app/src/main/java/com/example/carlogo/ui/App.kt app/src/test/java/com/example/carlogo/ui/ManagementBrandLogoPresentationTest.kt
git commit -m "feat: merge brand practice and management"
```

### Task 4: 完整验证

**Files:**
- Modify: 无。

**Interfaces:**
- Consumes: 已完成的应用。
- Produces: 单元测试结果和 debug APK。

- [ ] **Step 1: 运行完整单元测试**

Run: `./gradlew.bat testDebugUnitTest`

Expected: BUILD SUCCESSFUL，0 failures。

- [ ] **Step 2: 构建 debug APK**

Run: `./gradlew.bat assembleDebug`

Expected: BUILD SUCCESSFUL，输出 `app/build/outputs/apk/debug/app-debug.apk`。

- [ ] **Step 3: 核对需求并提交最终变更**

确认导航、首页卡片、品牌详情、管理操作和错题记录文案均符合 Global Constraints。

```bash
git add app/src/main/java/com/example/carlogo/ui/App.kt app/src/test/java/com/example/carlogo/ui/BrandHubPresentationTest.kt app/src/test/java/com/example/carlogo/ui/BrandPracticeFilterTest.kt app/src/test/java/com/example/carlogo/ui/ManagementBrandLogoPresentationTest.kt
git commit -m "feat: refine brand learning hub"
```
