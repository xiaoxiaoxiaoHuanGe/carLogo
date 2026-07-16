# Mistake Detail Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make mistake-book entries readable and open an informational detail page with the semantic question and correct answer.

**Architecture:** Transform the existing stored mistake IDs into a pure UI presentation with the in-memory brand/car question bank. The navigation shell retains the selected presentation and renders a new detail page; no retry flow or database change is added.

**Tech Stack:** Kotlin, Jetpack Compose Material 3, Room entities, JUnit 4.

## Global Constraints

- Feature is display-only: no retry button, practice mode, quiz generator change, or write to the mistake table.
- Render Chinese question-type labels, never `ModelToBrand` or `BrandToModel`.
- Use only the existing brand/car data loaded by `CarRepository.loadBrands()`.
- Preserve prior uncommitted feature changes, staged `.gitignore` and `handoff.md` deletion, and all `.idea` files.

---

### Task 1: Testable mistake presentation mapper

**Files:**
- Create: `app/src/main/java/com/example/carlogo/ui/MistakePresentation.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/MistakePresentationTest.kt`

**Interfaces:**
- Produces: `MistakePresentationMapper.from(mistake: MistakeEntity, brands: List<Brand>): MistakePresentation?`.
- Produces: list title, question-type label, semantic prompt, correct answer, and wrong count.

- [ ] **Step 1: Write failing tests** for a model-to-brand mistake, a brand-to-model mistake, and missing brand/car source data.

```kotlin
assertEquals("看车型猜品牌", presentation.questionTypeLabel)
assertEquals("凯迪拉克 CT4 属于哪个品牌？", presentation.question)
assertEquals("凯迪拉克", presentation.correctAnswer)
```

- [ ] **Step 2: Run `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.MistakePresentationTest`** and confirm the missing mapper causes a compilation failure.

- [ ] **Step 3: Implement the mapper** using `QuestionType.entries`, `Brand.displayText()`, and `CarModel.displayText()`; return null for unresolved source data.

- [ ] **Step 4: Re-run the focused test** and confirm it passes.

### Task 2: List and detail navigation

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt:80-100, 250-330, 820-850`

**Interfaces:**
- Consumes: `MistakePresentation` generated from the loaded brand list.
- Produces: `MistakeDetailScreen(presentation, insets, onBack)`.

- [ ] **Step 1: Add selected-mistake state** to `MainShell` and route `mistakeDetail` back to `mistakes`.
- [ ] **Step 2: Pass brands and an item-click callback to `MistakesScreen`**; map each record and make resolved cards clickable.
- [ ] **Step 3: Replace raw IDs/enums in each row** with its readable title, Chinese type label, correct-answer line, wrong-count badge, and chevron.
- [ ] **Step 4: Add the detail page** with cards for the question, correct answer, type, and cumulative wrong count. Do not add a practice action.
- [ ] **Step 5: Run `./gradlew.bat testDebugUnitTest assembleDebug`** and confirm all unit tests pass and the debug APK is assembled.
