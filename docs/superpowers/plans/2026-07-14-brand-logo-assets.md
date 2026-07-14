# Brand Logo Assets Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bundle supplied brand-logo files into the offline APK and render them in every shared special-practice and management brand mark while retaining brand-name text.

**Architecture:** JPEGs from `sucai/images` become Android `drawable-nodpi` resources. `BrandLogoRegistry` maps stable `SeedData` IDs to resource IDs, and the shared `BrandMark` composable uses that registry with a custom-brand initials fallback.

**Tech Stack:** Kotlin 2.2.10, Jetpack Compose Material 3, Android resources, JUnit 4.

## Global Constraints

- Source assets and mappings are exclusively `sucai/images` and `sucai/汽车品牌车型列表(含logo).md`.
- Bundle images offline in `app/src/main/res/drawable-nodpi`; retain original JPEG content and aspect ratio.
- `名爵` maps to `上汽名爵（MG）.jpg`; `长城 / 哈弗` maps to `长城哈弗（Great Wall Haval）.jpg`.
- Keep brand-name text, special-grid geometry, management behavior, Room fields, and custom-brand text fallback unchanged.
- Do not stage or alter pre-existing unrelated changes.

---

### Task 1: Test the deterministic built-in-logo mapping

**Files:**
- Create: `app/src/main/java/com/example/carlogo/ui/BrandLogoRegistry.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/BrandLogoRegistryTest.kt`

**Interfaces:**
- Produces `BrandLogoRegistry.resourceIdFor(brandId: String): Int?`.
- `resourceIdFor` returns a drawable resource for every `SeedData.brands` ID and `null` for unknown/custom IDs.

- [ ] **Step 1: Write a failing mapping contract test**

```kotlin
@Test
fun `every bundled seed brand has a logo resource and custom brands do not`() {
    assertTrue(SeedData.brands.all { BrandLogoRegistry.resourceIdFor(it.id) != null })
    assertNull(BrandLogoRegistry.resourceIdFor("custom-user-brand"))
}
```

- [ ] **Step 2: Run the focused test and verify it fails because the registry is missing**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandLogoRegistryTest`

Expected: compilation failure for unresolved `BrandLogoRegistry`.

- [ ] **Step 3: Create the registry using exact seed-ID mappings**

Use a `when` expression with these exact ID/resource pairs:

```kotlin
"tesla" to R.drawable.brand_tesla; "byd" to R.drawable.brand_byd; "nio" to R.drawable.brand_nio
"xpeng" to R.drawable.brand_xpeng; "li-auto" to R.drawable.brand_li_auto; "zeekr" to R.drawable.brand_zeekr
"aion" to R.drawable.brand_aion; "im" to R.drawable.brand_im; "leapmotor" to R.drawable.brand_leapmotor
"neta" to R.drawable.brand_neta; "avatr" to R.drawable.brand_avatr; "deepal" to R.drawable.brand_deepal
"voyah" to R.drawable.brand_voyah; "arcfox" to R.drawable.brand_arcfox; "luxeed" to R.drawable.brand_luxeed
"bmw" to R.drawable.brand_bmw; "mercedes" to R.drawable.brand_mercedes; "audi" to R.drawable.brand_audi
"lexus" to R.drawable.brand_lexus; "volvo" to R.drawable.brand_volvo; "porsche" to R.drawable.brand_porsche
"cadillac" to R.drawable.brand_cadillac; "land-rover" to R.drawable.brand_land_rover; "hongqi" to R.drawable.brand_hongqi
"toyota" to R.drawable.brand_toyota; "volkswagen" to R.drawable.brand_volkswagen; "nissan" to R.drawable.brand_nissan
"honda" to R.drawable.brand_honda; "mazda" to R.drawable.brand_mazda; "mg" to R.drawable.brand_mg
"geely" to R.drawable.brand_geely; "changan" to R.drawable.brand_changan; "great-wall-haval" to R.drawable.brand_great_wall_haval
"chery" to R.drawable.brand_chery; "wuling" to R.drawable.brand_wuling; "aito" to R.drawable.brand_aito
"stelato" to R.drawable.brand_stelato; "maextro" to R.drawable.brand_maextro
```

- [ ] **Step 4: Re-run the focused test after Task 2 adds the referenced resources**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandLogoRegistryTest`

