# Mistake Detail Design

## Goal

Turn the mistake book from a read-only list of internal identifiers into a readable, tappable explanation of each wrong question and its correct answer.

## Confirmed requirements

- The mistake book lists real brand/model names, a Chinese question-type label, the correct answer, and the wrong-count badge.
- Tapping a list item opens a dedicated mistake-detail page.
- The detail page shows the original semantic question, the correct answer, the question type, and cumulative wrong count.
- The feature is informational only. It must not add a retry button, mistake-practice mode, selected-mistake flow, or any practice action.
- Existing `MistakeEntity` fields (`brandId`, `carId`, `questionType`, `wrongCount`) are the only data source; no Room schema change or migration is required.
- If a saved mistake can no longer resolve to a brand or car because the custom source was deleted, the list shows it as unavailable instead of exposing raw IDs.

## Architecture

`MistakePresentationMapper` maps a `MistakeEntity` and the already loaded `Brand` list into a UI-safe presentation object. It converts the stored enum value into Chinese copy and builds the semantic question/correct answer. The mapper remains pure and is unit tested for both question directions and missing source data.

`MainShell` keeps the selected presentation while navigating between the mistake list and a new `mistakeDetail` page. The list and detail page use only Compose state and existing data; no persistence changes occur.

## Acceptance

- A `ModelToBrand` record for a car displays `看车型猜品牌`, asks which brand the car belongs to, and shows the brand as correct answer.
- A `BrandToModel` record displays `看品牌选车型`, asks which model belongs to the brand, and shows the car as correct answer.
- The list no longer renders raw strings such as `cadillac-4` or `ModelToBrand`.
- There is no retry/repractice control on either screen.
- Unit tests and debug APK assembly succeed.
