# Random Practice Settings and Brand Card Design

## Goal

Let a user configure the number of questions for a random-practice round, remove the current-version vehicle-image UI, and make the brand-practice grid visually stable.

## Confirmed requirements

- The random-practice question count defaults to 10 and can be chosen from 5 through 50 in increments of 1.
- The random-practice card keeps a direct `开始练习` action. A separate adjacent settings action opens the count picker; starting a round immediately uses the currently saved value.
- The selected count persists after an app restart.
- Each random-practice question independently selects its question type. There is no longer any required 50/50 split between brand-to-model and model-to-brand questions.
- Brand-practice keeps its existing question-count behavior and has no count picker.
- This version must remove all vehicle-image interaction and presentation: no album picker, no image-copy routine, and no image controls in the add/edit-car dialog.
- Image fields must remain in the domain model, Room entity/table, repository API, and existing data. Existing image references are preserved for a future image feature; no schema migration and no file deletion are required.
- The brand-practice page remains a two-column grid without horizontal scrolling. Each card has a uniform 180dp height and equal width within the current screen. Text can use the compact existing typography and ellipsis rather than changing card geometry.

## Architecture

`RandomPracticeConfig` will centralize the allowed range and default so the UI and the pure quiz generator share one contract. `QuizGenerator` will accept a requested random count and sample unique `(question type, brand, car)` candidates, which allows odd counts and non-balanced type distributions while retaining four-option validation.

The Compose root will read and write the selected count using app-private `SharedPreferences`. The home random-practice card will display the active count, provide an adjacent settings control, and pass that count into round generation. No new dependency is required.

The existing image data path remains intact below the UI boundary. The management dialog will create a new vehicle with a null image reference and preserve a pre-existing reference when a vehicle is edited. Image-specific imports, state, Activity Result launcher, button, and file-copy helper will be removed from the UI file.

## Error handling

- A random requested count outside 5..50 fails fast in the generator with a clear message.
- A random question type is selected independently for every question. The generator uses unused candidates of that type first, then may reuse a type-specific candidate when that type's unique candidates are exhausted, so it never restores a fixed type split.
- The stored preference is clamped to 5..50 when read, protecting against stale or manually corrupted values.

## Testing and acceptance

- Unit tests prove valid odd and maximum random counts, unique question targets, four options, and one correct option per question.
- Unit tests prove configuration range validation and default behavior.
- The existing brand-practice tests continue to verify its unchanged behavior.
- The codebase builds and all debug unit tests pass with `./gradlew.bat testDebugUnitTest assembleDebug`.
- Manual Compose smoke check: the random card displays the saved count; the settings picker changes it; restarting the app retains it; the add/edit-car dialog contains no image action; two brand cards align to the same height.