Expected: PASS.

### Task 2: Bundle the exact supplied JPEG assets

**Files:**
- Create: `app/src/main/res/drawable-nodpi/brand_*.jpg` (40 files)

- [ ] **Step 1: Copy each mapped source without transforming its bytes**

Use PowerShell `Copy-Item -LiteralPath` from `sucai/images` to `app/src/main/res/drawable-nodpi`, renaming only to the lower-case Android-safe `brand_<id>.jpg` names used in Task 1. Copy `上汽名爵（MG）.jpg` to `brand_mg.jpg` and `长城哈弗（Great Wall Haval）.jpg` to `brand_great_wall_haval.jpg`.

- [ ] **Step 2: Verify all assets are present and byte-identical to their source**

Run a PowerShell comparison of each source/destination SHA-256 hash.

Expected: 40 destination files and every source/destination hash pair matches.

- [ ] **Step 3: Run the registry test**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandLogoRegistryTest`

Expected: PASS.

### Task 3: Render mapped logos through the shared mark component

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`

**Interfaces:**
- `BrandMark(brand, modifier)` resolves `BrandLogoRegistry.resourceIdFor(brand.id)`.
- Built-in brands use `Image(painterResource(resourceId), contentScale = ContentScale.Fit)` inside the existing circular container.
- Unknown/custom brands retain `Text(brand.nameEn.take(2).uppercase())`.

- [ ] **Step 1: Keep the mapping test green before UI implementation**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.carlogo.ui.BrandLogoRegistryTest`

Expected: PASS.

- [ ] **Step 2: Implement the smallest shared display change**

Inside the existing `BrandMark` `Box`, resolve the resource ID once. When non-null, render the supplied logo with a content description based on `brand.displayText()`, `Modifier.fillMaxSize().padding(6.dp)`, and `ContentScale.Fit`; otherwise keep the current initials `Text`. Do not remove any `Text(brand.displayText())` call from `BrandCard`, `ManagementScreen`, `BrandDetailManagementScreen`, or its dialog.

- [ ] **Step 3: Build and search scoped invariants**

Run: `rg -n 'BrandMark\(|Text\(brand\.displayText\(\)' app/src/main/java/com/example/carlogo/ui/App.kt`

Expected: shared marker and brand-name text uses remain in special and management paths.

Run: `./gradlew.bat assembleDebug`

Expected: `BUILD SUCCESSFUL` and `app/build/outputs/apk/debug/app-debug.apk` exists.

### Task 4: Full verification

**Files:**
- Modify: `app/src/main/java/com/example/carlogo/ui/App.kt`
- Create: `app/src/main/java/com/example/carlogo/ui/BrandLogoRegistry.kt`
- Create: `app/src/test/java/com/example/carlogo/ui/BrandLogoRegistryTest.kt`
- Create: `app/src/main/res/drawable-nodpi/brand_*.jpg`

- [ ] **Step 1: Run the full build and test command**

Run: `./gradlew.bat testDebugUnitTest assembleDebug`

Expected: all debug unit tests pass and the debug APK assembles.

- [ ] **Step 2: Inspect asset and source integrity**

Run: `git diff --check -- app/src/main/java/com/example/carlogo/ui app/src/test/java/com/example/carlogo/ui app/src/main/res/drawable-nodpi`

Expected: no whitespace errors.

Run: `rg -n 'ContentScale.Fit|BrandLogoRegistry|Text\(brand\.displayText\(\)' app/src/main/java/com/example/carlogo/ui`

Expected: fitted logo rendering, registry lookup, and retained brand-name text are all present.
