# CDL Permit Prep USA

A premium, subscription-funded (no ads) CDL exam prep app for Android, built with Kotlin,
Jetpack Compose, and Clean Architecture / MVVM.

## Stack

Kotlin • Jetpack Compose • Material 3 • Hilt • Room + Paging 3 • DataStore • Firebase
(Auth, Firestore, Analytics, Crashlytics) • Google Play Billing v9 • WorkManager • Coroutines/Flow
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
  memory. It ships with ~500 original, hand-written questions bundled as
  `app/src/main/assets/questions_seed.json` and loaded by `AssetQuestionLoader` on first
  launch — the schema and indexes are designed for 10k/50k/100k+ rows, so growing the bank is
  purely a matter of adding more rows to that JSON (or swapping in a real bulk CSV/JSON
  import pipeline); nothing else in the app needs to change. See "Scaling the question bank"
  below.
- **Firebase stores only small, per-user data** (profile, premium status, purchase history,
  bookmarks, streak, settings, exam history) — never the question bank itself.
- **Premium packs, not just a single paywall**: each `QuestionEntity`/`Question` carries an
  optional `packId` (see `domain/model/Category.kt` -> `CategoryPacks`). Hazardous Materials,
  Tank Vehicles, Doubles/Triples, and Passenger/School Bus are gated categories that can be
  unlocked individually (`pack_hazmat`, `pack_tanker`, `pack_doubles_triples`,
  `pack_passenger`, one-time purchases) *or* all at once via any subscription/lifetime
  purchase. `BillingRepository.ownedPackIds()` + `premiumStatus()` drive this everywhere a
  question is fetched (`GetRandomQuestionsUseCase`) and everywhere a category is shown
  (`HomeViewModel.isCategoryUnlocked`), so a free user can never receive premium-pack
  questions through any practice mode, mock exam included.
- **AI is not wired into the UI directly.** `domain/ai/AiInterfaces.kt` defines
  `AnswerExplanationProvider`, `StudyPlanner`, `PersonalTutor`, and
  `QuestionRecommendationEngine`. Today's bindings (`di/AiModule.kt`) point at simple
  rule-based implementations (`data/ai/DefaultAiImplementations.kt`) so the app works fully
  offline; swap in an LLM-backed implementation later by changing only that module.

## Getting the project running

