# Brand Logo Assets Design

## Goal

Show the supplied real brand logos in the existing brand-mark positions of the special-practice and question-bank-management UI, while retaining every displayed brand name as text and leaving all other layout and behavior intact.

## Confirmed requirements

- Treat `sucai/images` as the authoritative source directory and `sucai/汽车品牌车型列表(含logo).md` as the authoritative name-to-file mapping.
- Bundle the selected images into the offline APK; installed devices must not need the Windows source path.
- The current built-in question bank contains 40 brands. Each has a corresponding supplied image.
- `名爵` uses `上汽名爵（MG）.jpg`; `长城 / 哈弗` uses `长城哈弗（Great Wall Haval）.jpg`.
- In special practice and question-bank management, an actual logo replaces the current circular two-letter marker. The existing Chinese/English brand-name text stays visible.
- Custom brands have no supplied mapping and retain the existing text-in-circle fallback marker plus their name text.
- The card/list dimensions, two-column special-practice grid, navigation, editing, and brand selection behavior must not change.

## Architecture

Copy the supplied JPEGs into `app/src/main/res/drawable-nodpi` using lowercase Android-safe names such as `brand_tesla.jpg`. A new `BrandLogoRegistry` maps the stable seed-brand IDs (for example `tesla`, `mg`, and `great-wall-haval`) to drawable resource IDs. IDs are used rather than display names so bilingual text and custom user edits cannot break the lookup.

`BrandMark` is the only shared presentation point used by the special-practice cards, management lists, and selected-brand detail header. It will resolve a mapped resource and render it in the unchanged circular container with `ContentScale.Fit`; when no resource exists, it will render the existing initials fallback. Existing `Text(brand.displayText())` calls stay unchanged.

## Mapping and asset integrity

- Copy only files declared by the supplied Markdown mapping and used by a current built-in brand.
- Preserve the source JPEG content; do not crop, redraw, or convert the logos.
- Keep image density neutral with `drawable-nodpi` so all original aspect ratios are fitted inside the fixed mark dimensions.
- Add a test that requires every `SeedData.brands` entry to resolve through `BrandLogoRegistry` and verifies that an unknown/custom ID has no logo mapping.

## Acceptance checks

- All 40 current seed-brand IDs resolve to the expected bundled drawable.
- Custom brand IDs use the initials fallback without an exception.
- No `Text(brand.displayText())` call in the scoped screens is removed.
- `BrandMark` remains the common component for special practice and management, so both screens show consistent logos without altering their container sizes.
- `./gradlew.bat testDebugUnitTest assembleDebug` succeeds.
