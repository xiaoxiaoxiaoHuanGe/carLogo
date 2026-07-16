# Seven-Day Learning History Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace session-only learning records with a seven-day heatmap and date-grouped, weighted daily learning summaries.

**Architecture:** Add a pure domain aggregator that turns completed sessions into seven local-date summaries. The repository supplies only finished Room sessions; Compose renders the heatmap and every non-empty date summary without changing the Room schema.

**Tech Stack:** Kotlin, java.time, Jetpack Compose Material 3, Room, JUnit 4.

## Global Constraints

- The range is exactly today and the preceding six device-local calendar days.
- Daily correct rate is integer `totalCorrect * 100 / totalQuestions` and is absent when no questions were completed.
- Heat levels are `NONE` (0), `LOW` (1–9), `MEDIUM` (10–19), and `HIGH` (20+ completed questions).
- Only sessions with non-null `finishedAt` are displayed or aggregated.
- Preserve all existing user changes, `.idea` files, ignored local `handoff.md`, keystore data, staged `.gitignore` and staged `handoff.md` deletion.

---

### Task 1: Testable seven-day aggregate

**Files:**
- Create: `app/src/main/java/com/example/carlogo/domain/LearningHistory.kt`
- Create: `app/src/test/java/com/example/carlogo/domain/LearningHistoryTest.kt`

**Interfaces:**
- Produces: `CompletedPracticeSession(mode, totalQuestionCount, correctQuestionCount, finishedAtMillis)`.
- Produces: `LearningHistory.fromCompletedSessions(sessions, today, zoneId): List<LearningDaySummary>`.

- [ ] **Step 1: Write failing tests** asserting that seven consecutive days are returned, same-day sessions are weighted together, sessions older than six days are omitted, and all four heat levels are assigned.

```kotlin
val summaries = LearningHistory.fromCompletedSessions(
    sessions = listOf(
        CompletedPracticeSession("Random", 10, 8, instant("2026-07-16T08:00:00Z")),
        CompletedPracticeSession("BrandPractice", 2, 1, instant("2026-07-16T10:00:00Z")),
    ),
    today = LocalDate.of(2026, 7, 16),
    zoneId = ZoneOffset.UTC,
)
assertEquals(12, summaries.last().completedQuestionCount)
assertEquals(75, summaries.last().correctRatePercent)
```

- [ ] **Step 2: Run** `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.LearningHistoryTest` **and confirm it fails because the aggregate types do not exist.**
- [ ] **Step 3: Implement the immutable aggregate** with date filtering, chronological session rows, weighted rate and four heat levels.
- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: Completed-session repository source

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/data/CarRepository.kt:68-75`

**Interfaces:**
- Changes: `CarRepository.history(): List<QuizSessionEntity>` returns `QuizDao.getCompletedHistory()`.

- [ ] **Step 1: Change the repository history source** from the all-session query to `getCompletedHistory()`.
- [ ] **Step 2: Compile the focused debug unit-test task** to verify the repository compiles against the existing Room DAO.

### Task 3: Seven-day record screen

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:80-100, 831-840`

**Interfaces:**
- Consumes: `LearningHistory.fromCompletedSessions(...)` and `LearningDaySummary`.
- Produces: a `RecordsScreen` with a seven-cell heatmap and date-grouped completed-session detail.

- [ ] **Step 1: Replace the one-row-per-session UI** with a loaded `LearningHistory` state derived from completed session records using `LocalDate.now(ZoneId.systemDefault())`.
- [ ] **Step 2: Render a full-width seven-cell heatmap** showing Chinese weekday, date and completed count, with color based exclusively on `HeatLevel`.
- [ ] **Step 3: Render non-empty days from newest to oldest** with date, completed question count, weighted correct rate, session count, mode, score and local completion time.
- [ ] **Step 4: Render an empty state** when no day in the seven-day range has completed questions.
- [ ] **Step 5: Run** `./gradlew.bat testDebugUnitTest assembleDebug` **and confirm unit tests and debug APK assembly succeed.**
