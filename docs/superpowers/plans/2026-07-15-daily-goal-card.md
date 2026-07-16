# Daily Goal Card Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the decorative random-home progress card with a persisted daily 10-question goal based on completed quiz sessions.

**Architecture:** Keep session persistence unchanged. Query the existing `quiz_sessions` rows that have a `finishedAt` timestamp inside the device-local calendar day, expose the aggregate through `CarRepository`, and transform it into a pure display model. The Compose home page reloads that model when its random tab is entered.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Room, JUnit 4.

## Global Constraints

- Daily target is exactly 10 questions.
- Count only `quiz_sessions` rows whose `finishedAt` is non-null and falls within `[local day start, next local day start)`.
- Preserve the current Room version 2 schema and `MIGRATION_1_2`.
- Do not stage or alter `.idea` files, `keystore.properties`, build outputs, or the local ignored `handoff.md`.

---

### Task 1: Pure daily-goal display model

**Files:**
- Create: `app/src/main/java/com/example/carlogo/domain/DailyGoalProgress.kt`
- Create: `app/src/test/java/com/example/carlogo/domain/DailyGoalProgressTest.kt`

**Interfaces:**
- Produces: `DailyGoalProgress.fromCompletedQuestionCount(completedQuestionCount: Int): DailyGoalProgress`.
- Produces: `completedQuestionCount`, `goalQuestionCount`, `remainingQuestionCount`, `progress`, and `isCompleted` for the UI.

- [ ] **Step 1: Write failing unit tests** for zero progress, partial progress, capping progress after the target, and rejecting a negative source count.

```kotlin
assertEquals(0, DailyGoalProgress.fromCompletedQuestionCount(0).completedQuestionCount)
assertEquals(5, DailyGoalProgress.fromCompletedQuestionCount(5).remainingQuestionCount)
assertEquals(10, DailyGoalProgress.fromCompletedQuestionCount(18).completedQuestionCount)
```

- [ ] **Step 2: Run `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.DailyGoalProgressTest`** and confirm it fails because the type does not exist.

- [ ] **Step 3: Add `DailyGoalProgress`** with target 10, bounded displayed completion, and a `0f..1f` progress value.

- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: Completed-question aggregate

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/data/local/Daos.kt:29-35`
- Modify: `app/src/main/java/com/example/carlogo/data/CarRepository.kt:62-70`

**Interfaces:**
- Consumes: `QuizSessionEntity.totalCount` and `QuizSessionEntity.finishedAt`.
- Produces: `CarRepository.todayCompletedQuestionCount(): Int`.

- [ ] **Step 1: Add the Room query** `completedQuestionCountBetween(dayStartMillis, nextDayStartMillis)` using `COALESCE(SUM(totalCount), 0)` and excluding null `finishedAt` values.

- [ ] **Step 2: Add repository day-boundary calculation** using `LocalDate.now()` and `ZoneId.systemDefault()`, then delegate to the DAO query.

- [ ] **Step 3: Compile the debug unit-test task** to verify Room accepts the aggregate query and generated DAO implementation.

### Task 3: Home-page card and refresh

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:1-110, 238-285, 330-460`

**Interfaces:**
- Consumes: `DailyGoalProgress` and `CarRepository.todayCompletedQuestionCount()`.
- Produces: a `LearningProgressCard(progress: DailyGoalProgress)` whose ring and text reflect current-day completion.

- [ ] **Step 1: Store a `DailyGoalProgress` in `MainShell`** and refresh it in a `LaunchedEffect(page)` only when `page == "random"`.

- [ ] **Step 2: Pass the value into `RandomPracticePage`** and then into `LearningProgressCard`.

- [ ] **Step 3: Replace hard-coded `连续学习 7 天`, seven dots, and `68%`** with `今日目标`, `已完成 X/10 题`, remaining/completed copy, and a Material 3 circular indicator around `X/10`.

- [ ] **Step 4: Run `./gradlew.bat testDebugUnitTest assembleDebug`** and confirm all unit tests pass and the debug APK is created.