1. Open the project root in Android Studio (Koala+ recommended).
2. **Firebase**: `app/google-services.json` in this repo is a **placeholder**. Setting up the
   real project requires your Google account in a browser, so it can't be automated from here
   — walkthrough:
   1. Go to [console.firebase.google.com](https://console.firebase.google.com) → **Add
      project** → name it (e.g. "CDL Permit Prep USA") → keep Google Analytics enabled (the
      app links `firebase-analytics-ktx`).
   2. **Add app → Android**, twice, both in the *same* project:
      - Package name `com.cdlpermitprep.usa` (release)
      - Package name `com.cdlpermitprep.usa.debug` (debug — matches the
        `applicationIdSuffix` in `app/build.gradle.kts`; skipping this makes
        `processDebugGoogleServices` fail with "No matching client found")
      - SHA-1 is optional at this stage; if you want to add it now, the release upload key's
        fingerprint is `A8:47:A5:92:3A:B3:36:F4:91:60:77:8C:FB:98:42:F6:EC:70:39:2C`.
   3. Download **google-services.json** from Project settings → General → Your apps (one file
      covers both registered apps).
   4. **Build → Authentication → Get started → Sign-in method** → enable **Email/Password**
      (used by `LoginViewModel.signInWithEmail`/`registerWithEmail`) and **Anonymous** (used by
      "Continue as Guest", so even guest users get a Firebase identity their gamification/
      premium status can sync against later).
   5. **Build → Firestore Database → Create database** → production mode → pick a region →
      **Rules** tab → paste the contents of `firestore.rules` (already in this repo, scoped so
      a user can only read/write their own `users/{uid}` document, matching exactly what
      `UserRepositoryImpl.syncToCloud()` writes).
   6. **Build → Crashlytics → Enable** (it'll show "waiting for first crash" until the app
      actually runs with the real config — that's expected).
   7. Get the JSON into the build: either add it as the `GOOGLE_SERVICES_JSON` GitHub secret
      (Settings → Secrets and variables → Actions — the workflow already writes it over the
      placeholder before building, see step 5 under "Building an APK/AAB" below) so CI uses
      it, and/or replace `app/google-services.json` locally for Android Studio builds. Firebase
      client config isn't a traditional secret (access is enforced by the security rules
      above, not by hiding the file), so either approach is fine — the secret keeps it out of
      a public repo's history, which is the only real reason to prefer it here.
3. **Billing**: create the following products in Google Play Console once you're ready to test
   purchases (see `data/billing/BillingManager.kt` -> `BillingProducts`):
   - Subscriptions: `cdl_premium_monthly`, `cdl_premium_yearly`
   - In-app products: `cdl_premium_lifetime`, `pack_hazmat`, `pack_doubles_triples`,
     `pack_tanker`, `pack_passenger`
   - Purchases should additionally be verified server-side (e.g. a Cloud Function calling the
     Play Developer API) before granting entitlements for anything beyond a soft unlock —
     that backend piece isn't included here.
4. Build & run. On first launch the app seeds its local Room database from
   `app/src/main/assets/questions_seed.json` (loaded by `AssetQuestionLoader`), so it works
   fully offline immediately with 500+ questions across every category, all four premium packs (60-70 questions each), and 15 states.

## Scaling the question bank

The bank ships as `app/src/main/assets/questions_seed.json`, an array of objects matching
`QuestionEntity` (`state`, `category`, `subCategory`, `difficulty`, `question`, `optionA`-`D`,
`correctAnswer`, `explanation`, `tags`, `isPremium`, optional `packId`). To grow it:

- **Add more rows to that JSON** (or a generation script that produces it — see the shape of
  each entry) and rebuild; `AssetQuestionLoader` bulk-inserts everything in one pass via
  `QuestionDao.insertAll()`.
- For a genuinely large bank (10k-100k+), swap `AssetQuestionLoader` for a pipeline that reads
  a bundled/downloaded CSV or JSON in batches instead of one `assets` file — `QuestionEntity`'s
  schema and indexes are already shaped for that scale; nothing else in the app needs to change.
- `AssetQuestionLoader` falls back to the small hardcoded set in
  `data/local/seed/SeedQuestionProvider.kt` if the asset is ever missing or malformed, so the
  app never ships with zero offline content.

**On question accuracy**: the bundled questions are original content written from
publicly-known CDL safety/regulatory concepts (the source material — FMCSA and state DMV
commercial driver manuals — is U.S. government public domain). They have not been reviewed by
a CDL subject-matter expert or checked against current state-by-state statutes, so treat them
as a solid starting bank, not a substitute for your state's official manual before a real exam.

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

Only two things are exam-specific: the seed data in
`app/src/main/assets/questions_seed.json` and branding (`app_name`, colors/theme, package
name, Play Store assets). Swap those and the rest of the architecture — Room schema,
repositories, Billing (including the pack system), Firebase sync, navigation, gamification,
analytics — carries over unchanged.

## Notes on this build

This source tree builds successfully end-to-end via the GitHub Actions workflow in this repo
(`.github/workflows/android-build.yml` — `assembleDebug` + `bundleRelease` both green), using
Gradle 8.7/AGP 8.6, Kotlin 1.9, Compose BOM 2024.06. It was developed in an environment without a
local Android SDK, so local verification happened entirely through that CI workflow rather
than `./gradlew` on a dev machine — if you hit a version-resolution hiccup opening it in
Android Studio, let Android Studio's suggested upgrades resolve it.
