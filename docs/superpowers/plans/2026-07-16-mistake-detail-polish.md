# Mistake Detail Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the mistake-detail page into a concise review screen with brand identity, an answer highlight, and a compact error-count badge.

**Architecture:** Extend the existing `MistakePresentation` mapper with a stable display name for the resolved brand. Pass the loaded brand list to the existing detail screen so it can reuse `BrandLogoTile`; retain a safe text fallback if the brand can no longer be found.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, JUnit 4.

## Global Constraints

- Preserve the existing local Room schema and mistake-detail navigation.
- Reuse existing offline logo assets through `BrandLogoTile`.
- Do not stage, commit, reset, or alter user-owned IDE files.

---

### Task 1: Add brand identity to the detail presentation

**Files:**
- Modify: `app/src/test/java/com/example/carlogo/ui/MistakePresentationTest.kt`
- Modify: `app/src/main/java/com/example/carlogo/ui/MistakePresentation.kt`

**Interfaces:**
- Produces: `MistakePresentation.brandName: String` for the detail hero card.

- [ ] **Step 1: Write the failing assertions**

Add `assertEquals("凯迪拉克", presentation.brandName)` to both mapped-question tests.

- [ ] **Step 2: Run the focused test**

Run: `./gradlew.bat :app:testDebugUnitTest --tests com.example.carlogo.ui.MistakePresentationTest`

Expected: compilation failure because `brandName` does not yet exist.

- [ ] **Step 3: Implement the minimal mapper change**

Add `val brandName: String` to `MistakePresentation` and set `brandName = brandName` in both mapper branches.

- [ ] **Step 4: Verify the focused test**

Run the same command and confirm the mapper tests pass.

### Task 2: Rebuild the Compose detail layout

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`

**Interfaces:**
- Consumes: `MistakePresentation.brandId`, `MistakePresentation.brandName`, `MistakePresentation.question`, `MistakePresentation.correctAnswer`, and `MistakePresentation.wrongCount`.
- Produces: A hero card with brand identity and a red error badge, followed by a combined question-and-answer review card.

- [ ] **Step 1: Pass brands to the detail screen**

Change the `mistakeDetail` route to call `MistakeDetailScreen(mistake, brands, insets, ...)`.

- [ ] **Step 2: Replace the three equal-weight cards**

Render a top `TechCard` with a 64dp `BrandLogoTile`, question type, brand name, and a red circular `wrongCount` badge. Render a second `TechCard` with the question, a divider, a green check label, and a `headlineMedium` correct-answer value.

- [ ] **Step 3: Keep an unavailable-brand fallback**

If `brands.firstOrNull { it.id == mistake.brandId }` returns null, show a neutral 64dp `车` tile instead of a logo.

- [ ] **Step 4: Verify layout code and build**

Run `git diff --check`, then `./gradlew.bat testDebugUnitTest assembleDebug`.
