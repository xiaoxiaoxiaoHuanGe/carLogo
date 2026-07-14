# Random Practice Settings and Brand Cards Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add persistent 5–50 question selection to random practice, remove the current vehicle-image UI while retaining image data, and stabilize brand-practice cards.

**Architecture:** A pure `RandomPracticeConfig` object defines the count contract used by `QuizGenerator` and the Compose UI. The app root persists the selected count with `SharedPreferences`, while the random card configures and displays it. Existing data-layer image fields stay unchanged; only the management UI image interaction is removed.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose Material 3, Room 2.7.1, JUnit 4.

## Global Constraints

- Random count defaults to 10 and is an integer from 5 through 50 inclusive.
- Random questions do not enforce a type split; brand practice retains its existing generation behavior.
- Keep `imageRef` fields and existing image data untouched; do not add a Room migration.
- Keep the brand-practice grid at two columns and disable horizontal scrolling.
- Do not stage or modify pre-existing `.idea`, handoff, or unrelated documentation changes.

---

### Task 1: Define and test the random-round contract

**Files:**
- Create: `app/src/main/java/com/example/carlogo/domain/RandomPracticeConfig.kt`
- Modify: `app/src/main/java/com/example/carlogo/domain/QuizGenerator.kt`
- Modify: `app/src/test/java/com/example/carlogo/domain/QuizGeneratorTest.kt`
- Create: `app/src/test/java/com/example/carlogo/domain/RandomPracticeConfigTest.kt`

**Interfaces:**
- Produces `RandomPracticeConfig.DEFAULT_QUESTION_COUNT`, `MIN_QUESTION_COUNT`, `MAX_QUESTION_COUNT`, and `validateQuestionCount(Int): Int`.
- Changes `QuizGenerator.createRound(mode, brands, randomQuestionCount)` so the third argument controls random rounds only.

- [ ] **Step 1: Write failing configuration tests**

```kotlin
@Test fun `default count is ten`() = assertEquals(10, RandomPracticeConfig.DEFAULT_QUESTION_COUNT)
@Test fun `accepts range endpoints`() {
    assertEquals(5, RandomPracticeConfig.validateQuestionCount(5))
    assertEquals(50, RandomPracticeConfig.validateQuestionCount(50))
}
@Test(expected = IllegalArgumentException::class)
fun `rejects count below minimum`() { RandomPracticeConfig.validateQuestionCount(4) }
```

- [ ] **Step 2: Run the new configuration test and verify it fails because the object is absent**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.RandomPracticeConfigTest`

Expected: compilation failure referring to unresolved `RandomPracticeConfig`.

- [ ] **Step 3: Write failing random-generation tests**

```kotlin
@Test fun `random round supports an odd requested count without duplicate targets`() {
    val questions = QuizGenerator(Random(7)).createRound(QuizMode.Random, brands, randomQuestionCount = 11)
    assertEquals(11, questions.size)
    assertEquals(11, questions.map { "${it.type}|${it.targetBrandId}|${it.targetCarId}" }.distinct().size)
}

@Test fun `random round supports the configured maximum`() {
    val questions = QuizGenerator(Random(9)).createRound(QuizMode.Random, brands, randomQuestionCount = 50)
    assertEquals(50, questions.size)
    assertTrue(questions.all { it.options.size == 4 && it.options.count { option -> option.isCorrect } == 1 })
}
```

- [ ] **Step 4: Run the generator test and verify the old fixed-20 behavior fails**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.QuizGeneratorTest`

Expected: the 11-question assertion fails because the current generator returns 20 questions.

- [ ] **Step 5: Implement the minimal configuration and generator behavior**

```kotlin
object RandomPracticeConfig {
    const val MIN_QUESTION_COUNT = 5
    const val MAX_QUESTION_COUNT = 50
    const val DEFAULT_QUESTION_COUNT = 10

    fun validateQuestionCount(questionCount: Int): Int {
        require(questionCount in MIN_QUESTION_COUNT..MAX_QUESTION_COUNT) {
            "随机练习题数必须在 $MIN_QUESTION_COUNT 到 $MAX_QUESTION_COUNT 之间。"
        }
        return questionCount
    }
}
```

For `QuizMode.Random`, select a question type independently for each requested question. Select an unused `(QuestionType, Brand, CarModel)` candidate of that type when available; after that type's unique candidates are exhausted, reuse a candidate of the same type. This preserves random type selection even at 50 questions. Leave the brand-practice branch on its existing per-type generation path.

