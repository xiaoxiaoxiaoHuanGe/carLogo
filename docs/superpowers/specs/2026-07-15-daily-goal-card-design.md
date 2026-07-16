# Daily Goal Card Design

## Goal

Replace the decorative home learning-progress card with a real daily goal that advances by the number of questions in completed, confirmed quiz sessions.

## Confirmed requirements

- The card appears at the top of the random-practice home page, in the existing card position.
- The daily target is a fixed 10 questions.
- The card displays `今日目标`, the completed and target counts, a circular progress indicator, and an explicit remaining/completed status.
- Only sessions with a non-null `finishedAt` count. Leaving a quiz before confirming its result does not count because partial answers are not persisted.
- The total is calculated for the device's current local calendar day. It includes both random and brand-practice sessions and caps the visible progress at 10.
- No Room schema change, migration, new dependency, release version bump, or release build is needed.

## Architecture

`QuizDao` supplies a compact aggregate query over completed sessions in a supplied local-day range. `CarRepository` owns local-day boundary calculation and exposes the completed question count. A pure `DailyGoalProgress` domain type owns the fixed goal, cap, remaining count, and progress fraction so it can be unit tested without Room.

`MainShell` refreshes the progress whenever the random home page is displayed. `RandomPracticePage` receives the calculated value and `LearningProgressCard` renders it, preserving the existing visual hierarchy while replacing hard-coded data.

## Acceptance

- A new user sees `已完成 0/10 题` and `还差 10 题`.
- Completing and confirming a 5-question practice displays `已完成 5/10 题` on return to the home page.
- Completing more than 10 questions in the same day displays `已完成 10/10 题` and `今日目标已完成`.
- Unfinished sessions are excluded.
- Debug unit tests and debug APK assembly pass.
