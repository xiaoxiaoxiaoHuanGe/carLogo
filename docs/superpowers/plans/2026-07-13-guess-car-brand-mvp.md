# Guess Car Brand MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a fully offline Kotlin Android app that teaches car-model-to-brand associations through 20-question quizzes.

**Architecture:** A single Android app module uses Compose screens backed by ViewModels and a Room database. The quiz generator is a pure Kotlin class so it can be unit-tested without Android framework dependencies; repositories persist seeded content, quiz history, and mistakes locally.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room/KSP, Coil, Android Photo Picker, JUnit 4.

## Global Constraints

- Native Android, Kotlin, offline only; no server, account, or network API.
- Every round contains exactly 20 questions: 10 brand-to-model and 10 model-to-brand.
- Each question has exactly four options and three distractors from other brands.
- Built-in seed data contains 20 brands and at least five models per brand.
- Images are stored as bundled drawables or copied to app-private storage after user selection.
- User progress, history, and mistakes persist in Room.

---

### Task 1: Build baseline and domain test harness

**Files:**
- Modify: `gradle/libs.versions.toml`, `app/build.gradle.kts`
- Create: `app/src/test/java/com/example/carlogo/domain/QuizGeneratorTest.kt`

**Interfaces:**
- Produces a compile-ready Compose Android app module and failing tests for `QuizGenerator.createRound`.

- [ ] Verify the existing Compose project with `./gradlew.bat testDebugUnitTest`.
- [ ] Add Room/KSP, Navigation Compose, Lifecycle ViewModel, and Coil dependencies.
- [ ] Write a failing test that requires twenty questions, ten of each type, four unique options, and one correct option.
- [ ] Run `./gradlew.bat testDebugUnitTest`; expected result before domain implementation: compilation fails because `QuizGenerator` does not exist.

### Task 2: Testable quiz domain

**Files:**
- Create: `app/src/main/java/com/example/carlogo/domain/model/QuizModels.kt`
- Create: `app/src/main/java/com/example/carlogo/domain/QuizGenerator.kt`
- Modify: `app/src/test/java/com/example/carlogo/domain/QuizGeneratorTest.kt`

**Interfaces:**
- Consumes: `List<BrandWithCars>` and `QuizMode`.
- Produces: `createRound(mode: QuizMode, brands: List<BrandWithCars>, selectedBrandId: Long?): List<Question>`.

- [ ] Implement only the models and deterministic generator behavior needed by the failing tests.
- [ ] Run unit tests until they pass.

### Task 3: Room persistence and seed content

**Files:**
- Create: `app/src/main/java/com/example/carlogo/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/example/carlogo/data/local/Entities.kt`
- Create: `app/src/main/java/com/example/carlogo/data/local/Daos.kt`
- Create: `app/src/main/java/com/example/carlogo/data/SeedData.kt`
- Create: `app/src/main/java/com/example/carlogo/data/CarRepository.kt`

**Interfaces:**
- Produces: `CarRepository` methods for brands, cars, persistence, history, and mistakes.

- [ ] Add tests for seed data cardinality and the repository-independent mapping.
- [ ] Implement Room entities, DAO queries, seed insertion, and repository operations.
- [ ] Run unit tests.

### Task 4: Compose app flows

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/MainActivity.kt`
- Create: `app/src/main/java/com/example/carlogo/ui/App.kt`
- Create: `app/src/main/java/com/example/carlogo/ui/QuizViewModel.kt`
- Create: `app/src/main/java/com/example/carlogo/ui/Screens.kt`

**Interfaces:**
- Consumes: `CarRepository`, `QuizGenerator`.
- Produces: home, brand selection, quiz, result, mistake, history, and management screens.

- [ ] Test ViewModel answer scoring using a fake repository.
- [ ] Implement the screen state and Compose navigation.
- [ ] Run unit tests and a debug build.

### Task 5: Image import and final verification

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/Screens.kt`
- Create: `README.md`

- [ ] Use the Android Photo Picker to select a custom model image, copy it into `filesDir/car_images`, and store its path.
- [ ] Verify random practice, brand practice, feedback, history, mistakes, and local CRUD manually.
- [ ] Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat assembleDebug`.
