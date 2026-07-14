# Final Answer Feedback Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Keep the final quiz question visible with correct-or-wrong feedback until the user explicitly chooses to view the score.

**Architecture:** Preserve `QuizSessionState.isFinished` as the factual condition that every question has been answered. Add a separate confirmation flag controlled by `QuizSessionController`; the root UI routes to the result screen only after that confirmation. The quiz footer exposes a score button only after the final question has been answered.

**Tech Stack:** Kotlin, Jetpack Compose, JUnit 4, Gradle Android test task.

## Global Constraints

- Random and special practice must share the same corrected session behavior.
- Do not change question generation, scoring, answer locking, or non-final auto-advance behavior.
- Persist the session result only after the user chooses to view the score.

---

### Task 1: Add explicit result confirmation to the quiz session

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/domain/QuizSessionController.kt`
- Test: `app/src/test/java/com/example/carlogo/domain/QuizSessionControllerTest.kt`

**Interfaces:**
- Produces: `QuizSessionState.isResultConfirmed: Boolean` and `QuizSessionController.confirmResult(): QuizSessionState`.

- [ ] **Step 1: Write the failing test**

```kotlin
@Test
fun `final answer waits for explicit result confirmation`() {
    val controller = QuizSessionController(listOf(question("q1", true), question("q2", true)))

    controller.answer("correct-q1")
    controller.next()
    val finalAnswer = controller.answer("correct-q2")

    assertTrue(finalAnswer.isFinished)
    assertFalse(finalAnswer.isResultConfirmed)

    val confirmed = controller.confirmResult()

    assertTrue(confirmed.isResultConfirmed)
}
```

- [ ] **Step 2: Run the focused test and verify it fails**

Run: `./gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.domain.QuizSessionControllerTest`

Expected: test compilation fails because `isResultConfirmed` and `confirmResult` do not exist.

- [ ] **Step 3: Write the minimal implementation**

```kotlin
data class QuizSessionState(
    // existing fields
    val isResultConfirmed: Boolean = false,
)

fun confirmResult(): QuizSessionState {
    if (state.isFinished) state = state.copy(isResultConfirmed = true)
    return state
}
```

- [ ] **Step 4: Run the focused test and verify it passes**

Run: `./gradlew.bat --console=plain testDebugUnitTest --tests com.example.carlogo.domain.QuizSessionControllerTest`

Expected: `BUILD SUCCESSFUL`.

### Task 2: Route to the score page only after confirmation

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:187-210,882-957`
- Test: `app/src/test/java/com/example/carlogo/domain/QuizSessionControllerTest.kt`

**Interfaces:**
- Consumes: `QuizSessionState.isResultConfirmed`, `QuizSessionController.confirmResult()`.
- Produces: a `查看评分` action visible after the final answer and delayed persistence of the session score.

- [ ] **Step 1: Wire the root route and quiz footer**

```kotlin
activeState != null && !activeState.isResultConfirmed -> QuizScreen(/* ... */, onShowResult = { /* confirm and persist */ })
activeState?.isResultConfirmed == true -> ResultScreen(activeState) { /* existing restart */ }

if (state.hasNextQuestion) {
    TechButton("下一题", onNext, state.canGoNext, Modifier.weight(1f))
} else {
    TechButton("查看评分", onShowResult, state.isFinished, Modifier.weight(1f))
}
```

- [ ] **Step 2: Run all unit tests and build the debug APK**

Run: `./gradlew.bat --console=plain testDebugUnitTest assembleDebug`

Expected: `BUILD SUCCESSFUL`; debug APK at `app/build/outputs/apk/debug/app-debug.apk`.
