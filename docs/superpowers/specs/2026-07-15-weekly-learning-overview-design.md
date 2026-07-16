# Weekly Learning Overview Card Design

## Goal

Replace the static "汽车品牌学习者" card on the My Learning page with a factual learning overview that emphasizes the cumulative number of completed questions.

## Confirmed requirements

- The card heading is `本周学习概览`.
- The primary metric is `累计完成 X 题`, calculated from every confirmed completed session.
- The card also shows overall correct rate, the number of confirmed sessions completed since the current local Monday, and the number of pending mistake records.
- An unfinished session never contributes to any metric.
- An empty account shows `累计完成 0 题`, `正确率 --`, `本周完成 0 次练习 · 暂无错题待复习`.
- The card is informational; the existing Learning Record and Mistake Book entries remain the dedicated navigation actions below it.
- No Room schema migration, new dependency, release build, or version change is required.

## Architecture

`QuizDao` returns only sessions with a non-null `finishedAt`. `CarRepository` converts them to a small pure domain input and calculates the local-Monday timestamp. `LearningOverview` aggregates total questions, total correct answers, weekly session count, and pending mistake count so its arithmetic is unit tested independently of Room.

`SettingsPage` loads the overview when it is composed and replaces the static identity card with the new presentation. Returning from a completed quiz constructs the settings page again, so its overview is read from the saved session records.

## Acceptance

- With no completed session, the zero-state copy is shown.
- Two completed sessions totaling 15 questions and 12 correct answers display `累计完成 15 题` and `80%` correct rate.
- Only sessions finished on or after the current local Monday contribute to the weekly session count.
- Unfinished sessions never enter the repository aggregate.
- All debug unit tests and debug APK assembly succeed.
