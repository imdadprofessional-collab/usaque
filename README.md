# CDL Permit Prep USA

A premium, subscription-funded (no ads) CDL exam prep app for Android, built with Kotlin,
Jetpack Compose, and Clean Architecture / MVVM.

## Stack

Kotlin • Jetpack Compose • Material 3 • Hilt • Room + Paging 3 • DataStore • Firebase
(Auth, Firestore, Analytics, Crashlytics) • Google Play Billing v7 • WorkManager • Coroutines/Flow
• Navigation Compose • Coil • Timber.

## Architecture

```
presentation/   Compose screens + ViewModels (MVVM), one package per feature
domain/         Pure Kotlin: models, repository interfaces, use cases, AI interfaces
data/           Room DB, DAOs, Firebase, Billing, DataStore, repository implementations
di/             Hilt modules wiring data -> domain
```

- **Repository pattern + interfaces**: `domain/repository` defines contracts; `data/repository`
  implements them. ViewModels depend only on the interfaces, so the local Room-backed
  implementation can be swapped (e.g. a remote-backed one) without touching UI.
- **Question bank scales independently of memory**: `QuestionDao` exposes `PagingSource`
  queries (see `data/local/dao/QuestionDao.kt`) so the app never loads the full table into
  memory. It ships with a small seed set (`data/local/seed/SeedQuestionProvider.kt`) but the
  schema and indexes are designed for 10k/50k/100k+ rows — swap the seeder for a bulk
  CSV/JSON importer to scale up.
- **Firebase stores only small, per-user data** (profile, premium status, purchase history,
  bookmarks, streak, settings, exam history) — never the question bank itself.
- **AI is not wired into the UI directly.** `domain/ai/AiInterfaces.kt` defines
  `AnswerExplanationProvider`, `StudyPlanner`, `PersonalTutor`, and
  `QuestionRecommendationEngine`. Today's bindings (`di/AiModule.kt`) point at simple
  rule-based implementations (`data/ai/DefaultAiImplementations.kt`) so the app works fully
  offline; swap in an LLM-backed implementation later by changing only that module.

## Getting the project running

1. Open the project root in Android Studio (Koala+ recommended).
2. **Firebase**: `app/google-services.json` in this repo is a **placeholder**. Create a real
   Firebase project (Authentication + Firestore + Analytics + Crashlytics enabled), register
   the app with package name `com.cdlpermitprep.usa`, and replace `app/google-services.json`
   with the file Firebase gives you.
3. **Billing**: create the following products in Google Play Console once you're ready to test
   purchases (see `data/billing/BillingManager.kt` -> `BillingProducts`):
   - Subscriptions: `cdl_premium_monthly`, `cdl_premium_yearly`
   - In-app products: `cdl_premium_lifetime`, `pack_hazmat`, `pack_doubles_triples`,
     `pack_tanker`, `pack_passenger`
   - Purchases should additionally be verified server-side (e.g. a Cloud Function calling the
     Play Developer API) before granting entitlements for anything beyond a soft unlock —
     that backend piece isn't included here.
4. Build & run. On first launch the app seeds its local Room database from
   `SeedQuestionProvider` so it works fully offline immediately.

## Scaling the question bank

Replace/extend `SeedQuestionProvider` with a real import pipeline (e.g. read a CSV/JSON bundle
shipped as an asset, or fetched once and cached) that calls `QuestionDao.insertAll()` in
batches. The `QuestionEntity` schema (state, category, subCategory, difficulty, tags, etc.)
and its indexes are already shaped for large banks — nothing else in the app needs to change.

## Building an APK/AAB via GitHub Actions

`.github/workflows/android-build.yml` builds the app on every push and on demand
(`workflow_dispatch`) — no local Android Studio setup required:

1. Push to this branch (or open a PR to `main`/`master`), or trigger it manually from the
   **Actions** tab → **Android Build (APK & AAB)** → **Run workflow**.
2. The workflow builds a debug APK (`assembleDebug`) and a release AAB (`bundleRelease`) and
   uploads both as workflow artifacts — open the finished run and download
   `cdl-permit-prep-debug-apk` / `cdl-permit-prep-release-aab` from the **Artifacts** section.
3. The debug APK is auto-signed with the standard Android debug key, so it installs straight
   onto a device/emulator for testing.
4. The release AAB builds **unsigned** by default (safe: the build never fails for missing
   secrets). To get a signed AAB ready for Play Console, add these repo secrets
   (Settings → Secrets and variables → Actions):
   - `RELEASE_KEYSTORE_BASE64` — your upload keystore, base64-encoded (`base64 -w0 my.keystore`)
   - `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`

   Once all four are set, the same workflow run signs the AAB automatically (see the
   `hasReleaseSigningConfig` guard in `app/build.gradle.kts`).
5. Optionally add a `GOOGLE_SERVICES_JSON` secret (the full file contents) to build against
   your real Firebase project instead of the placeholder committed at
   `app/google-services.json`.

## Reusing this codebase for a different exam

Only two things are exam-specific: the seed data in `data/local/seed/SeedQuestionProvider.kt`
and branding (`app_name`, colors/theme, package name, Play Store assets). Swap those and the
rest of the architecture — Room schema, repositories, Billing, Firebase sync, navigation,
gamification, analytics — carries over unchanged.

## Notes on this build

This is a complete, compilable-by-design source tree (Gradle/AGP 8.5, Kotlin 1.9, Compose BOM
2024.06) generated in an environment without the Android SDK, so it has **not** been run
through `./gradlew assembleDebug` here. Before shipping, open it in Android Studio, sync
Gradle, and resolve any dependency-version bumps Android Studio suggests.
