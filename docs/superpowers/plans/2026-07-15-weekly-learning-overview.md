# Weekly Learning Overview Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the static My Learning identity card with a cumulative-question-focused weekly learning overview.

**Architecture:** Read completed quiz sessions and mistakes from existing Room tables without a schema change. Convert the persistence rows into a pure aggregate model that can be tested using fixed timestamps, then render that model in the settings page.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Room, JUnit 4.

## Global Constraints

- Primary card metric is the lifetime completed question count.
- Weekly session count uses the device-local Monday at 00:00 as its inclusive boundary.
- Only sessions whose `finishedAt` is non-null count.
- Preserve existing `.idea` files, ignored local `handoff.md`, keystore configuration, build outputs, and already staged Git changes.

---

### Task 1: Testable learning aggregate

**Files:**
- Create: `app/src/main/java/com/example/carlogo/domain/LearningOverview.kt`
- Create: `app/src/test/java/com/example/carlogo/domain/LearningOverviewTest.kt`

**Interfaces:**
- Produces: `CompletedLearningSession(totalQuestionCount: Int, correctQuestionCount: Int, finishedAtMillis: Long)`.
- Produces: `LearningOverview.fromCompletedSessions(sessions, weekStartMillis, pendingMistakeCount)`.

- [ ] **Step 1: Write failing tests** for zero state, total question/correct aggregation, integer correct rate, weekly boundary inclusion, and pending mistakes.

```kotlin
val overview = LearningOverview.fromCompletedSessions(
    sessions = listOf(CompletedLearningSession(10, 8, 1_000L), CompletedLearningSession(5, 4, 2_000L)),
    weekStartMillis = 1_500L,
    pendingMistakeCount = 3,
)
assertEquals(15, overview.totalCompletedQuestionCount)
assertEquals(80, overview.correctRatePercent)
assertEquals(1, overview.weeklyCompletedSessionCount)
```

- [ ] **Step 2: Run `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.LearningOverviewTest`** and confirm it fails because the aggregate type does not exist.

- [ ] **Step 3: Implement the minimal immutable aggregate** with a zero-safe correct rate and session timestamp filtering.

- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: Repository overview source

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/data/local/Daos.kt:29-37`
- Modify: `app/src/main/java/com/example/carlogo/data/CarRepository.kt:1-90`

**Interfaces:**
- Produces: `CarRepository.learningOverview(): LearningOverview`.

- [ ] **Step 1: Add `getCompletedHistory()`** with `WHERE finishedAt IS NOT NULL` to `QuizDao`.
- [ ] **Step 2: Calculate the current local Monday start** with `DayOfWeek.MONDAY` and `TemporalAdjusters.previousOrSame`.
- [ ] **Step 3: Map completed Room entities and current mistakes into `LearningOverview.fromCompletedSessions`.
- [ ] **Step 4: Compile the debug unit-test task** to verify the Room DAO query and repository interface.

### Task 3: My Learning overview card

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:80-100, 280-310, 700-730`

**Interfaces:**
- Consumes: `CarRepository.learningOverview()` and `LearningOverview`.
- Produces: a `LearningOverviewCard(overview)` above the existing Learning Record entry.

- [ ] **Step 1: Pass `CarRepository` into `SettingsPage`** and load the overview with `LaunchedEffect(Unit)`.
- [ ] **Step 2: Replace the static identity text** with `本周学习概览`, `累计完成 X 题`, an overall-correct-rate ring, and weekly/mistake summary copy.
- [ ] **Step 3: Preserve the existing Learning Record, Mistake Book, and Question Bank Management navigation entries.**
- [ ] **Step 4: Run `./gradlew.bat testDebugUnitTest assembleDebug`** and confirm all unit tests pass and the debug APK exists.