- [ ] **Step 6: Run the focused tests and verify they pass**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.RandomPracticeConfigTest --tests com.example.carlogo.domain.QuizGeneratorTest`

Expected: all selected tests pass.

### Task 2: Persist and expose the random count in Compose

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`

**Interfaces:**
- `CarLogoApp` owns the saved random count and passes it into `startRound` only for `QuizMode.Random`.
- `RandomPracticePage` receives the current count and an update callback.

- [ ] **Step 1: Keep Task 1 tests green before UI work**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.domain.RandomPracticeConfigTest --tests com.example.carlogo.domain.QuizGeneratorTest`

Expected: all selected tests pass.

- [ ] **Step 2: Implement persisted settings and count-aware start**

Use `context.getSharedPreferences("random_practice_settings", Context.MODE_PRIVATE)` and the key `question_count`. Read with `DEFAULT_QUESTION_COUNT` and clamp it to the configuration range. When the user confirms a setting, update Compose state and call `edit().putInt(...).apply()`. Call `QuizGenerator().createRound(mode, brands, selectedRandomQuestionCount)` only when `mode is QuizMode.Random`; brand practice uses its existing default path.

- [ ] **Step 3: Implement the compact settings interaction**

Render the configured count in place of the hard-coded `20`. Place a small settings button adjacent to `开始练习`; it opens a dialog containing a Material 3 `Slider` with `valueRange = 5f..50f`, `steps = 44`, a current count label, and confirm/cancel actions. The start button remains direct and starts the saved count immediately.

- [ ] **Step 4: Build the debug APK**

Run: `./gradlew.bat assembleDebug`

Expected: `BUILD SUCCESSFUL` and `app/build/outputs/apk/debug/app-debug.apk` exists.

### Task 3: Remove vehicle-image UI and stabilize brand cards

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`

**Interfaces:**
- Existing `CarModel.imageRef`, `CarEntity.imageRef`, and repository signatures remain unchanged.
- The add-car path passes `null`; edit preserves `editingCar.imageRef` without showing it.

- [ ] **Step 1: Remove only image-interaction code**

Delete the Activity Result imports, image state, picker launcher, album button, `copyImageToPrivateStorage`, and file/UUID imports from `App.kt`. In the save callback, pass `null` for a new car and `editingCar?.imageRef` when updating a car. Do not modify Room entities, domain models, repository methods, or image references in question data.

- [ ] **Step 2: Make card geometry independent of text length**

Keep `GridCells.Fixed(2)` and its existing 14dp spacing. Apply `Modifier.fillMaxWidth().height(180.dp)` to every `BrandCard`; keep the brand mark size fixed and use compact text styles with explicit line limits and ellipsis so all cards retain equal geometry.

- [ ] **Step 3: Search for forbidden UI remnants and build**

Run: `rg -n 'rememberLauncherForActivityResult|ActivityResultContracts|从相册选择车型图片|copyImageToPrivateStorage' app/src/main/java/com/example/carlogo/ui/App.kt`

Expected: no matches.

Run: `./gradlew.bat assembleDebug`

Expected: `BUILD SUCCESSFUL`.

### Task 4: Full verification and review

**Files:**
- Modify: `app/src/test/java/com/example/carlogo/domain/QuizGeneratorTest.kt`
- Create: `app/src/test/java/com/example/carlogo/domain/RandomPracticeConfigTest.kt`

- [ ] **Step 1: Run the full required check**

Run: `./gradlew.bat testDebugUnitTest assembleDebug`

Expected: all debug unit tests and debug APK assembly succeed.

- [ ] **Step 2: Inspect the scoped diff and source invariants**

Run: `git diff --check -- app/src/main/java/com/example/carlogo app/src/test/java/com/example/carlogo`

Expected: no whitespace errors.

Run: `rg -n 'val imageRef|imageRef:' app/src/main/java/com/example/carlogo/domain app/src/main/java/com/example/carlogo/data`

Expected: image fields remain in the model/data layers.

- [ ] **Step 3: Manual smoke checklist**

Install the debug APK and confirm: random starts directly with the saved count; settings slider accepts 5 and 50; restarting retains the value; the car dialog has no image button; brand cards are two columns and share the same height.
