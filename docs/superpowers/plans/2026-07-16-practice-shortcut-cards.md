# Practice Shortcut Cards Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the Practice home screen's Brand Practice and Mistake Review shortcuts into data-aware, logo-rich action cards.

**Architecture:** Keep UI copy selection in a pure, unit-tested presentation model. Load the pending mistake count alongside the existing daily goal in `MainShell`, then render two focused Compose cards using existing brand-logo assets and unchanged route callbacks.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Room repository, JUnit 4.

## Global Constraints

- Use only existing `BrandLogoRegistry` drawable mappings; do not add generated or third-party image assets.
- Brand card displays at most three registered logos and has a safe text fallback.
- Mistake card reads the real pending mistake count and has distinct zero/non-zero copy.
- Preserve routes: brand card opens `special`; mistake card opens `mistakes`; add no retry/repractice action.
- Preserve all existing user changes, `.idea` files, ignored local `handoff.md`, keystore data, staged `.gitignore` and staged `handoff.md` deletion.

---

### Task 1: Testable mistake-review shortcut copy

**Files:**
- Create: `app/src/main/java/com/example/carlogo/ui/MistakeReviewShortcutPresentation.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/MistakeReviewShortcutPresentationTest.kt`

**Interfaces:**
- Produces: `MistakeReviewShortcutPresentation.fromPendingMistakeCount(count)`.
- Produces: `headline`, `detail`, and `hasPendingMistakes` properties for Compose.

- [ ] **Step 1: Write failing tests** for two pending mistakes, zero mistakes, and a negative-count validation error.

```kotlin
val pending = MistakeReviewShortcutPresentation.fromPendingMistakeCount(2)
assertEquals("待复盘 2 道", pending.headline)
assertTrue(pending.hasPendingMistakes)
assertEquals("暂无错题", MistakeReviewShortcutPresentation.fromPendingMistakeCount(0).headline)
```

- [ ] **Step 2: Run** `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.MistakeReviewShortcutPresentationTest` **and confirm it fails because the presentation type does not exist.**
- [ ] **Step 3: Implement the immutable model** with non-negative validation and the exact Chinese copy.
- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: Home data refresh and card composition

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:260-280, 375-522`

**Interfaces:**
- Consumes: `MistakeReviewShortcutPresentation`, `repository.mistakes()`, `BrandLogoRegistry`, and loaded `brands`.
- Produces: `BrandPracticeShortcutCard` and `MistakeReviewShortcutCard` composables that retain current route callbacks.

- [ ] **Step 1: In `MainShell`, add remembered shortcut presentation state** initialized from count zero, and refresh it from `repository.mistakes().size` when `page == "random"` alongside the daily goal.
- [ ] **Step 2: Pass `brands` and the presentation into `RandomPracticePage`.**
- [ ] **Step 3: Replace `ShortcutCard` calls** with a brand card that filters and shows up to three registered logo tiles plus text fallback, and a mistake card that renders the pure model, review-tag layers, warning/clear accent, and a route arrow.
- [ ] **Step 4: Keep the two-column size stable on narrow screens** with a fixed card height, weighted width, compact labels, and no horizontal scrolling.
- [ ] **Step 5: Run** `./gradlew.bat testDebugUnitTest assembleDebug` **and confirm unit tests and the debug APK build succeed.**
