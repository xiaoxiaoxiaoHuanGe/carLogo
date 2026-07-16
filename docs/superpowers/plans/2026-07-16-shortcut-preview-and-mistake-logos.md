# Shortcut Preview Alignment and Mistake Logos Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Match the approved shortcut-card preview layout and replace mistake-list warning icons with the related brand logos.

**Architecture:** Extend the already pure mistake presentation with a brand ID verified by tests. Then use existing `BrandLogoTile` mappings in the list and refactor the two Practice home cards around one shared fixed title slot so decorative elements cannot shift title alignment.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, JUnit 4, existing drawable resources.

## Global Constraints

- Use only existing `BrandLogoRegistry` mappings and drawables; do not add generated assets.
- Both home shortcut cards use the same 180dp height and same title-slot dimensions/style.
- Mistake list shows a brand logo for registered brands and a neutral fallback only when no drawable exists.
- Preserve `special` and `mistakes` routes, all existing data behavior, and the read-only mistake detail flow.
- Preserve all existing user changes, `.idea` files, ignored local `handoff.md`, keystore data, staged `.gitignore` and staged `handoff.md` deletion.

---

### Task 1: Carry brand identity into mistake presentation

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/MistakePresentation.kt`
- Modify: `app/src/test/java/com/example/carlogo/ui/MistakePresentationTest.kt`

**Interfaces:**
- Adds: `MistakePresentation.brandId: String`.
- Produces: `MistakePresentationMapper.from(...)` values that preserve the resolved `Brand.id` for both question types.

- [ ] **Step 1: Add a failing assertion** to both existing successful mapping tests.

```kotlin
assertEquals("cadillac", presentation.brandId)
```

- [ ] **Step 2: Run** `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.MistakePresentationTest` **and confirm it fails because `brandId` does not exist.**
- [ ] **Step 3: Add the immutable `brandId` field** and map it from the resolved brand in both `QuestionType` branches.
- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: Preview-aligned shortcuts and mistake-list logos

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:520-609, 1065-1112`

**Interfaces:**
- Consumes: `MistakePresentation.brandId`, loaded `brands`, existing `BrandLogoTile`, and `MistakeReviewShortcutPresentation`.
- Produces: aligned shortcut cards and a brand-logo leading visual for every resolved mistake row.

- [ ] **Step 1: Extract a shared 30dp title-slot modifier/constant** used by both shortcut cards; render the title with identical `titleLarge`, bold, and top-start alignment.
- [ ] **Step 2: Recompose the brand card** with title, subtitle, a center-aligned 48dp/14dp/6dp three-logo stack and bottom-right `→`; retain `品牌库` fallback.
- [ ] **Step 3: Recompose the mistake card** with the same title slot, model headline/detail, stacked review tags, absolute top-end 42dp pink warning badge for pending data (or mint `✓` zero state), and bottom-right `→`.
- [ ] **Step 4: Replace the list-row red `!` tile** with `BrandLogoTile` at 46dp/15dp/6dp when the mapped brand has a drawable; otherwise render a neutral `车` fallback tile using the same dimensions.
- [ ] **Step 5: Run** `./gradlew.bat testDebugUnitTest assembleDebug` **and confirm all tests and the debug APK build succeed.**
